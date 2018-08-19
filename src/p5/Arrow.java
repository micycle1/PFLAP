package p5;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;
import controlP5.Textfield;

import commands.deleteTransition;

import main.Functions;
import main.HistoryHandler;
import main.PFLAP;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import static main.Consts.notificationData.symbolNotValid;
import static main.Consts.transitionBezierCurve;
import static main.Consts.selfTransitionLength;

import static main.Functions.angleBetween;
import static main.Functions.numberBetween;

import static main.PFLAP.p;
import static main.PFLAP.machine;

import static java.lang.Math.PI;

import static processing.core.PApplet.dist;
import static processing.core.PApplet.map;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.*;

public class Arrow {

	private State tail, head;
	private ControlP5 cp5;
	private ScrollableList stateOptions;
	private ControlListener menuListener;
	private PVector tailXY, headXY, midPoint, bezierCPoint, bezierApex, arrowTip;
	private PVector selfBezierCP1, selfBezierCP2, selfBezierTranslate, selfBezierTextLoc;
	private float rotationOffset, theta1, theta2, arrowTipAngle, textSize = 16, selfTransitionAngle, selfBezierAngle;
	private Textfield transitionSymbolEntry;
	private char transitionSymbol, stackPop, stackPush;
	private int ID, labelRotationModifier = -1;

	private static int bezierDir = -1;

	private static enum entryTypes {
		SYMBOL, POP, PUSH
	}
	private entryTypes entryType = entryTypes.SYMBOL;

	public Arrow(PVector startXY, State tail) {
		// Constructor for creating transition
		// PFLAP.allowGUIInterraction = false;
		ID = this.hashCode();
		this.tail = tail;
		tailXY = startXY;
		headXY = tailXY;

	}

	public Arrow(State tail, State head) {
		// Constructor for finalised transtions
		// PFLAP.allowGUIInterraction = false;
		ID = this.hashCode();
		this.tail = tail;
		this.head = head;
		selfTransitionAngle = radians((PFLAP.arrows.size() * 20));
		tailXY = tail.getPosition();
		headXY = head.getPosition();
		initCP5();
		update();
	}

