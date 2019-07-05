package p5;

import static main.PFLAP.cp5Font;
import static main.PFLAP.p;

import java.util.ArrayList;

import commands.deleteTransition;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ListBox;

import main.Functions;
import main.HistoryHandler;
import main.PFLAP;
import main.PFLAP.PApplet;
import model.LogicalTransition;

import processing.core.PVector;

public abstract class AbstractArrow {
	
	protected State tail, head; // remove references, get positions from View class 
	protected ControlP5 cp5;
	protected ListBox stateOptions;
	private ControlListener menuListener;
	private String transitionInfo = "";
	protected final int ID;
	public ArrayList<LogicalTransition> transitions; // Transitions this arrow represents
	
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

	private final void initCP5() {
		cp5 = new ControlP5(p);
		cp5.setFont(cp5Font);
		cp5.hide();
		
		menuListener = new ControlListener() {
			@Override
			public void controlEvent(ControlEvent optionSelected) {
				switch ((int) optionSelected.getValue()) {
					case 0 :
						PFLAP.allowGUIInterraction = false;
						PApplet.view.entryArrow(head, tail);
						stateOptions.hide();
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
		stateOptions.addItems(new String[]{"Extend Transition", "Delete Transition"})
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
	
	protected final void drawTransitionLabel(float xPos, float yPos) {
		p.fill(0);
		p.text(transitionInfo, xPos, yPos);
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
	
	@Override
	public final String toString() {
		return String.valueOf(ID) + tail.getLabel() + head.getLabel();
	}

}