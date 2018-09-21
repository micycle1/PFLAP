package p5;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;
import controlP5.Textfield;

import commands.Command;
import commands.deleteTransition;
import commands.modifyTransition;

import main.Functions;
import main.HistoryHandler;
import main.PFLAP;

import processing.core.PVector;

import static main.Consts.notificationData.symbolInvalid;
import static main.Consts.notificationData.transitionInvalid;
import static main.Consts.transitionBezierCurve;
import static main.Consts.selfTransitionLength;

import static main.Functions.angleBetween;
import static main.Functions.numberBetween;
import static main.Functions.testForLambda;

import static main.PFLAP.p;
import static main.PFLAP.machine;

import static processing.core.PApplet.dist;
import static processing.core.PApplet.map;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.radians;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.abs;

import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

import java.util.ArrayList;

import static processing.core.PConstants.HALF_PI;

/**
 * Graphical representation of machine transitions
 * @author micycle1
 */
public class Arrow {

	private State tail, head;
	private ControlP5 cp5;
	private ScrollableList stateOptions;
	private ControlListener menuListener;
	private PVector tailXY, headXY, midPoint, bezierCPoint, bezierApex, arrowTip;
	private PVector selfBezierCP1, selfBezierCP2, selfBezierTranslate, selfBezierTextLoc;
	private float rotationOffset, theta1, theta2, arrowTipAngle, textSize = 16, selfTransitionAngle, selfBezierAngle;
	private Textfield transitionSymbolEntry;
	private char transitionSymbol, stackPop;
	private String stackPush = "";
	private int ID, labelRotationModifier = -1;
	private Command modifyBuffer;

	private static enum entryTypes {
		SYMBOL, POP, PUSH;
	}
	private entryTypes entryType = entryTypes.SYMBOL;

	private static enum arrowTypes {
		DIRECT, BEZIER, SELF;
	}
	private arrowTypes arrowType = arrowTypes.DIRECT;

	public Arrow(PVector startXY, State tail) {
		// Constructor for creating transition
		ID = this.hashCode();
		this.tail = tail;
		tailXY = startXY;
		headXY = tailXY;
	}

	public Arrow(State tail, State head) {
		// Constructor for finalised transitions
		ID = this.hashCode();
		this.tail = tail;
		this.head = head;
		selfTransitionAngle = radians((PFLAP.arrows.size() * 50));
		tailXY = tail.getPosition();
		headXY = head.getPosition();
		initCP5();
		update();
	}

	@Deprecated
	public Arrow(State tail, State head, char transitionSymbol) {
		ID = this.hashCode();
		this.tail = tail;
		this.head = head;
		selfTransitionAngle = radians((PFLAP.arrows.size() * 50));
		tailXY = tail.getPosition();
		headXY = head.getPosition();
		this.transitionSymbol = transitionSymbol;
		update();
	}

	public void initCP5() {
		cp5 = new ControlP5(p);
		cp5.show();
		// @formatter:off
		transitionSymbolEntry = cp5.addTextfield("Symbol Entry")
				.setColorLabel(0)
				.setLabel("")
				.keepFocus(true)
				.setSize(30, 15)
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
		stateOptions = cp5.addScrollableList("Options");
		// @formatter:off
		stateOptions.setType(1)
		.addItems(new String[]{"Modify Transition", "Delete Transition"})
		.open()
		.hide()
		.addListener(menuListener);
		// @formatter:on
	}

	private void entry(entryTypes entryType) {
		transitionSymbolEntry.clear();
		switch (entryType) {
			case SYMBOL :
				transitionSymbol = testForLambda(transitionSymbolEntry.getStringValue().charAt(0));
				if (PFLAP.mode == PFLAP.modes.DPA) {
					this.entryType = entryTypes.POP;
				} else {
					if (!testUniqueDFATransition(transitionSymbol)) {
						Notification.addNotification(transitionInvalid);
						return;
					}
					machine.addTransition(this);
					PFLAP.allowGUIInterraction = true;
					transitionSymbolEntry.hide();
				}
				break;
			case POP :
				stackPop = testForLambda(transitionSymbolEntry.getStringValue().charAt(0));
				this.entryType = entryTypes.PUSH;
				break;
			case PUSH :
				stackPush = testForLambda(transitionSymbolEntry.getStringValue());
				if (!testUniqueDPATransition(stackPush)) {
					Notification.addNotification(transitionInvalid);
					return;
				} else {
					machine.addTransition(this);
					PFLAP.allowGUIInterraction = true;
					transitionSymbolEntry.hide();
				}
				break;
			default :
				break;
		}
		if (modifyBuffer != null) {
			HistoryHandler.buffer(modifyBuffer);
			modifyBuffer = null;
		}
	}

