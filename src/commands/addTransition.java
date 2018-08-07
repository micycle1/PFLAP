package commands;

import main.PFLAP;
import p5.Arrow;
import p5.State;

public class addTransition implements Command {

	private Arrow a;
	private State head, tail;

	public addTransition(State tail, State head) {
		this.head = head;
		this.tail = tail;
		a = new Arrow(tail, head);
	}

	@Override
	public void execute() {
		head.addArrowHead(a);
		tail.addArrowTail(a);
		PFLAP.arrows.add(a);
	}

	@Override
	public void undo() {
		a.kill();
	}

	@Override
	public String description() {
		return "New Transition: " + a.getTail().getLabel() + " -> " + a.getSymbol() + " ->" + a.getHead().getLabel();
	}
}
