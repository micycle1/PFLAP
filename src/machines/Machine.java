package machines;

import p5.State;

public interface Machine {

	void addNode(State s);
	void deleteNode(State s);
	
	int totalTransitions();

	void setInitialState(State s);
	State getInitialState();

//	void addTransition(Arrow a); //infer transtion from arrow based on machine type
//	void removeTransition(Arrow a); //infer transtion from arrow based on machine type

	boolean run(String input);



	void debug();
}
