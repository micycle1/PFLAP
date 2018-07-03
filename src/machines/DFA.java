package machines;

import java.util.HashMap;
import java.util.Map;

import p5.State;

public class DFA {
	/**
	 * Deterministic Finite Automaton
	 */

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
	}

	public static void addTransition(State tail, State head, Character symbol) {
			transitions.get(tail).put(symbol, head); //OVerwrites same symbol
	}

	public static void removeTransition(State tail, State head, Character symbol) {
		transitions.get(tail).remove(symbol);
	}

	public static boolean run(String input) {
		//System.out.println("⊢");
		State s = initial;
		while (!(input.isEmpty())) {
			char symbol = input.charAt(0);
			if (transitions.get(s).containsKey(symbol)) {
				s = transitions.get(s).get(symbol);
				input = input.substring(1);
			} else {
				return false;
			}
		}
		return s.isAccepting();
		// Notification?
	}
	
	public static boolean step() { //TODO + DPA
		return false;
	}
	public static boolean fastRun() { //TODO
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
		if (initial != null) {
			System.out.println("Initial: " + initial.getLabel());
		}
		System.out.println("Transtions #: " + totalTransitions());
		for (State s : transitions.keySet()) {
			for (Character c : transitions.get(s).keySet()) {
				System.out.println(s.getLabel() + " -> " + c + " -> " + transitions.get(s).get(c).getLabel());
			}
		}
		//entryset
		System.out.println("");
	}
}