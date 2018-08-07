package commands;

import static main.PFLAP.machine;

import main.PFLAP;
import p5.State;

public final class deleteState implements Command {

	private final State s;
	private boolean initial;

	public deleteState(State s) {
		this.s = s;
		// initial = s.in
	}

	@Override
	public void execute() {
		s.kill();
		PFLAP.nodes.remove(s);
	}

	@Override
	public void undo() {
		// re add arrows? TODO
		machine.addNode(s);
		if (initial) {
			s.setAsInitial();
		}
		PFLAP.nodes.add(s);
	}

	@Override
	public String description() {
		return "Deleted the state " + s.getLabel();
	}

}
