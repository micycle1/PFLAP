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
import transitionView.LogicalTransition;
import transitionView.View;

public final class addTransition implements Command {

//	private final AbstractArrow a; // todo change final if changing type
//	private final State head, tail;
	private final LogicalTransition t;

//	public addTransition(State tail, State head) {
//		this.head = head;
//		this.tail = tail;
//		
//		if (head.equals(tail)) {
//			a = new SelfArrow(tail);
//		} else {
//			a = new DirectArrow(head, tail);
//		}
//		
//		// create logical transition, add to view and machine!
//	}
	
	public addTransition(LogicalTransition t) {
		this.t = t;
	}

	@Override
	public void execute() {
//		head.addArrowHead(a);
//		tail.addArrowTail(a);
//		if (a.getSymbol() != '\u0000') {
//			machine.addTransition(a);
//		}
//		PFLAP.arrows.add(a); // removed anyway in transtionchange
//		View.addTransition(t);
	}

	@Override
	public void undo() {
//		View.deleteTransition(a);
	}

	@Override
	public String description() {
//		return "New Transition: " + a.getTail().getLabel() + " -> " + a.getSymbol() + " ->" + a.getHead().getLabel();
		return "";
	}
}
