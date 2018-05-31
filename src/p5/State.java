package p5;

import java.util.ArrayList;

import DFA.Machine;
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
	public int nodeID;
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
	private boolean selected = false, accepting = false; // TODO (deletion)

	// [transition symbol, next node, current string]

	public State(PVector XY, int ID) {
		label = "q" + ID;
		nodeID = this.hashCode();
		position = XY;
		cp5 = new ControlP5(p);
		cp5.hide();
		listener = new ControlListener() {

			@Override
			public void controlEvent(ControlEvent optionSelected) {
				cp5.hide();
				switch ((int) optionSelected.getValue()) {
					case 0 :
						PFLAP.initialStateID = nodeID;
						break;
					case 1 :
						accepting = !accepting;
						p.println(nodeID);
						Machine.nodes.get(nodeID).toggleAccepting();
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
				.addItems(new String[]{"Set As Inititial", "Toggle Accepting", "Rename"})
				.open()
				.addListener(listener);
				// @formatter:on
	}

	public void kill() {
		arrowHeads.forEach(a -> a.kill());
		arrowTails.forEach(a -> a.kill());
		cp5.getAll().forEach(c -> c.remove());
	}

	public void draw() {

		// if (cp5.isVisible()) {
		// if (!(cp5.isMouseOver())) {
		// cp5.hide();
		// }
		// }

		p.strokeWeight(3);
		if (PFLAP.initialStateID == nodeID) {
			p.fill(255, 0, 0);
			p.noStroke();

			p.pushMatrix();
			p.translate(position.x - Consts.stateRadius / 2 - 3, position.y);
			p.triangle(-Consts.initialNodeIndicatorSize, -Consts.initialNodeIndicatorSize,
					-Consts.initialNodeIndicatorSize, Consts.initialNodeIndicatorSize, 0, 0);
			p.rotate(PApplet.radians(90));
			p.popMatrix();

			p.stroke(0);
		}
		if (!selected) {
			p.fill(255, 220, 0);
			p.ellipse(position.x, position.y, Consts.stateRadius, Consts.stateRadius);
			p.fill(0);
		} else {
			if (PFLAP.initialStateID == ID) {
				p.fill(0, 255, 0); // TODO
			} else {
				p.fill(0, 35, 255);
			}

			p.ellipse(position.x, position.y, Consts.stateRadius, Consts.stateRadius);
			p.fill(255);
		}
		if (accepting) {
			p.noFill();
			p.strokeWeight(2);
			p.ellipse(position.x, position.y, Consts.stateRadius - 9, Consts.stateRadius - 9);
		}
		p.text(label, position.x, position.y);
	}

	public void setPosition(PVector position) {

		this.position = position;
		cp5.setPosition((int) this.position.x + 10, (int) this.position.y + 10);
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
		stateOptions.open();
		selected = true;
		selectedPosition = position;
	}

	public void deselect() {
		cp5.hide();
		selected = false;
		selectedPosition = null;
	}

	public PVector getPosition() {
		return position;
	}

	public PVector getSelectedPosition() {
		return selectedPosition;
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

	public boolean UIOpen() {
		return cp5.isVisible();
	}

	public boolean isAccepting() {
		return accepting;
	}
}