package p5;

import static main.Consts.notificationData.symbolInvalid;
import static main.Consts.notificationData.transitionInvalid;

import static main.Functions.angleBetween;
import static main.Functions.testForLambda;

import static main.PFLAP.cp5Font;
import static main.PFLAP.p;

import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.TWO_PI;

import processing.core.PVector;

import commands.addTransition;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Textfield;

import main.HistoryHandler;
import main.PFLAP;

import model.LogicalTransition;
import model.Model;

/**
 * Temporary arrow that displays between user adding arrow and entering transition info
 */
public final class EntryArrow {
	private Textfield transitionSymbolEntry;
	private ControlP5 cp5;
	private char transitionSymbol, stackPop;
	private String stackPush = "";
	private State head, tail;
	private PVector midPoint, directHead;
	private float angle;
	public boolean dispose = false;

	private static enum entryTypes {
		SYMBOL, POP, PUSH;
	}
	private entryTypes entryType = entryTypes.SYMBOL;

	public EntryArrow(State head, State tail) {
		this.head = head;
		this.tail = tail;
		midPoint = new PVector((head.getPosition().x + tail.getPosition().x) / 2,
				(head.getPosition().y + tail.getPosition().y) / 2);
		angle = angleBetween(head.getPosition(), tail.getPosition()) % TWO_PI;
		directHead = new PVector(head.getPosition().x + head.getRadius() * -0.5f * cos(angle),
				head.getPosition().y + head.getRadius() * -0.5f * sin(angle));
		initCP5();
	}

	public void draw() {
		p.noFill();
		p.stroke((float) PFLAP.transitionColour.getRed(), (float) PFLAP.transitionColour.getGreen(),
				(float) PFLAP.transitionColour.getBlue(), 80); // 80
		p.pushMatrix();
		p.translate(directHead.x, directHead.y);
		p.rotate(angle);
		p.beginShape();
		p.vertex(-10, -7);
		p.vertex(0, 0);
		p.vertex(-10, 7);
		p.endShape();
		p.popMatrix();
		p.strokeWeight(2);
		p.line(tail.getPosition().x, tail.getPosition().y, head.getPosition().x, head.getPosition().y);
	}

	private void kill() {
		cp5.dispose();
		dispose = true;
	}

	private void initCP5() {
		cp5 = new ControlP5(p);
		cp5.setFont(cp5Font);
		transitionSymbolEntry = cp5.addTextfield("Symbol Entry").setColorLabel(0).setLabel("").setSize(30, 15)
				.setFocus(true).addCallback(new CallbackListener() {
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
		transitionSymbolEntry.setPosition(midPoint.x - transitionSymbolEntry.getWidth() / 2, midPoint.y + 10);
	}

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

		LogicalTransition t = new LogicalTransition(head, tail, transitionSymbol, stackPop, stackPush);

		if (Model.assureUniqueTransition(t)) {
			HistoryHandler.buffer(new addTransition(t));
			PFLAP.allowGUIInterraction = true;
			transitionSymbolEntry.hide();
		} else {
			Notification.addNotification(transitionInvalid);
			entryType = entryTypes.SYMBOL;
		}
		kill();
	}
}
