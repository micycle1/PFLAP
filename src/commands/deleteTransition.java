package commands;

import java.util.ArrayList;

import model.LogicalTransition;
import model.Model;

import p5.AbstractArrow;

public final class deleteTransition implements Command {

	private final ArrayList<LogicalTransition> transitions;

	public deleteTransition(AbstractArrow a) {
		this.transitions = a.transitions;
	}

	@Override
	public void execute() {
		Model.deleteTransition(transitions);
	}

	@Override
	public void undo() {
		Model.addTransition(transitions);
	}

	@Override
	public String description() {
		return "Delete Transition: " + transitions.get(0).toString();
	}
}
