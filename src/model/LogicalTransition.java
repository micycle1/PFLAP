package model;

import java.io.Serializable;

import p5.State;

/**
 * Model/Logical representation (not graphical) of transition.
 */
public class LogicalTransition implements Serializable {
	
	public Integer head, tail;
	protected char transitionSymbol, stackPop;
	protected String stackPush = "";
	
	public LogicalTransition(Integer head, Integer tail, char transitionSymbol, char stackPop, String stackPush) {
		this.head = head;
		this.tail = tail;
		this.transitionSymbol = transitionSymbol;
		this.stackPop = stackPop;
		this.stackPush = stackPush;
	}
	
	public Integer getHead() {
		return head;
	}
	
	public Integer getTail() {
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
