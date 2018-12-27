package p5;

import static main.Consts.notificationData.symbolInvalid;
import static main.Consts.notificationData.transitionInvalid;
import static main.Functions.testForLambda;
import static main.PFLAP.cp5Font;
import static main.PFLAP.machine;
import static main.PFLAP.p;

import java.io.Serializable;

import commands.Command;
import commands.deleteTransition;
import commands.modifyTransition;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ListBox;
import controlP5.Textfield;
import main.Functions;
import main.HistoryHandler;
import main.PFLAP;
import processing.core.PVector;

public abstract class AbstractArrow implements Serializable {
	
	protected State tail, head;
	protected transient ControlP5 cp5;
	protected transient ListBox stateOptions;
	private transient ControlListener menuListener;
	protected transient Textfield transitionSymbolEntry;
	private char transitionSymbol, stackPop;
	private String stackPush = "";
	protected final int ID;
	private transient Command modifyBuffer;
	
	private static enum entryTypes {
		SYMBOL, POP, PUSH;
	}
	private entryTypes entryType = entryTypes.SYMBOL;
	
	public AbstractArrow(State head, State tail) {
		ID = this.hashCode();
		this.head = head;
		this.tail = tail;
		initCP5();
		initialEntry();
		update();
	}
	
	/**
	 * Constructor when polymorphing
	 */
	public AbstractArrow(State head, State tail, char transitionSymbol, char stackPop, String stackPush) {
		ID = this.hashCode();
		this.head = head;
		this.tail = tail;
		this.transitionSymbol = transitionSymbol;
		this.stackPop = stackPop;
		this.stackPush = stackPush;
		initCP5();
		update();
	}
	
	public final void initCP5() {
		cp5 = new ControlP5(p);
		cp5.setFont(cp5Font);
//		cp5.show(); remove
		cp5.hide();
		// @formatter:off
		transitionSymbolEntry = cp5.addTextfield("Symbol Entry")
				.setColorLabel(0)
				.setLabel("")
				.setSize(30, 15)
//				.setFocus(true) remove
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
						modifyBuffer = new modifyTransition(AbstractArrow.this);
						entryType = entryTypes.SYMBOL;
						stateOptions.hide();
						transitionSymbolEntry.show();
						transitionSymbolEntry.setFocus(true);
						PFLAP.allowGUIInterraction = false;
						break;
					case 1 : // Delete
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
	
	private final void initialEntry() {
		cp5.show();
		transitionSymbolEntry.setFocus(true);
	}
	
	/**
	 * Specifies behaviour when entering transition info.
	 */
	private final void entry(entryTypes entryType) {
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
			Functions.transitionChange(head, tail); // todo
			
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
	
	public abstract void update();
	
	public final void parentKill() {
		PFLAP.arrows.remove(this);
		machine.removeTransition(this);
		disableUI();
	}

	public final void kill() {
		head.childKill(this);
		tail.childKill(this);
		machine.removeTransition(this);
		PFLAP.arrows.remove(this);
		disableUI();
//		cp5.dispose();
	}
	
	public abstract void draw();
	
	protected final void drawTransitionLabel(PVector pos) {
		p.fill(0);
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
	
	protected final void drawArrowTip(PVector translate, float angle) {
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

	public abstract boolean isMouseOver(PVector mousePos);
	
	public final void hideUI() {
		cp5.hide();
	}

	public final void showUI() {
		cp5.show();
		stateOptions.show();
		stateOptions.open();
	}

	public final void enableUI() {
		cp5.show();
		cp5.setUpdate(true);
	}

	private final void disableUI() {
		cp5.hide();
		cp5.setUpdate(false);
	}
	
	public final void disposeUI() {
		cp5.dispose();
	}

	public final State getHead() {
		return head;
	}

	public final void setHead(State head) {
		this.head = head;
	}

	public final State getTail() {
		return tail;
	}

	public final void setTail(State tail) {
		this.tail = tail;
	}

	public final char getSymbol() {
		return transitionSymbol;
	}

	public final void setTransitionSymbol(char transitionSymbol) {
		this.transitionSymbol = transitionSymbol;
	}

	public final char getStackPop() {
		return stackPop;
	}

	public final void setStackPop(char stackPop) {
		this.stackPop = stackPop;
	}

	public final String getStackPush() {
		return stackPush;
	}

	public final void setStackPush(String stackPush) {
		this.stackPush = stackPush;
	}

	@Override
	public final String toString() {
		return String.valueOf(ID) + tail.getLabel() + head.getLabel();
	}

}