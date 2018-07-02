package p5;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Textfield;

import machines.DFA;
import machines.DPA;

import main.Consts;
import main.Functions;
import main.PFLAP;

import processing.core.PApplet;
import processing.core.PVector;

public class Arrow {

	public static PApplet p;
	private State tail, head;
	private PVector tailXY, headXY;
	private float rotationOffset;
	private Textfield transitionSymbolEntry;
	private char transitionSymbol = ' ', stackPop, stackPush;
	private int ID;
	
	private static enum entryTypes { 
		SYMBOL, POP, PUSH
	}
	private entryTypes entryType = entryTypes.SYMBOL;

	public Arrow(PVector startXY, State tail) {
		PFLAP.allowNewArrow = false;
		ID = this.hashCode();
		this.tail = tail;
		tailXY = startXY;
		headXY = tailXY;
		// @formatter:off
		transitionSymbolEntry = PFLAP.cp5.addTextfield(String.valueOf(ID)) //make static?
				.setColorLabel(0)
				.setLabel("")
				.hide()
				.setSize(30, 15)
				.addCallback(new CallbackListener() {
					// @formatter:on
					@Override
					public void controlEvent(CallbackEvent input) {
						if (transitionSymbolEntry.getStringValue().length() == 1) { //TODO && transition has unique symbol
							entry();
						} else {
							// notification object make!!!
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
				}
				else {
					//DFA //todo switch
					DFA.addTransition(tail, head, transitionSymbol);
					PFLAP.cp5.remove(String.valueOf(ID));
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
				PFLAP.allowNewArrow = true;
				break;
			default :
				break;
		}
	}

	public void update() {
		float theta1 = Functions.angleBetween(head.getPosition(), tail.getPosition());
		headXY = new PVector(head.getPosition().x + Consts.stateRadius * 0.5f * PApplet.cos(theta1),
				head.getPosition().y + Consts.stateRadius * 0.5f * PApplet.sin(theta1));

		float theta2 = Functions.angleBetween(tail.getPosition(), head.getPosition());
		tailXY = new PVector(tail.getPosition().x + Consts.stateRadius * 0.5f * PApplet.cos(theta2),
				tail.getPosition().y + Consts.stateRadius * 0.5f * PApplet.sin(theta2));
		rotationOffset = theta2;
		transitionSymbolEntry.setPosition((headXY.x + tailXY.x) / 2, (headXY.y + tailXY.y) / 2).show().setFocus(true);

		
//		if ( (-3/2*PApplet.PI)<=theta2 &&  (-1/2*PApplet.PI)>=theta2) {
//			rotationOffset = theta1;
//		}
		//if (p.frameCount % 5 == 0)p.println(theta1,theta2, "|", rotationOffset);
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
		switch (PFLAP.mode) {
			case DFA :
				p.text(transitionSymbol, -PApplet.dist(tailXY.x, tailXY.y, headXY.x, headXY.y)/2, 7);
				break;
			case DPA :
				p.text(transitionSymbol + "; "+ stackPop +"/"+stackPush, -PApplet.dist(tailXY.x, tailXY.y, headXY.x, headXY.y)/2, 7);
				break;
		}
		
		//p.text(rotationOffset, -PApplet.dist(tailXY.x, tailXY.y, headXY.x, headXY.y)/2, 7);

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
		rotationOffset = Functions.angleBetween(tailXY, headXY);
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
		// + setters for transition modification
		return stackPop;
	}
	
	public char getStackPush() {
		return stackPush;
	}
}