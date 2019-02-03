package commands;

import java.util.ArrayList;

import model.LogicalTransition;
import model.Model;

public final class deleteState implements Command {

	private final int s;
	private final ArrayList<LogicalTransition> affectedTransitions;

	public deleteState(int s) {
		this.s = s;
		affectedTransitions = Model.getConnectingTransitions(s);
	}

	@Override
	public void execute() {
		Model.deleteState(s);
	}

	@Override
	public void undo() {
		Model.addState(s);
		Model.addTransition(affectedTransitions);
	}

	@Override
	public String description() {
		return "Deleted State " + s;
	}

}
