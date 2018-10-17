package commands;

import main.PFLAP;
import p5.AbstractArrow;

public final class modifyTransition implements Command {
	
	private final AbstractArrow transition;
	private final char oldTransitionSymbol, oldStackPop;
	private final String oldStackPush;
	private char newTransitionSymbol, newStackPop;
	private String newStackPush;
	private boolean init = true;
		
	public modifyTransition(AbstractArrow a) {
		transition = a;
		PFLAP.machine.removeTransition(a);
		
		oldTransitionSymbol = a.getSymbol();
		oldStackPop = a.getStackPop();
		oldStackPush = a.getStackPush();
	}

	@Override
	public void execute() {
		if (init) { 
		newTransitionSymbol = transition.getSymbol();
		newStackPop = transition.getStackPop();
		newStackPush = transition.getStackPush();
		init = false;
		}
		
		PFLAP.machine.removeTransition(transition);
		transition.setTransitionSymbol(newTransitionSymbol);
		transition.setStackPop(newStackPop);
		transition.setStackPush(newStackPush);
		PFLAP.machine.addTransition(transition);
	}

	@Override
	public void undo() {
		PFLAP.machine.removeTransition(transition);
		transition.setTransitionSymbol(oldTransitionSymbol);
		transition.setStackPop(oldStackPop);
		transition.setStackPush(oldStackPush);
		PFLAP.machine.addTransition(transition);
	}

	@Override
	public String description() {
		return "Modify Transition " + transition.toString();
	}

}
