package commands;

import main.PFLAP;
import p5.State;

public class setInitialState implements Command {

	private final State previousInitial, newInitial;

	public setInitialState(State s) {
		previousInitial = PFLAP.machine.getInitialState();
		newInitial = s;
	}

	@Override
	public void execute() {
		if (previousInitial != null) {
			previousInitial.deInitial();
		}
		newInitial.setAsInitial();
		PFLAP.machine.setInitialState(newInitial);
	}

	@Override
	public void undo() {
		newInitial.deInitial();
		if (previousInitial != null) {
			previousInitial.setAsInitial();
		}
		PFLAP.machine.setInitialState(previousInitial);
	}

	@Override
	public String description() {
		return "Set Initial State: State " + newInitial.getLabel();
	}

}
