package commands;

import static main.PFLAP.machine;

import java.util.ArrayList;

import main.PFLAP;
import p5.State;
import transitionView.LogicalTransition;

public final class deleteState implements Command {

	private final State s;
	private final ArrayList<LogicalTransition> affectedTransitions;

	public deleteState(State s) {
		this.s = s;
		affectedTransitions = PFLAP.PApplet.view.getConnectingTransitions(s);
	}

	@Override
	public void execute() {
		PFLAP.PApplet.view.deleteState(s);
	}

	@Override
	public void undo() {
		PFLAP.PApplet.view.addState(s);
		PFLAP.PApplet.view.addTransitions(affectedTransitions);
	}

	@Override
	public String description() {
		return "Deleted the state " + s.getLabel();
	}

}
