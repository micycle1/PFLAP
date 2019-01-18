package commands;

import static main.PFLAP.machine;

import main.PFLAP;

import transitionView.LogicalTransition;

public final class addTransition implements Command {

	private final LogicalTransition t;
	
	public addTransition(LogicalTransition t) {
		this.t = t;
	}

	@Override
	public void execute() {
		PFLAP.PApplet.view.addTransition(t);
		machine.addTransition(t); // deprecate (machine gets model from view)
	}

	@Override
	public void undo() {
		PFLAP.PApplet.view.deleteTransition(t);
		machine.removeTransition(t);
	}

	@Override
	public String description() {
		return "New Transition: " + t.getTail().getLabel() + " -> " + t.getSymbol() + " ->" + t.getHead().getLabel();
	}
}
