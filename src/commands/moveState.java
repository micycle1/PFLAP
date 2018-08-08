package commands;

import p5.State;
import processing.core.PVector;

public final class moveState implements Command {
	
	private final PVector oldPos, newPos;
	private final State s;
	
	public moveState(State s, PVector oldPosition) {
		this.s = s;
		oldPos = oldPosition;
		newPos = s.getPosition();
		// use state selected position
	}
	
	@Override
	public void execute() {
		s.setPosition(newPos);
	}

	@Override
	public void undo() {
		s.setPosition(oldPos);
	}

	@Override
	public String description() {
		return "Move State " + s.getLabel() + " to " + newPos.toString();
	}

}
