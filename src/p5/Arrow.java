package p5;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ListBox;
import controlP5.Textfield;

import java.io.Serializable;

import commands.Command;
import commands.deleteTransition;
import commands.modifyTransition;

import main.Functions;
import main.HistoryHandler;
import main.PFLAP;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Consts.notificationData.symbolInvalid;
import static main.Consts.notificationData.transitionInvalid;
import static main.Consts.transitionBezierCurve;
import static main.Consts.selfTransitionLength;

import static main.Functions.angleBetween;
import static main.Functions.testForLambda;

import static main.PFLAP.p;
import static main.PFLAP.cp5Font;
import static main.PFLAP.machine;

import static processing.core.PApplet.dist;
import static processing.core.PApplet.map;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.radians;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.abs;

import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

/**
 * Graphical representation of machine transitions
 * @author micycle1
 */
public class Arrow implements Serializable {

	private State tail, head;
	private transient ControlP5 cp5;
	private transient ListBox stateOptions;
	private transient ControlListener menuListener;
	private transient Textfield transitionSymbolEntry;
	private PVector midPoint, bezierCPoint, bezierApex, arrowTip, directTail, directHead;
	private PVector selfBezierCP1, selfBezierCP2, selfBezierTranslate, selfBezierTextLoc;
	private float theta1, theta2, arrowTipAngle, textSize = 16, selfTransitionAngle, selfBezierAngle,
			labelRotationOffset;
	private char transitionSymbol, stackPop;
	private String stackPush = "";
	private int ID, labelRotationModifier;
	private transient Command modifyBuffer;

	private static enum entryTypes {
		SYMBOL, POP, PUSH;
	}
	private entryTypes entryType = entryTypes.SYMBOL;

	private static enum arrowTypes {
		DIRECT, BEZIER, SELF;
	}
	private arrowTypes arrowType = arrowTypes.DIRECT;

	public Arrow(State tail, State head) {
		ID = this.hashCode();
		this.tail = tail;
		this.head = head;
		selfTransitionAngle = radians((PFLAP.arrows.size() * 50));
		initCP5();
		update(true);
	}

	public void initCP5() {
		cp5 = new ControlP5(p);
		cp5.setFont(cp5Font);
		cp5.show();
		// @formatter:off
		transitionSymbolEntry = cp5.addTextfield("Symbol Entry")
				.setColorLabel(0)
				.setLabel("")
				.setSize(30, 15)
				.setFocus(true)
				.addCallback(new CallbackListener() {
		// @formatter:on
					@Override
					public void controlEvent(CallbackEvent input) {
						if (input.getAction() == 100) {
							String tempSymbol = transitionSymbolEntry.getStringValue();
							if (tempSymbol.length() == 1 && entryType != entryTypes.PUSH) {
								entry(entryType);
							} else {
								if (entryType == entryTypes.PUSH && tempSymbol.length() > 0) {
									entry(entryType);
								} else {
									Notification.addNotification(symbolInvalid);
								}
							}
						}
					}
				});

		menuListener = new ControlListener() {
			@Override
			public void controlEvent(ControlEvent optionSelected) {
				switch ((int) optionSelected.getValue()) {
					case 0 : // Modify
						modifyBuffer = new modifyTransition(Arrow.this);
						entryType = entryTypes.SYMBOL;
						stateOptions.hide();
						transitionSymbolEntry.show();
						transitionSymbolEntry.setFocus(true);
						PFLAP.allowGUIInterraction = false;
						break;
					case 1 : // Delete
						HistoryHandler.buffer(new deleteTransition(Arrow.this));
						stateOptions.hide();
						break;
					default :
						break;
				}
			}

		};
		stateOptions = cp5.addListBox("          Options");
		// @formatter:off
		stateOptions.addItems(new String[]{"Modify Transition", "Delete Transition"})
			.open()
			.hide()
			.setWidth(140)
			.setBarHeight(35)
			.setColorLabel(Functions.invertColor(PFLAP.stateColour))
			.setItemHeight(25)
			.setColorValue(Functions.invertColor(PFLAP.stateColour))
			.setColorBackground(PFLAP.stateColour.getRGB())
			.setColorActive(PFLAP.stateColour.getRGB())
			.setColorForeground(Functions.darkenColor(PFLAP.stateColour, 0.33f)) // Mouseover
			.addListener(menuListener);
		// @formatter:on
	}

	/**
	 * Specifies behaviour when entering transition info.
	 */
	private void entry(entryTypes entryType) {
		transitionSymbolEntry.clear();
		switch (entryType) {
			case SYMBOL :
				transitionSymbol = testForLambda(transitionSymbolEntry.getStringValue().charAt(0));
				switch (PFLAP.mode) {
					case MOORE :
					case DFA :
						break;
					case DPA :
						this.entryType = entryTypes.POP;
						return;
					case MEALY :
						this.entryType = entryTypes.PUSH;
						return;
				}
				break;
			case POP :
				stackPop = testForLambda(transitionSymbolEntry.getStringValue().charAt(0));
				this.entryType = entryTypes.PUSH;
				return;
			case PUSH :
				stackPush = testForLambda(transitionSymbolEntry.getStringValue());
		}

		if (machine.testUniqueTransition(this, transitionSymbol, stackPop, stackPush)) {
			machine.addTransition(this);
			PFLAP.allowGUIInterraction = true;
			transitionSymbolEntry.hide();
			if (modifyBuffer != null) {
				HistoryHandler.buffer(modifyBuffer);
				modifyBuffer = null;
			}
		} else {
			Notification.addNotification(transitionInvalid);
			entryType = entryTypes.SYMBOL;
		}
	}

