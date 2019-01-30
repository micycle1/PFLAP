package commands;

import main.PFLAP;
import model.Model;
import p5.State;

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
		return "Toggle Accepting: State " + PFLAP.PApplet.view.getStateByID(s).getLabel();
	}

}
