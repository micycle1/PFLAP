package model;

import p5.State;

public interface Machine {
	
//	void addNode(State s);
//	void deleteNode(State s);
//	
//	int totalTransitions();
//
//	void setInitialState(State s);
//	State getInitialState();
//
//	void addTransition(LogicalTransition a); //infer transtion from arrow based on machine type
//	void removeTransition(LogicalTransition a); //infer transtion from arrow based on machine type
	
	enum status {SUCCESS, NOTACCEPTING, FAIL} // NOTACCEPTING = consumed, but didn't finish on final state

	status run(String input);
	
	void beginStep(String input);

	
	/**
	 * @return The next state
	 */
	Integer stepForward();
	
	/**
	 * {@link main.Step Step} calls this to revert the machine's State.
	 * @param s Previous State
	 * @param input Previous input (one character longer)
	 */
	void stepBackward(Integer s, String input);
	
//	boolean assureUniqueTransition(LogicalTransition transition);
}
