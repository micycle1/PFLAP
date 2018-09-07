package commands;

import static main.PFLAP.machine;

import main.PFLAP;
import p5.Arrow;
import p5.State;

/**
 * DELETE SINGLE TRANSITION NOT YET IMPLEMENTED TODO
 */

public final class deleteTransition implements Command {

	private Arrow a;
	private State head, tail;

	public deleteTransition(Arrow a) {
		this.a = a;
	}

	@Override
	public void execute() {
		head = a.getHead();
		tail = a.getTail();
		a.kill();
		// remove from machine?
	}

	@Override
	public void undo() {
		a.enableUI();
		head.addArrowHead(a);
		tail.addArrowTail(a);
		machine.addTransition(a);
		PFLAP.arrows.add(a);
	}

	@Override
	public String description() {
		return "Delete Transition: " + a.getSymbol();
	}
}
