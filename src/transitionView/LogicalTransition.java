package transitionView;

import java.io.Serializable;

import p5.State;

/**
 * Model/Logical representation (not graphical) of transition.
 */
public class LogicalTransition implements Serializable {
	
	protected State head, tail;
	protected char transitionSymbol, stackPop;
	protected String stackPush = "";
	
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
	
}
