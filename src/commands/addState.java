package commands;

import static main.PFLAP.machine;

import main.PFLAP;
import p5.State;

public final class addState implements Command {

	private final State s;

	public addState(State s) {
		this.s = s;
	}

	@Override
	public void execute() {
		s.load(); // todo
		PFLAP.nodes.add(s);
		machine.addNode(s);
	}

	@Override
	public void undo() {
		s.kill();
		s.deselect();
		PFLAP.nodes.remove(s);
	}

	@Override
	public String description() {
		return "New State: " + s.getLabel();
	}

}
