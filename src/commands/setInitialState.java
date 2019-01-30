package commands;

import main.PFLAP;
import model.Model;
import p5.State;

public class setInitialState implements Command {

	private final int previousInitial, newInitial;

	public setInitialState(int s) {
		previousInitial = Model.initialState;
		newInitial = s;
	}

	@Override
	public void execute() {
		Model.initialState = newInitial;
	}

	@Override
	public void undo() {
		Model.initialState = previousInitial;
	}

	@Override
	public String description() {
		return "Set Initial State: State ";
//		return "Set Initial State: State " + newInitial.getLabel();
	}

}
