package commands;

import static main.PFLAP.machine;

import main.PFLAP;

import p5.AbstractArrow;
import p5.State;

public final class deleteTransition implements Command {

	private final AbstractArrow a;
	private State head, tail;

	public deleteTransition(AbstractArrow a) {
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
