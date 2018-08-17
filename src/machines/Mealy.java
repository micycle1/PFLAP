package machines;

import p5.Arrow;
import p5.State;

/**
 * <b>Mealy Machine</b>
 * <p>Each transition specifies and input and output symbol.
 * An output tape is built up as the machine transitions.
 */

public final class Mealy implements Machine {

	@Override
	public void addNode(State s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteNode(State s) {
		// TODO Auto-generated method stub

	}

	@Override
	public int totalTransitions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setInitialState(State s) {
		// TODO Auto-generated method stub

	}

	@Override
	public State getInitialState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTransition(Arrow a) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTransition(Arrow a) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean run(String input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void debug() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginStep(String input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean stepForward() {
		// TODO Auto-generated method stub
		return false;
	}

}
