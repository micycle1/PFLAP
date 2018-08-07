package machines;

import static main.Consts.notificationData.machineAccepted;
import static main.Consts.notificationData.machineRejected;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import p5.Arrow;
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

	private Table<State, Character, State> transitionTable;
	private State initial;

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

	public void addTransition(Arrow a) {
		System.out.print("add");
		transitionTable.put(a.getTail(), a.getSymbol(), a.getHead());
	}

	public void removeTransition(Arrow a) {
		transitionTable.remove(a.getTail(), a.getSymbol());
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
			input = input.substring(1);
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

	public boolean step() { // TODO + DPA
		return false;
	}
	public boolean fastRun() { // TODO
		return false;
	}

	public int totalTransitions() {
		return transitionTable.cellSet().size();
	}

	public void debug() {
		/**
		 * Prints all transitions in machine.
		 */
		// TODO
	}
}