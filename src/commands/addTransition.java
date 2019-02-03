package commands;

import model.LogicalTransition;
import model.Model;

public final class addTransition implements Command {

	private final LogicalTransition t;
	
	public addTransition(LogicalTransition t) {
		this.t = t;
	}

	@Override
	public void execute() {
		Model.addTransition(t);
	}

	@Override
	public void undo() {
		Model.deleteTransition(t);
	}

	@Override
	public String description() {
		return "Add Transition: " + t.toString();
	}
}
