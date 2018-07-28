package commands;

import p5.Arrow;

/**
 * DELETE SINGLE TRANSITION NOT YET IMPLEMENTED TODO
 */

public class deleteTransition implements Command {

	private Arrow a;

	public deleteTransition(Arrow a) {
		this.a = a;
	}

	@Override
	public void execute() {
		a.parentKill();
	}

	@Override
	public void undo() {
	}

	@Override
	public String description() {
		return "Delete Transition: " + a.getSymbol();
	}
}
