package commands;

import static main.PFLAP.machine;

import java.util.HashSet;

import main.Functions;
import main.PFLAP;
import p5.AbstractArrow;
import p5.BezierArrow;
import p5.DirectArrow;
import p5.SelfArrow;
import p5.State;

public final class addTransition implements Command {

	private final AbstractArrow a; // todo change final if changing type
	private final State head, tail;

	public addTransition(State tail, State head) {
		this.head = head;
		this.tail = tail;
		
		if (head.equals(tail)) {
			a = new SelfArrow(tail);
		} else {
			a = new DirectArrow(head, tail);
		}

//		HashSet<AbstractArrow> buffer = new HashSet<>();
//		for (AbstractArrow a : head.getOutgoingArrows()) {
//			if (a.getHead().equals(tail) && !a.getHead().equals(a.getTail()) && !(a instanceof BezierArrow)) {
//				buffer.add(a);
//			}
//		}

//		if (!buffer.isEmpty()) {
//			a = new BezierArrow(head, tail);
//			while (buffer.iterator().hasNext()) {
//				AbstractArrow replace = buffer.iterator().next();
//				BezierArrow newArrow = new BezierArrow(replace.getHead(), replace.getTail(), replace.getSymbol(),
//						replace.getStackPop(), replace.getStackPush());
//				replace.getHead().addArrowHead(newArrow);
//				replace.getTail().addArrowTail(newArrow);
//				replace.kill();
//				PFLAP.arrows.add(newArrow);
//				machine.addTransition(newArrow);
//				buffer.remove(replace);
//			}
//		} else {
//			if (head.equals(tail)) {
//				a = new SelfArrow(tail);
//			} else {
//				a = new DirectArrow(head, tail);
//			}
//		}
	}

	@Override
	public void execute() {
		head.addArrowHead(a);
		tail.addArrowTail(a);
		if (a.getSymbol() != '\u0000') {
			machine.addTransition(a);
			Functions.transitionChange(head, tail);	
		}
		PFLAP.arrows.add(a); // removed anyway in transtionchange
	}

	@Override
	public void undo() {
		a.kill();
		Functions.transitionChange(head, tail);
	}

	@Override
	public String description() {
		return "New Transition: " + a.getTail().getLabel() + " -> " + a.getSymbol() + " ->" + a.getHead().getLabel();
	}
}
