package commands;

import model.Model;

public final class addState implements Command {

	private final int n;
	private transient boolean initial = true;

	public addState(Integer s) {
		this.n = s;
	}

	@Override
	public void execute() {
//	 
//		machine.addNode(s);
//		PApplet.view.addState(s);
		Model.addState(n);
	}

	@Override
	public void undo() {
		// if was initial, restore
//		PApplet.view.deleteState(s);
		Model.deleteState(n);
	}

	@Override
	public String description() {
		return "New State";
//		return "New State: " + s.getLabel();
	}

}
