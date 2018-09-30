package machines;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import main.Step;
import p5.Arrow;
import p5.State;

/**
 * <b>Moore Machine</b>
 * <p>Each transition specifies only a symbol to consume.
 * Unlike other machines, states must specify an output symbol.
 * Transitioning to/from? a state appends its output symbol to the machine output.
 */

public final class Moore implements Machine {

	private Table<State, Character, State> transitionTable;
	private State initial, stepState;
	private String output, stepInput;

	public Moore() {
		transitionTable = HashBasedTable.create();
	}

	@Override
	public void addNode(State s) {
	}

	@Override
	public void deleteNode(State s) {
	}

	@Override
	public int totalTransitions() {
		return transitionTable.cellSet().size();
	}

	@Override
	public void setInitialState(State s) {
		initial = s;
	}

	@Override
	public State getInitialState() {
		return initial;
	}

	@Override
	public void addTransition(Arrow a) {
		transitionTable.put(a.getTail(), a.getSymbol(), a.getHead());
	}

	@Override
	public void removeTransition(Arrow a) {
		transitionTable.remove(a.getTail(), a.getSymbol());

	}

	@Override
	public boolean run(String input) {
		State s = initial;
		output = "";
		while (!input.isEmpty()) {
			char symbol = input.charAt(0);
			input = input.substring(1);
			if (transitionTable.contains(s, symbol)) {
				output += s.getMoorePush();
				s = transitionTable.get(s, symbol);
			} else {
				return true;
			}
		}
		return true;
	}

	@Override
	public void beginStep(String input) {
		stepState = initial;
		stepInput = input;
		output = "";
	}

	@Override
	public State stepForward() {
		State prevState = stepState;
		if (!stepInput.isEmpty()) {
			char symbol = stepInput.charAt(0);
			stepInput = stepInput.substring(1);
			if (transitionTable.contains(stepState, symbol)) {
				output += stepState.getMoorePush();
				stepState = transitionTable.get(stepState, symbol);
				return stepState;
			} else {
				Step.setMachineOutcome(true);
				stepState = prevState;
				return stepState;
			}
		} else {
			Step.setMachineOutcome(true);
			stepState = prevState;
			return stepState;
		}
	}

	@Override
	public void stepBackward(State s, String input) {
		output = output.substring(0, (output.length() - 1) - (stepState.getMoorePush().length() - 1));
		stepState = s;
		stepInput = input;
	}

	public String getOutput() {
		return output;
	}

	@Override
	public boolean testUniqueTransition(Arrow transition, char symbol, char stackPop, String stackPush) {
		return !transitionTable.contains(transition.getTail(), symbol);
	}

}
