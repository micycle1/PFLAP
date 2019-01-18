package commands;

import java.util.ArrayList;

import main.PFLAP;

import p5.AbstractArrow;
import transitionView.LogicalTransition;

public final class deleteTransition implements Command {

	private final ArrayList<LogicalTransition> transitions;

	public deleteTransition(AbstractArrow a) {
		this.transitions = a.transitions;
	}

	@Override
	public void execute() {
		PFLAP.PApplet.view.deleteTransition(transitions);
	}

	@Override
	public void undo() {
		PFLAP.PApplet.view.addTransitions(transitions);
	}

	@Override
	public String description() {
		return "Delete Transition: ";
	}
}
