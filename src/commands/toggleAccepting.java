package commands;

import model.Model;

public class toggleAccepting implements Command {
	
	private final int s;
	
	public toggleAccepting(int s) {
		this.s = s;
	}

	@Override
	public void execute() {
		if (Model.acceptingStates.contains(s)) {
			Model.acceptingStates.remove(s);
		}
		else {
			Model.acceptingStates.add(s);
		}
	}

	@Override
	public void undo() {
		if (Model.acceptingStates.contains(s)) {
			Model.acceptingStates.remove(s);
		}
		else {
			Model.acceptingStates.add(s);
		}
	}

	@Override
	public String description() {
		return "Toggle Accepting: " + s;
	}

}
