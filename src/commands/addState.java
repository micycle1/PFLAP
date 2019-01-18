package commands;

import static main.PFLAP.machine;

import main.PFLAP.PApplet;

import p5.State;

public final class addState implements Command {

	private final State s;
	private transient boolean initial = true;

	public addState(State s) {
		this.s = s;
	}

	@Override
	public void execute() {
		if (!initial) {
			s.initCP5();  // re-create cp5 after load
			initial = true;
		}
		machine.addNode(s);
		PApplet.view.addState(s);
	}

	@Override
	public void undo() {
		// if was initial, restore
		PApplet.view.deleteState(s);
	}

	@Override
	public String description() {
		return "New State: " + s.getLabel();
	}

}
