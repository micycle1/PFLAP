package commands;

import static main.PFLAP.machine;

import main.Functions;
import main.PFLAP;
import p5.AbstractArrow;
import p5.DirectArrow;
import p5.SelfArrow;
import p5.State;
import p5.BezierArrow;

public final class addTransition implements Command {

	private final AbstractArrow a;
	private final State head, tail;

	public addTransition(State tail, State head) {
		this.head = head;
		this.tail = tail;
		
//		a = new BezierArrow(head, tail);
		
		if (Functions.detectCycle(tail, head)) {
			a = new BezierArrow(head, tail);
		} else {
			if (head.equals(tail)) {
				a = new SelfArrow(tail);
			}
			else {
				a = new DirectArrow(head, tail); // todo detect loop here?
			}
		}
	}

	@Override
	public void execute() {
		head.addArrowHead(a); // remove head and tail references from this class, arrow constructor calls add on head and tail
		tail.addArrowTail(a);
		if (a.getSymbol() != '\u0000') {
			machine.addTransition(a);
		}
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
