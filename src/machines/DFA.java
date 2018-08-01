package machines;

import java.util.HashMap;
import java.util.Map;

import p5.Arrow;
import p5.Notification;
import p5.State;

/**
 * <p> <b>Deterministic Finite Automaton</b></p>
 * <p> Accepts iff accepting state and input consumed
 */

public class DFA implements Machine {

	private Map<State, Map<Character, State>> transitions;
	private State initial;
	private String initialInput;
	
	public DFA() {
		transitions = new HashMap<>();
	}

	public void setInitialState(State s) {
		initial = s;
	}

	public State getInitialState() {
		return initial;
	}

	public void addNode(State s) {
		transitions.put(s, new HashMap<>());
	}

	public void deleteNode(State s) {
		transitions.remove(s);
	}

	public void addTransition(Arrow a) {
		transitions.get(a.getTail()).put(a.getSymbol(), a.getHead()); // OVerwrites same symbol
	}
	
	public void removeTransition(Arrow a) {
		transitions.get(a.getTail()).remove(a.getSymbol());
	}

	public boolean run(String input) {
		System.out.println("~~~~~~~~~~");
		initialInput = input;
		State s = initial;
		while (!(input.isEmpty())) {
			char symbol = input.charAt(0);
			if (transitions.get(s).containsKey(symbol)) {
				System.out.println("[" + input + "] " + s.getLabel() + " -> " + symbol + " -> "
						+ transitions.get(s).get(symbol).getLabel());
				s = transitions.get(s).get(symbol);
				input = input.substring(1);
			} else {
				System.out.println("Attempting: [" + input + "] " + s.getLabel() + " -> " + symbol);
				System.out.println("'" + initialInput + "'" + " REJECTED. (No " + symbol + " transition on state "
						+ s.getLabel() + ").");

				Notification.addNotification(main.Consts.notificationData.machineRejected);
				return false;
			}
		}
		if (s.isAccepting()) {
			Notification.addNotification(main.Consts.notificationData.machineAccepted);
			System.out.println("'" + initialInput + "'" + " ACCEPTED on state " + s.getLabel() + ".");
		} else {
			Notification.addNotification(main.Consts.notificationData.machineRejected);
			System.out.println("'" + initialInput + "'" + " REJECTED. (Input consumed but state " + s.getLabel()
					+ " is not accepting).");
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
		int n = 0;
		for (Map<Character, State> m : transitions.values()) {
			n += m.size();
		}
		return n;
	}

	public void debug() {
		/**
		 * Prints all transitions in machine.
		 */
		if (initial != null) {
			System.out.println("Initial: " + initial.getLabel());
		}
		System.out.println("Transtions #: " + totalTransitions());
		for (State s : transitions.keySet()) {
			for (Character c : transitions.get(s).keySet()) {
				System.out.println(s.getLabel() + " -> " + c + " -> " + transitions.get(s).get(c).getLabel());
			}
		}
		System.out.println("");
	}

}