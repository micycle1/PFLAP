package machines;

import java.util.HashMap;
import java.util.Map;

import main.Consts;
import p5.Notification;
import p5.State;

public class DFA {
	/**
	 * Deterministic Finite Automaton
	 */

	private static Map<State, Map<Character, State>> transitions = new HashMap<>();
	private static State initial;
	private static String initialInput;

	public static void setInitialState(State s) {
		initial = s;
	}

	public static State getInitialState() {
		return initial;
	}

	public static void addNode(State s) {
		transitions.put(s, new HashMap<>());
	}

	public static void deleteNode(State s) {
		transitions.remove(s);
	}

	public static void addTransition(State tail, State head, Character symbol) {
		transitions.get(tail).put(symbol, head); // OVerwrites same symbol
	}

	public static void removeTransition(State tail, State head, Character symbol) {
		transitions.get(tail).remove(symbol);
	}

	public static boolean run(String input) {
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

	public static boolean step() { // TODO + DPA
		return false;
	}
	public static boolean fastRun() { // TODO
		return false;
	}

	public static int totalTransitions() {
		int n = 0;
		for (Map<Character, State> m : transitions.values()) {
			n += m.size();
		}
		return n;
	}

	public static void debug() {
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