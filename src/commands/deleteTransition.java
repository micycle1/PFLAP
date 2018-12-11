package commands;

import static main.PFLAP.machine;

import java.util.HashSet;

import main.PFLAP;

import p5.AbstractArrow;
import p5.BezierArrow;
import p5.DirectArrow;
import p5.State;

public final class deleteTransition implements Command {

	private final AbstractArrow a;
	private State head, tail;

	public deleteTransition(AbstractArrow a) {
		this.a = a;
		head = a.getHead();
		tail = a.getTail();

		if (a instanceof BezierArrow) {
			HashSet<AbstractArrow> buffer = new HashSet<>();
			for (AbstractArrow check : head.getOutgoingArrows()) {
				if (check instanceof BezierArrow && check.getHead().equals(tail)
						&& !check.getHead().equals(check.getTail())) {
					buffer.add(check);
				}
			}

			while (buffer.iterator().hasNext()) {
				AbstractArrow replace = buffer.iterator().next();
				DirectArrow newArrow = new DirectArrow(replace.getHead(), replace.getTail(), replace.getSymbol(),
						replace.getStackPop(), replace.getStackPush());
				replace.getHead().addArrowHead(newArrow);
				replace.getTail().addArrowTail(newArrow);
				replace.kill(); // removes from PFLAP.arrows
				PFLAP.arrows.add(newArrow);
				machine.addTransition(newArrow);
				buffer.remove(replace);
			}
		}
	}

	@Override
	public void execute() {
		a.kill();
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