	/**
	 * Recalculates angle between connecting states.
	 * Called only when there is movement.
	 */
	public void update(boolean detectCycle) {
		if (detectCycle && detectCycles()) {
			arrowType = arrowTypes.BEZIER;
		} else {
			if (head.equals(tail)) {
				arrowType = arrowTypes.SELF;
			}
		}

		theta2 = angleBetween(tail.getPosition(), head.getPosition());
		theta1 = (theta2 + PI) % TWO_PI;
		if (Functions.numberBetween(theta2, PApplet.HALF_PI, 1.5 * PI)) {
			labelRotationModifier = 1;
			labelRotationOffset = theta1;
		} else {
			labelRotationModifier = -1;
			labelRotationOffset = theta2;
		}
		midPoint = new PVector((head.getPosition().x + tail.getPosition().x) / 2,
				(head.getPosition().y + tail.getPosition().y) / 2);
		
		switch (arrowType) {
			case BEZIER :
				textSize = map(PVector.dist(tail.getPosition(), head.getPosition()), 0, 200, 10, 16);
				bezierCPoint = new PVector(midPoint.x + (sin(-theta2) * (transitionBezierCurve * 2 * -1)),
						midPoint.y + (cos(-theta2) * (transitionBezierCurve * 2 * -1)));
				bezierApex = new PVector(midPoint.x - (bezierCPoint.x - midPoint.x),
						midPoint.y - (bezierCPoint.y - midPoint.y));
				arrowTipAngle = angleBetween(head.getPosition(), bezierApex);
				arrowTip = new PVector(head.getPosition().x + (head.getRadius() + 2) * -0.5f * cos(arrowTipAngle),
						head.getPosition().y + (head.getRadius() + 2) * -0.5f * sin(arrowTipAngle));
				transitionSymbolEntry.setPosition(bezierApex.x - transitionSymbolEntry.getWidth() / 2,
						bezierApex.y + 10);
				stateOptions.setPosition(bezierApex.x, bezierApex.y + 10);
				break;
			case DIRECT :
				textSize = map(PVector.dist(tail.getPosition(), head.getPosition()), 0, 200, 10, 18);
				directTail = new PVector(tail.getPosition().x + tail.getRadius() * -0.5f * cos(theta2),
						tail.getPosition().y + tail.getRadius() * -0.5f * sin(theta2));

				directHead = new PVector(head.getPosition().x + head.getRadius() * -0.5f * cos(theta1),
						head.getPosition().y + head.getRadius() * -0.5f * sin(theta1));
				transitionSymbolEntry.setPosition(midPoint.x - transitionSymbolEntry.getWidth() / 2, midPoint.y + 10);
				stateOptions.setPosition(midPoint.x, midPoint.y + 20);
				break;
			case SELF :
				textSize = 24;
				selfBezierCP1 = new PVector(head.getPosition().x + selfTransitionLength * sin(selfTransitionAngle),
						head.getPosition().y + selfTransitionLength * cos(selfTransitionAngle));
				selfBezierCP2 = new PVector(
						head.getPosition().x + selfTransitionLength * sin(selfTransitionAngle + radians(45)),
						head.getPosition().y + selfTransitionLength * cos(selfTransitionAngle + radians(45)));
				selfBezierTranslate = new PVector(
						head.getPosition().x + head.getRadius() / 2 * sin(selfTransitionAngle),
						head.getPosition().y + head.getRadius() / 2 * cos(selfTransitionAngle));
				selfBezierAngle = angleBetween(head.getPosition(), selfBezierCP1) - 0.3f;
				selfBezierTextLoc = new PVector(
						p.bezierPoint(head.getPosition().x, selfBezierCP1.x, selfBezierCP2.x, head.getPosition().x,
								0.5f) + 15 * sin(selfTransitionAngle),
						p.bezierPoint(head.getPosition().y, selfBezierCP1.y, selfBezierCP2.y, head.getPosition().y,
								0.5f) + 15 * cos(selfTransitionAngle));
				transitionSymbolEntry.setPosition(selfBezierTextLoc.x - transitionSymbolEntry.getWidth() / 2,
						selfBezierTextLoc.y + 10);
				stateOptions.setPosition(selfBezierTextLoc.x, selfBezierTextLoc.y + 10);
				break;
		}
	}

	/**
	 * Connected states call this upon their removal.
	 */
	protected void parentKill() {
		PFLAP.arrows.remove(this);
		machine.removeTransition(this);
	}

