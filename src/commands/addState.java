package commands;

import static main.PFLAP.machine;

import main.PFLAP;
import static main.PFLAP.p;
import p5.State;
import transitionView.View;

public final class addState implements Command {

	private final State s;

	public addState(State s) {
		this.s = s;
	}

	@Override
	public void execute() {
		s.load(); // todo
		machine.addNode(s);
		p.view.addState(s);
	}

	@Override
	public void undo() {
		s.kill();
		s.deselect();
//		PFLAP.nodes.remove(s);
	}

	@Override
	public String description() {
		return "New State: " + s.getLabel();
	}

}