	/**
	 * Recalculates angle between connecting states. Called only when there is
	 * movement.
	 */
	public void update() {
		theta2 = angleBetween(tail.getPosition(), head.getPosition());
		tailXY = new PVector(tail.getPosition().x + tail.getRadius() * -0.5f * cos(theta2),
				tail.getPosition().y + tail.getRadius() * -0.5f * sin(theta2));

		theta1 = (theta2 + PI) % TWO_PI;
		headXY = new PVector(head.getPosition().x + head.getRadius() * -0.5f * cos(theta1),
				head.getPosition().y + head.getRadius() * -0.5f * sin(theta1));

		midPoint = new PVector((head.getPosition().x + tail.getPosition().x) / 2,
				(head.getPosition().y + tail.getPosition().y) / 2);

		bezierCPoint = new PVector(midPoint.x + (sin(-theta2) * (transitionBezierCurve * 2 * -1)),
				midPoint.y + (cos(-theta2) * (transitionBezierCurve * 2 * -1)));

		bezierApex = new PVector(midPoint.x - (bezierCPoint.x - midPoint.x),
				midPoint.y - (bezierCPoint.y - midPoint.y));

		arrowTipAngle = angleBetween(head.getPosition(), bezierApex);
		arrowTip = new PVector(head.getPosition().x + (head.getRadius() + 2) * -0.5f * cos(arrowTipAngle),
				head.getPosition().y + (head.getRadius() + 2) * -0.5f * sin(arrowTipAngle)); // +2

		textSize = map(PVector.dist(tailXY, headXY), 0, 200, 10, 16);

		selfBezierCP1 = new PVector(head.getPosition().x + selfTransitionLength * sin(selfTransitionAngle),
				head.getPosition().y + selfTransitionLength * cos(selfTransitionAngle));
		selfBezierCP2 = new PVector(
				head.getPosition().x + selfTransitionLength * sin(selfTransitionAngle + radians(45)),
				head.getPosition().y + selfTransitionLength * cos(selfTransitionAngle + radians(45)));
		selfBezierTranslate = new PVector(head.getPosition().x + head.getRadius() / 2 * sin(selfTransitionAngle),
				head.getPosition().y + head.getRadius() / 2 * cos(selfTransitionAngle));
		selfBezierAngle = angleBetween(head.getPosition(), selfBezierCP1) - 0.3f;
		selfBezierTextLoc = new PVector(
				p.bezierPoint(head.getPosition().x, selfBezierCP1.x, selfBezierCP2.x, head.getPosition().x, 0.5f)
						+ 15 * sin(selfTransitionAngle),
				p.bezierPoint(head.getPosition().y, selfBezierCP1.y, selfBezierCP2.y, head.getPosition().y, 0.5f)
						+ 15 * cos(selfTransitionAngle));
		if (numberBetween(theta2, HALF_PI, 1.5 * PI)) {
			labelRotationModifier = 1;
			rotationOffset = theta1;
		} else {
			labelRotationModifier = -1;
			rotationOffset = theta2;
		}

		if (detectCycles()) {
			arrowType = arrowTypes.BEZIER;
		} else {
			if (head.equals(tail)) {
				arrowType = arrowTypes.SELF;
			}
		}

		transitionSymbolEntry.setPosition(midPoint.x - transitionSymbolEntry.getWidth() / 2, midPoint.y - 10);
		stateOptions.setPosition(midPoint.x, midPoint.y);
	}

	public void tempUpdate() {
		// Called when arrow is drawn to rotate arrow head properly
		theta2 = angleBetween(tail.getPosition(), new PVector(p.mouseX, p.mouseY));
		theta1 = (theta2 + PI) % TWO_PI;
	}

