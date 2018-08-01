package tests;

final class SimpleArrow {
	
	private SimpleState tail, head;
	private char transitionSymbol = ' ', stackPop, stackPush;
	
	protected SimpleArrow(SimpleState tail, SimpleState head, char transitionSymbol, char stackPop, char stackPush) {
		this.tail = tail;
		this.head = head;
		this.transitionSymbol = transitionSymbol;
		this.stackPop = stackPop;
		this.stackPush = stackPush;
	}
	
	protected SimpleState getTail() {
		return tail;
	}
	
	protected SimpleState getHead() {
		return head;
	}
	
	protected char getSymbol() {
		return transitionSymbol;
	}

	protected char getStackPop() {
		return stackPop;
	}

	protected char getStackPush() {
		return stackPush;
	}

}
