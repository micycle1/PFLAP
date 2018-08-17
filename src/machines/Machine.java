package machines;

import p5.Arrow;
import p5.State;

public interface Machine {

	void addNode(State s);
	void deleteNode(State s);
	
	int totalTransitions();

	void setInitialState(State s);
	State getInitialState();

	void addTransition(Arrow a); //infer transtion from arrow based on machine type
	void removeTransition(Arrow a); //infer transtion from arrow based on machine type

	void beginStep(String input);
	boolean run(String input);
	State stepForward();
	void stepBackward(State s, String input);

	void debug();
}
