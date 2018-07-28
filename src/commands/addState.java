package commands;

import machines.DFA;
import main.PFLAP;
import p5.State;

public class addState implements Command {

	private State s;

	public addState(State s) {
		this.s = s;
	}

	@Override
	public void execute() {
		PFLAP.nodes.add(s);
		DFA.addNode(s);
	}

	@Override
	public void undo() {
		s.kill();
		PFLAP.nodes.remove(s);
	}

	@Override
	public String description() {
		return "New State: " + s.getLabel();
	}

}
