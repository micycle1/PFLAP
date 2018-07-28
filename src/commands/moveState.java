package commands;

import p5.State;
import processing.core.PVector;

public class moveState implements Command {
	
	private PVector oldPos, newPos;
	private State s;
	
	public moveState(State s, PVector position) {
		this.s = s ;
		newPos = position;
	}

	@Override
	public void execute() {
		oldPos = s.getPosition();
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
