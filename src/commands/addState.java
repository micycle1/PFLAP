package commands;

import model.Model;

public final class addState implements Command {

	private final int n;

	public addState(Integer s) {
		this.n = s;
	}

	@Override
	public void execute() {
		Model.addState(n);
	}

	@Override
	public void undo() {
		Model.deleteState(n);
	}

	@Override
	public String description() {
		return "New State: " + n;
	}

}
