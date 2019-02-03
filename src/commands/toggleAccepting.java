package commands;

import model.Model;

public class toggleAccepting implements Command {
	
	private final int s;
	
	public toggleAccepting(int s) {
		this.s = s;
	}

	@Override
	public void execute() {
		Model.acceptingStates.add(s);
	}

	@Override
	public void undo() {
		Model.acceptingStates.remove(s);
	}

	@Override
	public String description() {
		return "Toggle Accepting: " + s;
	}

}
