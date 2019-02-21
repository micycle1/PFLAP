package p5;

import static main.PFLAP.cp5Font;
import static main.PFLAP.p;

import java.util.ArrayList;

import commands.deleteTransition;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ListBox;
import controlP5.Textfield;

import main.Functions;
import main.HistoryHandler;
import main.PFLAP;

import model.LogicalTransition;

import processing.core.PVector;

public abstract class AbstractArrow {
	
	protected State tail, head; // remove references, get positions from View class 
	protected ControlP5 cp5;
	protected ListBox stateOptions;
	private ControlListener menuListener;
	private Textfield transitionSymbolEntry;
	private String transitionInfo = "";
	protected final int ID;
	public ArrayList<LogicalTransition> transitions; // Transitions this arrow represents

	private static enum entryTypes {
		SYMBOL, POP, PUSH;
	}
	private entryTypes entryType = entryTypes.SYMBOL;
	
//	public AbstractArrow(State head, State tail) {
//		ID = this.hashCode();
//		this.head = head;
//		this.tail = tail;
//		initCP5();
//		update();
//	}

	public AbstractArrow(State head, State tail, ArrayList<LogicalTransition> transitions) {
		ID = this.hashCode();
		this.head = head;
		this.tail = tail;
		this.transitions = transitions;
		
		switch (PFLAP.mode) {
			case MOORE :
			case DFA :
				for (LogicalTransition t : transitions) {
					transitionInfo += t.getSymbol() + "\n";
				}
				break;
			case DPA :
				for (LogicalTransition t : transitions) {
					transitionInfo += t.getSymbol() + " ; " + t.getStackPop() + "/" + t.getStackPush() + "\n";
				}
				break;
			case MEALY :
				for (LogicalTransition t : transitions) {
					transitionInfo += t.getSymbol() + " ; " + t.getStackPush() + "\n";
				}
				break;
		}
		initCP5();
		update();
	}

	public final void initCP5() {
		cp5 = new ControlP5(p);
		cp5.setFont(cp5Font);
		cp5.hide();
		// @formatter:off todo
//		transitionSymbolEntry = cp5.addTextfield("Symbol Entry")
//				.setColorLabel(0)
//				.setLabel("")
//				.setSize(30, 15)
//				.addCallback(new CallbackListener() {
//		// @formatter:on
		// @Override
		// public void controlEvent(CallbackEvent input) {
		// if (input.getAction() == 100) {
		// String tempSymbol = transitionSymbolEntry.getStringValue();
		// if (tempSymbol.length() == 1 && entryType != entryTypes.PUSH) {
		// entry(entryType);
		// } else {
		// if (entryType == entryTypes.PUSH && tempSymbol.length() > 0) {
		// entry(entryType);
		// } else {
		// Notification.addNotification(symbolInvalid);
		// }
		// }
		// }
		// }
		// });

		menuListener = new ControlListener() {
			@Override
			public void controlEvent(ControlEvent optionSelected) {
				switch ((int) optionSelected.getValue()) {
					case -1 : // Modify [Deprecated] todo
						entryType = entryTypes.SYMBOL;
						stateOptions.hide();
						transitionSymbolEntry.show();
						transitionSymbolEntry.setFocus(true);
						PFLAP.allowGUIInterraction = false;
						break;
					case 0 : // Delete
						HistoryHandler.buffer(new deleteTransition(AbstractArrow.this));
						stateOptions.hide();
						break;
					default :
						break;
				}
			}

		};
		stateOptions = cp5.addListBox("          Options");
		// @formatter:off
		stateOptions.addItems(new String[]{"Delete Transition"})
			.open()
			.hide()
			.setWidth(140)
			.setBarHeight(35)
			.setColorLabel(Functions.colorToRGB(PFLAP.stateColour.invert()))
			.setItemHeight(25)
			.setColorValue(Functions.colorToRGB(PFLAP.stateColour.invert()))
			.setColorBackground(Functions.colorToRGB(PFLAP.stateColour))
			.setColorActive(Functions.colorToRGB(PFLAP.stateColour))
			.setColorForeground(Functions.darkenColor(PFLAP.stateColour, 0.33f)) // Mouseover
			.addListener(menuListener);
		// @formatter:on
	}

	public abstract void update();
	
	public abstract void draw();

	protected final void drawTransitionLabel(PVector pos) {
		p.fill(0);
		p.text(transitionInfo, pos.x, pos.y);
	}

	protected final void drawArrowTip(PVector translate, float angle) {
		p.noFill();
		p.strokeWeight(2);
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

	public abstract boolean isMouseOver(PVector mousePos);

	public final void hideUI() {
		cp5.hide();
	}

	public final void showUI() {
		cp5.show();
		stateOptions.show();
		stateOptions.open();
	}

	public final void disposeUI() {
		cp5.dispose();
	}

	public final State getHead() {
		return head;
	}

	public final State getTail() {
		return tail;
	}
	
	@Override
	public final String toString() {
		return String.valueOf(ID) + tail.getLabel() + head.getLabel();
	}

}