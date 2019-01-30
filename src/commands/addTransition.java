package commands;

import model.LogicalTransition;
import model.Model;
import processing.core.PVector;

public final class addTransition implements Command {

	private final LogicalTransition t;
	
	public addTransition(LogicalTransition t) {
		this.t = t;
	}
	
	public addTransition(LogicalTransition t, PVector pos) {
		this.t = t;
	}

	@Override
	public void execute() {
//		PFLAP.PApplet.view.addTransition(t);
//		machine.addTransition(t); // deprecate (machine gets model from view)
		Model.addTransition(t);
	}

	@Override
	public void undo() {
//		PFLAP.PApplet.view.deleteTransition(t);
//		machine.removeTransition(t);
	}

	@Override
	public String description() {
		return "";
//		return "New Transition: " + t.getTail().getLabel() + " -> " + t.getSymbol() + " ->" + t.getHead().getLabel();
	}
}
