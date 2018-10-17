package machines;

import static main.Consts.notificationData.machineAccepted;
import static main.Consts.notificationData.machineRejected;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import main.Consts;
import main.Step;

import p5.AbstractArrow;
import p5.Notification;
import p5.State;

/**
 * <p>
 * <b>Deterministic Finite Automaton</b>
 * </p>
 * <p>
 * Accepts iff accepting state and input consumed
 */

public class DFA implements Machine {

	private final Table<State, Character, State> transitionTable;
	private State initial, stepState;
	private String stepInput;

	public DFA() {
		transitionTable = HashBasedTable.create();
	}

	public void setInitialState(State s) {
		initial = s;
	}

	public State getInitialState() {
		return initial;
	}

	public void addNode(State s) {
	}

	public void deleteNode(State s) {
	}

	public void addTransition(AbstractArrow a) {
		transitionTable.put(a.getTail(), a.getSymbol(), a.getHead());
	}

	public void removeTransition(AbstractArrow a) {
		transitionTable.remove(a.getTail(), a.getSymbol());
	}

	public void beginStep(String input) {
		stepState = initial;
		stepInput = input;
	}

	@Override
	public State stepForward() {
		State prevState = stepState;
		if (!stepInput.isEmpty()) {
			char symbol = stepInput.charAt(0);
			stepInput = stepInput.substring(1);

			if (transitionTable.row(prevState).containsKey(symbol)) {
				stepState = transitionTable.get(stepState, symbol);
			} else {
				if (transitionTable.row(prevState).containsKey(Consts.lambda)) {
					stepState = transitionTable.get(prevState, Consts.lambda);
					return stepState;
				} else {
					Step.setMachineOutcome(false);
				}
			}
			return stepState;

		} else {
			Step.setMachineOutcome(stepState.isAccepting());
			stepState = prevState;
			return prevState;
		}
	}

	@Override
	public void stepBackward(State s, String input) {
		stepState = s;
		stepInput = input;
	}

	@Override
	public boolean run(String input) {
		System.out.println("~~~~~~~~~~");

		final String initialInput = input;
		State s = initial;

		while (!(input.isEmpty())) {
			char symbol = input.charAt(0);
			State old = s;
			s = transitionTable.get(s, symbol);
			System.out.println("Attempting: [" + input + "] " + old.getLabel() + " -> " + symbol);
			if (s == null) {
				System.out.println("'" + initialInput + "'" + " REJECTED. (No " + symbol + " transition).");
				Notification.addNotification(machineRejected);
				return false;
			}
			System.out.println("[" + input + "] " + old.getLabel() + " -> " + symbol + " -> " + s.getLabel());
			if (symbol != Consts.lambda) {
				input = input.substring(1);
			}

		}

		if (s.isAccepting()) {
			Notification.addNotification(machineAccepted);
			System.out.println("'" + initialInput + "'" + " ACCEPTED on state " + s.getLabel() + ".");
		} else {
			System.out.println("'" + initialInput + "'" + " REJECTED. (Input consume but state " + s.getLabel()
					+ " is not accepting).");
			Notification.addNotification(machineRejected);
		}

		System.out.println("~~~~~~~~~~");
		return s.isAccepting();
	}

	@Override
	public int totalTransitions() {
		return transitionTable.cellSet().size();
	}

	@Override
	public boolean testUniqueTransition(AbstractArrow transition, char symbol, char stackPop, String stackPush) {
		return !transitionTable.contains(transition.getTail(), symbol);
	}

}