package p5;

import java.util.ArrayList;

import DFA.Machine;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;
import controlP5.Textfield;
import main.Consts;
import main.PFLAP;

import processing.core.PApplet;
import processing.core.PVector;

public class State {

	public static PApplet p;
	public int stateID; // TODO rename to 'name'
	public String label; //TODO change to private 
	public ControlP5 cp5;
	private ScrollableList stateOptions;
	private ControlListener listener;
	private PVector position, selectedPosition;
	private ArrayList<Arrow> arrowHeads = new ArrayList<>();
	private ArrayList<Arrow> arrowTails = new ArrayList<>();
	private boolean selected = false, accepting = false, initial = false;
	private static Textfield rename;
	private static State renameState;

	// [transition symbol, next node, current string]

	static { // only one rename box for class, reads which state is renaming
		// @formatter:off
		rename = PFLAP.cp5.addTextfield("Rename State");
		rename.hide()
		.setSize(45, 20)
		.setFocus(false)
		.addCallback(new CallbackListener() {
			@Override
			public void controlEvent(CallbackEvent input) {
				if (input.getAction() == 100) {
				renameState.label = rename.getStringValue();
				rename.setFocus(false).hide();
				rename.clear();
				renameState = null;
				}
			}
		});
		// @formatter:on
	}
	
	public State(PVector XY, int liveID, int ID) {
		Machine.addNode(this);
		label = "q" + liveID;
		position = XY;
		stateID = ID;
		cp5 = new ControlP5(p);
		cp5.hide();
		listener = new ControlListener() {

			@Override
			public void controlEvent(ControlEvent optionSelected) {
				cp5.hide();
				switch ((int) optionSelected.getValue()) {
					case 0 :
						PFLAP.nodes.forEach(s -> s.initial = false);
						initial = true;
						Machine.setInitialState(State.this);
						break;
					case 1 :
						accepting = !accepting;
						break;
					case 2 :
						renameState = State.this;
						rename.setPosition(position.x, position.y + 50);
						rename.setFocus(true);
						rename.show();
						break;
					default :
						break;
				}

			}
		};
		stateOptions = cp5.addScrollableList("Options");
		// @formatter:off
		stateOptions.setType(1)
				.addItems(new String[]{"Set As Inititial", "Toggle Accepting", "Rename"})
				.open()
				.addListener(listener);
		// @formatter:on
	}

	public void kill() {
		p.print("kill"); //TODO remove
		arrowHeads.forEach(a -> a.kill());
		arrowTails.forEach(a -> a.kill());
		cp5.getAll().forEach(c -> c.remove());
		//Machine.deleteNode(this); TODO uncomment
		if (initial) {
			Machine.setInitialState(null);
		}
	}

	public void draw() {
		p.strokeWeight(3);
		if (initial) {
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
			p.fill(0, 35, 255);
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
		arrowHeads.forEach(a -> a.update());
		arrowTails.forEach(a -> a.update());
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