	public void kill() {
		head.childKill(this);
		tail.childKill(this);
		machine.removeTransition(this);
		PFLAP.arrows.remove(this);
		disableUI();
	}

	public void draw() {
		p.strokeWeight(2);
		switch (arrowType) {
			case DIRECT :
				p.line(directTail.x, directTail.y, directHead.x, directHead.y);
				drawArrowTip(directHead, theta1);
				p.pushMatrix();
				p.translate(directTail.x, directTail.y);
				p.rotate(labelRotationOffset);
				drawTransitionLabel(new PVector(
						labelRotationModifier * dist(directTail.x, directTail.y, directHead.x, directHead.y) / 2, 10));
				p.popMatrix();
				break;
			case BEZIER :
				p.noFill();
				p.curve(bezierCPoint.x, bezierCPoint.y, tail.getPosition().x, tail.getPosition().y,
						head.getPosition().x, head.getPosition().y, bezierCPoint.x, bezierCPoint.y);
				drawArrowTip(arrowTip, arrowTipAngle);
				drawTransitionLabel(bezierApex);
				break;
			case SELF :
				p.noFill();
				p.bezier(head.getPosition().x, head.getPosition().y, selfBezierCP1.x, selfBezierCP1.y, selfBezierCP2.x,
						selfBezierCP2.y, head.getPosition().x, head.getPosition().y);
				drawArrowTip(selfBezierTranslate, selfBezierAngle);
				drawTransitionLabel(selfBezierTextLoc);
				break;
		}
	}

	private void drawTransitionLabel(PVector pos) {
		p.fill(0);
		p.textSize(textSize);
		switch (PFLAP.mode) {
			case MOORE :
			case DFA :
				p.text(transitionSymbol, pos.x, pos.y);
				break;
			case DPA :
				p.text(transitionSymbol + " ; " + stackPop + "/" + stackPush, pos.x, pos.y);
				break;
			case MEALY :
				p.text(transitionSymbol + " ; " + stackPush, pos.x, pos.y);
				break;
		}
	}

	private void drawArrowTip(PVector translate, float angle) {
		p.noFill();
		p.strokeWeight(3);
		p.pushMatrix();
		p.translate(translate.x, translate.y);
		p.rotate(angle);
		p.beginShape();
		p.vertex(-10, -7);
		p.vertex(0, 0);
		p.vertex(-10, 7);
		p.endShape();
		p.popMatrix();
	}

	/**
	 * Naive approach.
	 * Detects loops in the machine, to draw bezier transitions when needed.
	 */
	private boolean detectCycles() {
		for (Arrow a : head.getOutgoingArrows()) {
			if (a.head == tail && !a.head.equals(a.tail)) {
				a.arrowType = arrowTypes.BEZIER;
				a.update(false);
				return true;
			}
		}
		return false;
	}

	public boolean isMouseOver(PVector mousePos) {
		switch (arrowType) {
			case BEZIER :
				return Functions.withinRange(bezierApex.x, bezierApex.y, textSize * 2, mousePos.x, mousePos.y)
						|| cp5.isMouseOver();
			case DIRECT :
				float dx = mousePos.x - midPoint.x;
				float dy = mousePos.y - midPoint.y;
				float nx = dx * cos(-theta2) - (dy * sin(-theta2));
				float ny = dy * cos(-theta2) + (dx * sin(-theta2));
				float dist = dist(tail.getPosition().x, tail.getPosition().y, head.getPosition().x,
						head.getPosition().y) / 2;
				boolean inside = (abs(nx) < dist * 2 / 2) && (abs(ny) < 10 / 2);
				return inside || Functions.withinRange(mousePos.x, mousePos.y, 20, midPoint.x, midPoint.y)
						|| cp5.isMouseOver();
			case SELF :
				return Functions.withinRange(selfBezierTextLoc.x, selfBezierTextLoc.y, textSize * 2, mousePos.x,
						mousePos.y) || cp5.isMouseOver();
			default :
				return false;
		}
	}

	public void hideUI() {
		cp5.hide();
	}

	public void showUI() {
		cp5.show();
		stateOptions.show();
		stateOptions.open();
	}

	public void enableUI() {
		cp5.show();
		cp5.setUpdate(true);
	}

	public void disableUI() {
		cp5.hide();
		cp5.setUpdate(false);
	}

	public State getHead() {
		return head;
	}

	public void setHead(State head) {
		this.head = head;
	}

	public State getTail() {
		return tail;
	}

	public void setTail(State tail) {
		this.tail = tail;
	}

	public char getSymbol() {
		return transitionSymbol;
	}

	public void setTransitionSymbol(char transitionSymbol) {
		this.transitionSymbol = transitionSymbol;
	}

	public char getStackPop() {
		return stackPop;
	}

	public void setStackPop(char stackPop) {
		this.stackPop = stackPop;
	}

	public String getStackPush() {
		return stackPush;
	}

	public void setStackPush(String stackPush) {
		this.stackPush = stackPush;
	}

	@Override
	public String toString() {
		return String.valueOf(ID) + tail.getLabel() + head.getLabel();
	}
}