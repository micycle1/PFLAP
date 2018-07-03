package machines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import p5.State;

public class NDFA extends DFA {
	private static Map<State, Map<Character, ArrayList<State>>> transitions = new HashMap<>();
	private static State initial;

	public static void addTransition(State tail, State head, Character symbol) {
		if (!transitions.get(tail).containsKey(symbol)) {
			transitions.get(tail).put(symbol, new ArrayList<State>());
		}
		transitions.get(tail).get(symbol).add(head);
	}

	public static void removeTransition(State tail, State head, Character symbol) {
		if (transitions.get(tail).get(symbol).size() == 1) {
		transitions.get(tail).get(symbol).remove(head);
		}
		else {
			transitions.get(tail).remove(symbol);
		}
	}
	
	public static boolean run(String input) {
		State s = initial;
		while (!(input.isEmpty())) {
			char symbol = input.charAt(0);
			if (transitions.get(s).containsKey(symbol)) {
				s = transitions.get(s).get(symbol).get(0);
				input = input.substring(1);
			} else {
				return false;
			}
		}
		return s.isAccepting();
		// Notification?
	}
	
	public static int totalTransitions() {
		int n = 0;
		for (Map<Character, ArrayList<State>> m : transitions.values()) {
			for (ArrayList<State> h : m.values()) {
				n += h.size();
			}
		}
		return n;
	}

	public static void debug() {
		if (initial != null) {
			System.out.println("Initial: " + initial.getLabel());
		}
		System.out.println("Transitions #: " + totalTransitions());
		for (State tail : transitions.keySet()) {
			for (Character c : transitions.get(tail).keySet()) {
				for (State head : transitions.get(tail).get(c)) {
					System.out.println(tail.getLabel() + " -> " + c + " -> " + head.getLabel());
				}
			}
		}
		System.out.println("");
	}
}
