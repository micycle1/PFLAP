package machines;

import p5.AbstractArrow;
import p5.State;

public interface Machine {
	
	void addNode(State s);
	void deleteNode(State s);
	
	int totalTransitions();

	void setInitialState(State s);
	State getInitialState();

	void addTransition(AbstractArrow a); //infer transtion from arrow based on machine type
	void removeTransition(AbstractArrow a); //infer transtion from arrow based on machine type

	void beginStep(String input);
	boolean run(String input);
	
	/**
	 * @return The next state
	 */
	State stepForward();
	
	/**
	 * {@link main.Step Step} calls this to revert the machine's State.
	 * @param s Previous State
	 * @param input Previous input (one character longer)
	 */
	void stepBackward(State s, String input);
	
	boolean testUniqueTransition(AbstractArrow transition, char symbol, char stackPop, String stackPush);
}