	protected void parentKill() {
		// States call this.
		PFLAP.arrows.remove(this);
		machine.removeTransition(this);
		// remove references to this in states and machine
	}

	public void kill() {
		head.childKill(this);
		tail.childKill(this);
		machine.removeTransition(this);
		PFLAP.arrows.remove(this);
		disableUI();
	}

	public void draw() {
		p.textSize(textSize);
		switch (arrowType) {
			case DIRECT :
				p.line(tailXY.x, tailXY.y, headXY.x, headXY.y);
				drawArrowTip(headXY, theta1);
				p.pushMatrix();
				p.translate(tailXY.x, tailXY.y);
				p.rotate(rotationOffset);
				p.fill(0);
				switch (PFLAP.mode) {
					case DFA :
						p.text(transitionSymbol,
								labelRotationModifier * dist(tailXY.x, tailXY.y, headXY.x, headXY.y) / 2, 10);
						break;
					case DPA :
						p.text(transitionSymbol + "; " + stackPop + "/" + stackPush,
								+labelRotationModifier * dist(tailXY.x, tailXY.y, headXY.x, headXY.y) / 2, 10);
						break;
					case MEALY :
						break;
					case MOORE :
						break;
					default :
						break;
				}
				p.popMatrix();
				break;
			case BEZIER :
				p.noFill();
				p.curve(bezierCPoint.x, bezierCPoint.y, tail.getPosition().x, tail.getPosition().y,
						head.getPosition().x, head.getPosition().y, bezierCPoint.x, bezierCPoint.y);
				drawArrowTip(arrowTip, arrowTipAngle);
				p.fill(0);
				p.text(transitionSymbol, bezierApex.x, bezierApex.y);
				switch (PFLAP.mode) {
					case DFA :
						p.text(transitionSymbol, bezierApex.x, bezierApex.y);
						break;
					case DPA :
						p.text(transitionSymbol + "; " + stackPop + "/" + stackPush, bezierApex.x, bezierApex.y);
						break;
					case MEALY :
						break;
					case MOORE :
						break;
					default :
						break;
				}
				break;
			case SELF :
				p.noFill();
				p.bezier(head.getPosition().x, head.getPosition().y, selfBezierCP1.x, selfBezierCP1.y, selfBezierCP2.x,
						selfBezierCP2.y, head.getPosition().x, head.getPosition().y);
				drawArrowTip(selfBezierTranslate, selfBezierAngle);
				p.fill(0);
				switch (PFLAP.mode) {
					case DFA :
						p.text(transitionSymbol, selfBezierTextLoc.x, selfBezierTextLoc.y);
						break;
					case DPA :
						p.text(transitionSymbol + "; " + stackPop + "/" + stackPush, selfBezierTextLoc.x,
								selfBezierTextLoc.y);
						break;
					case MEALY :
						break;
					case MOORE :
						break;
					default :
						break;
				}
				break;
		}
	}

	private void drawArrowTip(PVector translate, float angle) {
		p.noFill();
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
				return true;
			}
		}
		return false;
	}

	private boolean testUniqueDFATransition(char symbol) {
		ArrayList<Arrow> arrows = new ArrayList<>(tail.getOutgoingArrows());
		arrows.remove(this);
		for (Arrow a : arrows) {
			if (a.transitionSymbol == symbol) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks proposed DPA transition for uniqueness of all 3 transition attributes.
	 * Call this check when the final attribute, stackPush, is entered by user.
	 * @param push Proposed stackPush symbols
	 * @return true if unique
	 */
	private boolean testUniqueDPATransition(String push) {
		ArrayList<Arrow> arrows = new ArrayList<>(tail.getOutgoingArrows());
		arrows.remove(this);
		for (Arrow a : arrows) {
			if (a.transitionSymbol == transitionSymbol && a.stackPop == stackPop && a.stackPush.equals(push)) {
				return false;
			}
		}
		return true;
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
				float dist = dist(tailXY.x, tailXY.y, headXY.x, headXY.y) / 2;
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

	public PVector getTailXY() {
		return tailXY;
	}

	public void setHeadXY(PVector headXY) {
		this.headXY = headXY;
		rotationOffset = angleBetween(tailXY, headXY);
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
		// + setters for when modifying transitions
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