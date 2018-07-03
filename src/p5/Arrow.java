package p5;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Textfield;

import machines.DFA;
import machines.DPA;

import main.PFLAP;

import processing.core.PVector;

import static main.Consts.stateRadius;
import static main.Consts.notificationData.symbolNotValid;

import static main.Functions.angleBetween;
import static main.Functions.numberBetween;

import static main.PFLAP.p;
import static main.PFLAP.PI;

import static processing.core.PApplet.dist;
import static processing.core.PApplet.map;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;

public class Arrow {

	private State tail, head;
	private PVector tailXY, headXY;
	private float rotationOffset, theta1, theta2, textSize = 16;
	private Textfield transitionSymbolEntry;
	private char transitionSymbol = ' ', stackPop, stackPush;
	private int ID, labelRotationModifier = -1;

	private static enum entryTypes {
		SYMBOL, POP, PUSH
	}
	private entryTypes entryType = entryTypes.SYMBOL;

	public Arrow(PVector startXY, State tail) {
		// PFLAP.allowGUIInterraction = false;
		ID = this.hashCode();
		this.tail = tail;
		tailXY = startXY;
		headXY = tailXY;
		// @formatter:off
		transitionSymbolEntry = PFLAP.cp5.addTextfield(String.valueOf(ID)) //make static?
				.setColorLabel(0)
				.setLabel("")
				.hide()
				.keepFocus(true)
				.setSize(30, 15)
				.addCallback(new CallbackListener() {
					// @formatter:on
					@Override
					public void controlEvent(CallbackEvent input) {
						if (transitionSymbolEntry.getStringValue().length() == 1) { // TODO
																					// &&
																					// transition
																					// has
																					// unique
																					// symbol
							entry();
						} else {
							Notification.addNotification(symbolNotValid);
						}
					}
				});
	}

	private void entry() {
		transitionSymbolEntry.clear();
		switch (entryType) {
			case SYMBOL :
				transitionSymbol = transitionSymbolEntry.getStringValue().charAt(0);

				if (PFLAP.mode == PFLAP.modes.DPA) {
					entryType = entryTypes.POP;
				} else {
					// DFA //todo switch
					DFA.addTransition(tail, head, transitionSymbol);
					PFLAP.cp5.remove(String.valueOf(ID));
					PFLAP.allowGUIInterraction = true;
				}
				break;
			case POP :
				stackPop = transitionSymbolEntry.getStringValue().charAt(0);
				entryType = entryTypes.PUSH;
				break;
			case PUSH :
				stackPush = transitionSymbolEntry.getStringValue().charAt(0);
				DPA.addTransition(tail, head, transitionSymbol, stackPop, stackPush);
				PFLAP.cp5.remove(String.valueOf(ID));
				PFLAP.allowGUIInterraction = true;
				break;
			default :
				break;
		}
	}

	/**
	 * Recalculates angle between connecting states. Called only when there is
	 * movement.
	 */
	public void update() {
		theta1 = angleBetween(head.getPosition(), tail.getPosition());
		headXY = new PVector(head.getPosition().x + stateRadius * 0.5f * cos(theta1),
				head.getPosition().y + stateRadius * 0.5f * sin(theta1));

		theta2 = angleBetween(tail.getPosition(), head.getPosition());
		tailXY = new PVector(tail.getPosition().x + stateRadius * 0.5f * cos(theta2),
				tail.getPosition().y + stateRadius * 0.5f * sin(theta2));
		
		textSize = map(PVector.dist(tailXY, headXY), 0, 200, 10, 16);

		rotationOffset = theta2;
		transitionSymbolEntry.setPosition((headXY.x + tailXY.x) / 2, (headXY.y + tailXY.y) / 2).show().setFocus(true); // reposition

		if (numberBetween(theta2, 0.5f * PI, -0.5f * PI)) {
			labelRotationModifier = -1;
			rotationOffset = theta2;
		} else {
			labelRotationModifier = 1;
			rotationOffset = theta1;
		}
	}

	public void kill() {
		PFLAP.cp5.remove(String.valueOf(ID));
		PFLAP.arrows.remove(this);
	}

	public void draw() {
		p.line(tailXY.x, tailXY.y, headXY.x, headXY.y);

		// p.text(transitionSymbol, (tailXY.x + headXY.x) / 2, (tailXY.y +
		// headXY.y) / 2);

		p.noFill();
		p.bezier(tailXY.x, tailXY.y, tailXY.x+65, tailXY.y-45, tailXY.x+65, tailXY.y+45, tailXY.x, tailXY.y); //RANDOM POINT  ON SPHERE
		p.pushMatrix();

		p.translate(headXY.x, headXY.y);
		p.rotate(rotationOffset);
		p.textSize(textSize);
		switch (PFLAP.mode) {
			case DFA :
				p.text(transitionSymbol, + labelRotationModifier*dist(tailXY.x, tailXY.y, headXY.x, headXY.y)/2, 7);
				break;
			case DPA :
				p.text(transitionSymbol + "; "+ stackPop +"/"+stackPush, + labelRotationModifier*dist(tailXY.x, tailXY.y, headXY.x, headXY.y)/2, 7); //- to +
				break;
		}
		p.popMatrix();
		
		p.pushMatrix();
		p.translate(headXY.x, headXY.y);
		p.rotate(theta2);
		p.beginShape();
		p.vertex(-10, -7);
		p.vertex(0, 0);
		p.vertex(-10, 7);
		p.endShape();
		p.popMatrix();
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
		// + setters for when modiying transitions
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