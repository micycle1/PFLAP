package p5;

import java.util.ArrayList;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;

import main.Consts;
import main.PFLAP;

import processing.core.PApplet;
import processing.core.PVector;

public class State {

	public static PApplet p;
	public int ID;
	private String label;
	public ControlP5 cp5;
	private ScrollableList stateOptions;
	private ControlListener listener;
	private PVector position, selectedPosition; // TODO for multi move
	private ArrayList<Arrow> arrowHeads = new ArrayList<>(); // TODO head
																// touching this
																// state
	private ArrayList<Arrow> arrowTails = new ArrayList<>(); // TODO tailing
																// touching this
																// state
	public boolean selected = false, finalState = false; // TODO (deletion)

	// [transition symbol, next node, current string]

	public State(PVector XY, int ID) {
		label = "q" + ID;
		position = XY;
		this.ID = ID;
		cp5 = new ControlP5(p);
		cp5.hide();
		listener = new ControlListener() {

			@Override
			public void controlEvent(ControlEvent optionSelected) {
				switch ((int) optionSelected.getValue()) {
					case 0 :
						PFLAP.initialState = State.this;
					case 1 :
						finalState = !finalState;
						break;
					case 2 :
						// bring up text box for text entry
						break;
					default :
						break;
				}

			}
		};
		stateOptions = cp5.addScrollableList("Options");
		stateOptions.setType(1)
		// @formatter:off
				.addItems(new String[]{"Set As Inititial", "Toggle Final", "Rename"})
				.close()
				.addListener(listener);
				// @formatter:on
	}

	public void kill() {
		arrowHeads.forEach(a -> a.kill());
		arrowTails.forEach(a -> a.kill());
		cp5.getAll().forEach(c -> c.remove());
	}

	public void draw() {

//		if (cp5.isVisible()) {
//			if (!(cp5.isMouseOver())) {
//				cp5.hide();
//			}
//		}

		p.strokeWeight(3);
		if (PFLAP.initialState == this) {
			p.fill(0);
			p.triangle(position.x - 10, position.y - 10, position.x - 10, position.y + 10, position.x, position.y);
		}
		if (!selected) {
			p.fill(255, 220, 0);
			p.ellipse(position.x, position.y, Consts.stateRadius, Consts.stateRadius);
			p.fill(0);
		} else {
			p.fill(0, 35, 255);
			p.ellipse(position.x, position.y, Consts.stateRadius, Consts.stateRadius);
			p.fill(255);
		}
		if (finalState) {
			p.noFill();
			p.strokeWeight(2);
			p.ellipse(position.x, position.y, Consts.stateRadius - 9, Consts.stateRadius - 9);
		}
		p.text(label, position.x, position.y);
	}

	public void setPosition(PVector position) {
		cp5.setPosition((int) position.x + 10, (int) position.y + 10);
		this.position = position;
		for (Arrow a : arrowHeads) {
			// a.setHeadXY(position);
			a.update();
		}
		for (Arrow a : arrowTails) {
			// a.setTailXY(position);
			a.update();
		}
	}

	public void select() {
		selected = true;
		selectedPosition = position;
	}

	public void deselect() {
		selected = false;
		selectedPosition = null;
	}

	public PVector getPosition() {
		return position;
	}

	public void addArrowHead(Arrow a) {
		arrowHeads.add(a);
	}

	public void addArrowTail(Arrow a) {
		arrowTails.add(a);
	}

	public void hideUI() {
		cp5.hide();
	}

	public void showUI() {
		cp5.show();
	}
}