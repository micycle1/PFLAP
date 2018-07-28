package commands;

import machines.DFA;
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
		a.setTail(tail);
		a.setHead(head);
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
		return "New Transition: " + a.getSymbol(); //TODO
	}
}
