package commands;

import p5.Arrow;
import p5.State;

/**
 * DELETE SINGLE TRANSITION NOT YET IMPLEMENTED TODO
 */

public class deleteTransition implements Command {

	private Arrow a;
	private State head, tail; //???

	public deleteTransition(Arrow a) {
		this.a = a;
	}

	@Override
	public void execute() {
		a.kill();
	}

	@Override
	public void undo() {
		a.unKill();
	}

	@Override
	public String description() {
		return "Delete Transition: " + a.getSymbol();
	}
}
