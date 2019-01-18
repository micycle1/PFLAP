package commands;

import main.PFLAP;
import p5.State;
import processing.core.PVector;

public final class moveState implements Command {
	
	private final PVector oldPos;
	private PVector newPos;
	private final State s;
	
	public moveState(State s, PVector oldPosition) {
		this.s = s;
		oldPos = oldPosition;
		newPos = s.getPosition();
	}
	
	public void updatePos() {
		newPos = s.getPosition();
	}
	
	@Override
	public void execute() {
		PFLAP.PApplet.view.moveState(s, newPos);
	}

	@Override
	public void undo() {
		PFLAP.PApplet.view.moveState(s, oldPos);
	}

	@Override
	public String description() {
		return "Move State " + s.getLabel() + " to " + newPos.toString();
	}

}