	public Arrow(State tail, State head, char transitionSymbol) {
		ID = this.hashCode();
		this.tail = tail;
		this.head = head;
		selfTransitionAngle = radians((PFLAP.arrows.size() * 20));
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
							if (transitionSymbolEntry.getStringValue().length() == 1) {
								// TODO && transition has unique symbol
								entry();
							} else {
								Notification.addNotification(symbolNotValid);
							}
						}
					}
				});

		menuListener = new ControlListener() {
			@Override
			public void controlEvent(ControlEvent optionSelected) {
				switch ((int) optionSelected.getValue()) {
					case 0 :
						// TODO CHANGE TRANSITION SYMBOL + COMMAND?
						machine.removeTransition(Arrow.this);
						stateOptions.hide();
						transitionSymbolEntry.show();
						PFLAP.allowGUIInterraction = false;
						transitionSymbolEntry.unlock();
						transitionSymbolEntry.setFocus(true);
						break;
					case 1 :
						HistoryHandler.buffer(new deleteTransition(Arrow.this));
						break;
					default :
						break;
				}
			}

		};
		stateOptions = cp5.addScrollableList("Options");
		// @formatter:off
		stateOptions.setType(1)
		.addItems(new String[]{"Change Symbol", "Delete Transition"})
		.open()
		.hide()
		.addListener(menuListener);
		// @formatter:on
	}

	private void entry() {
		transitionSymbolEntry.clear();
		switch (entryType) {
			case SYMBOL :
				transitionSymbol = transitionSymbolEntry.getStringValue().charAt(0);
				if (PFLAP.mode == PFLAP.modes.DPA) {
					entryType = entryTypes.POP;
				} else {
					machine.addTransition(this); // don't add here if dpa
					PFLAP.allowGUIInterraction = true;
				}
				break;
			case POP :
				stackPop = transitionSymbolEntry.getStringValue().charAt(0);
				entryType = entryTypes.PUSH;
				break;
			case PUSH :
				stackPush = transitionSymbolEntry.getStringValue().charAt(0);
				machine.addTransition(this);
				PFLAP.allowGUIInterraction = true;
				break;
			default :
				break;
		}
		transitionSymbolEntry.hide();
		transitionSymbolEntry.lock();
	}

	/**
	 * Recalculates angle between connecting states. Called only when there is
	 * movement.
	 */
	public void update() {
		theta2 = angleBetween(tail.getPosition(), head.getPosition());
		tailXY = new PVector(tail.getPosition().x + tail.getRadius() * -0.5f * cos(theta2),
				tail.getPosition().y + tail.getRadius() * -0.5f * sin(theta2));

		theta1 = (theta2 + PConstants.PI) % PConstants.TWO_PI;
		headXY = new PVector(head.getPosition().x + head.getRadius() * -0.5f * cos(theta1),
				head.getPosition().y + head.getRadius() * -0.5f * sin(theta1));

		midPoint = new PVector((head.getPosition().x + tail.getPosition().x) / 2,
				(head.getPosition().y + tail.getPosition().y) / 2);

		bezierCPoint = new PVector(midPoint.x + (PApplet.sin(-theta2) * (transitionBezierCurve * 2 * -1)),
				midPoint.y + (PApplet.cos(-theta2) * (transitionBezierCurve * 2 * bezierDir)));

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
		if (numberBetween(theta2, PConstants.HALF_PI, 1.5 * PI)) { // TODO
																	// change
			labelRotationModifier = 1;
			rotationOffset = theta1;
		} else {
			labelRotationModifier = -1;
			rotationOffset = theta2;
		}

		// Update cp5:
		if (transitionSymbol == '\u0000') {
			transitionSymbolEntry.setPosition((headXY.x + tailXY.x) / 2, (headXY.y + tailXY.y) / 2); // TODO
			stateOptions.setPosition(headXY.x - dist(tailXY.x, tailXY.y, headXY.x, headXY.y) / 2,
					(PApplet.abs(headXY.y) + PApplet.abs(tailXY.y)) / 2 + 7); // TODO
		}

	}

	public void tempUpdate() {
		// Called when arrow is drawn to rotate arrow head properly
		theta2 = angleBetween(tail.getPosition(), new PVector(p.mouseX, p.mouseY));
		theta1 = (theta2 + PConstants.PI) % PConstants.TWO_PI;
	}

	protected void parentKill() {
		// States call this.
		PFLAP.arrows.remove(this);
		// remove references to this in states and machine
	}

	public void kill() {
		head.childKill(this);
		tail.childKill(this);
		machine.removeTransition(this);
		PFLAP.arrows.remove(this);
	}

	public void draw() {
		p.noFill();
		p.textAlign(CENTER, CENTER);
		if (head == tail) {
			p.bezier(head.getPosition().x, head.getPosition().y, selfBezierCP1.x, selfBezierCP1.y, selfBezierCP2.x,
					selfBezierCP2.y, head.getPosition().x, head.getPosition().y);
			drawArrowTip(selfBezierTranslate, selfBezierAngle);
			p.text(transitionSymbol, selfBezierTextLoc.x, selfBezierTextLoc.y);
		} else {
			if (head != null && head.connectedTailCount() > 1) { // TODO
				p.curve(bezierCPoint.x, bezierCPoint.y, tail.getPosition().x, tail.getPosition().y,
						head.getPosition().x, head.getPosition().y, bezierCPoint.x, bezierCPoint.y);
				drawArrowTip(arrowTip, arrowTipAngle);
			} else {
				p.line(tailXY.x, tailXY.y, headXY.x, headXY.y);
				drawArrowTip(headXY, theta1);
			}
		}

		p.pushMatrix();
		p.translate(tailXY.x, tailXY.y);
		p.rotate(rotationOffset);
		p.textSize(textSize);
		switch (PFLAP.mode) {
			case DFA :
				p.text(transitionSymbol, labelRotationModifier * dist(tailXY.x, tailXY.y, headXY.x, headXY.y) / 2, 7);
				break;
			case DPA :
				p.text(transitionSymbol + "; " + stackPop + "/" + stackPush,
						+labelRotationModifier * dist(tailXY.x, tailXY.y, headXY.x, headXY.y) / 2, 7);
				break;
			case MEALY :
				break;
			case MOORE :
				break;
			default :
				break;
		}
		p.popMatrix();
	}

	private void drawArrowTip(PVector translate, float angle) {
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

	public boolean isMouseOver(PVector mousePos) {
		float dx = mousePos.x - midPoint.x;
		float dy = mousePos.y - midPoint.y;
		float nx = dx * cos(-theta2) - (dy * sin(-theta2));
		float ny = dy * cos(-theta2) + (dx * sin(-theta2));
		float dist = dist(tailXY.x, tailXY.y, headXY.x, headXY.y) / 2;
		boolean inside = (PApplet.abs(nx) < dist * 2 / 2) && (PApplet.abs(ny) < 10 / 2);
		return inside || Functions.withinRange(mousePos.x, mousePos.y, 20, midPoint.x, midPoint.y) || cp5.isMouseOver();
	}

	public void hideUI() {
		cp5.hide();
	}

	public void showUI() {
		cp5.show();
		stateOptions.show();
		stateOptions.open();

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

	public char getStackPop() {
		// + setters for when modifying transitions
		return stackPop;
	}

	public char getStackPush() {
		return stackPush;
	}

	@Override
	public String toString() {
		return String.valueOf(ID) + tail.getLabel() + head.getLabel();
	}
}