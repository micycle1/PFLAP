package commands;

import model.Model;

public class setInitialState implements Command {

	private final int previousInitial, newInitial;

	public setInitialState(int s) {
		previousInitial = Model.getInitialState();
		newInitial = s;
	}

	@Override
	public void execute() {
		Model.setInitialState(newInitial);
	}

	@Override
	public void undo() {
		Model.setInitialState(previousInitial);
	}

	@Override
	public String description() {
		return "Set Initial State: " + newInitial;
	}

}
