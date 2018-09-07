package commands;

import p5.State;

public class toggleAccepting implements Command {
	
	private final State s;
	
	public toggleAccepting(State s) {
		this.s = s;
	}

	@Override
	public void execute() {
		s.toggleAccepting();
	}

	@Override
	public void undo() {
		s.toggleAccepting();
	}

	@Override
	public String description() {
		return "Toggle Accepting: State " + s.getLabel();
	}

}
