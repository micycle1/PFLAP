package commands;

import main.PFLAP;
import p5.Arrow;

public class addTransition implements Command {
	
	private Arrow a;
	
	public addTransition(Arrow a) {
		this.a = a;
	}

	@Override
	public void execute() {
		PFLAP.arrows.add(a);
	}

	@Override
	public void undo() {
		a.parentKill(); // ???
		//deletetransition
	}

	@Override
	public String description() {
		return "New Transition: " + a.getSymbol();
	}
}
