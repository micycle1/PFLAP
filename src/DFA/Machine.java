package DFA;

import java.util.HashMap;
import java.util.Map;

import p5.State;

public class Machine {

	private static Map<State, Map<Character, State>> transitions = new HashMap<>();
	private static State initial;

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
		// delete transitions to this node
	}

	public static void addTransition(State tail, State head, Character symbol) {
		if (!transitions.get(tail).containsKey(symbol)) {
			transitions.get(tail).put(symbol, head);
		}
	}

	public static void removeTransition(State tail, State head, Character symbol) {
		transitions.get(tail).remove(symbol);
		// removing head node
		// delete tail's references to it
	}

	public static boolean run(String symbols) {
		State s = initial;
		while (!(symbols.isEmpty())) {
			System.out.println(s.label);
			char symbol = symbols.charAt(0);
			if (transitions.get(s).containsKey(symbol)) {
				s = transitions.get(s).get(symbol);
				symbols = symbols.substring(1);
			} else {
				return false;
			}
		}
		return s.isAccepting();
		// Notification?
	}

	public static int totalTransitions() {
		int n = 0;
		for (Map<Character, State> m : transitions.values()) {
			n += m.size();
		}
		return n;
	}

	public static void debug() {
		if (initial != null) {
			System.out.println("Initial: " + initial.label);
		}
		System.out.println("Transtions #: " + totalTransitions());
		for (State s : transitions.keySet()) {
			for (Character c : transitions.get(s).keySet()) {
				System.out.println(s.label + " -> " + c + " -> " + transitions.get(s).get(c).label);
			}
		}
		System.out.println("");
	}
}