package transitionView;

import p5.AbstractArrow;
import p5.State;

/**
 * Model representation (not graphical) for transitions.
 */
public class LogicalTransition {
	
	protected State head, tail;
	protected char transitionSymbol, stackPop;
	protected String stackPush = "";
	protected AbstractArrow a;
	
	public LogicalTransition(State head, State tail, char transitionSymbol, char stackPop, String stackPush) {
		this.head = head;
		this.tail = tail;
		this.transitionSymbol = transitionSymbol;
		this.stackPop = stackPop;
		this.stackPush = stackPush;
	}
	
	public State getHead() {
		return head;
	}
	
	public State getTail() {
		return tail;
	}
	
	public char getStackPop() {
		return stackPop;
	}
	public String getStackPush() {
		return stackPush;
	}

	public char getSymbol() {
		return transitionSymbol;
	}
	
	public AbstractArrow getAArrow() {
		return a;
	}
	public void setAArrow(AbstractArrow a) {
		this.a = a;
	}
	
}
