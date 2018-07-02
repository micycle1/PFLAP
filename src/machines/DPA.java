package machines;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import p5.State;

public class DPA {
	/**
	 * Deterministic Pushdown Automaton Accept if accept state and stack empty
	 */

	private static Deque<Character> stack = new ArrayDeque<Character>();
	private static State initial;
	private static char initialStackSymbol;
	private static Map<State, Map<Character, Map<char[], State>>> transitions = new HashMap<>();

	public static void setInitialState(State s) {
		initial = s;
	}

	public static State getInitialState() {
		return initial;
	}

	public static void setInitialStackSymbol(char ss) {
		initialStackSymbol = ss;
	}

	public static char getInitialStackSymbol() {
		return initialStackSymbol;
	}

	public static void addNode(State s) {
		transitions.put(s, new HashMap<>());
	}

	public static void deleteNode(State s) {
		transitions.remove(s);
	}

	public static void addTransition(State tail, State head, Character symbol, Character pop, Character push) {
		//addNode(tail); // TODO
		Map<char[], State> c = new HashMap<>();
		c.put(new char[]{pop, push}, head);
		transitions.get(tail).put(symbol, c);
	}

	 public static void removeTransition(State tail, State head, Character symbol, Character pop, Character push) {
		 
		 //remove the state always
		 //remove 
		 
		 //transitions.get(tail).get(symbol).get(new char[] {pop, push})
		 if (transitions.get(tail).get(symbol).get(new char[] {pop, push}).size() > 1) {
			 //transitions.get(tail).get(new char[] {pop, push}).remove(head);
		 }
		 else {
			 transitions.get(tail).get(symbol).get(new char[] {pop, push});
		 }
	 }

	public static boolean run(String input) {
		State s = initial;
		stack.push(initialStackSymbol);
		while (!(input.isEmpty())) {
			char symbol = input.charAt(0);
			if (transitions.get(s).containsKey(symbol)
					&& transitions.get(s).get(symbol).keySet().contains(stack.peek())) {
				// s = transitions.get(s).get(symbol);
				input = input.substring(1);
			} else {
				return false;
			}
		}
		return s.isAccepting();
		// Notification?

		// if (stack.pop().equals(transitions.get(s).get()))
	}
	//
	// public static int totalTransitions() {
	// int n = 0;
	// for (Map<Character, State> m : transitions.values()) {
	// n += m.size();
	// }
	// return n;
	// }
	//
	public static void debug() {
		if (initial != null) {
			System.out.println("Initial: " + initial.getLabel());
		}
		for (State tail : transitions.keySet()) {
			for (Character c :transitions.get(tail).keySet()) {
				for (char[] a: transitions.get(tail).get(c).keySet()) {
					System.out.println( tail.getLabel() + " -> "+ c +"; "+ a[0] + "/" + a[1] + " -> " +
							transitions.get(tail).get(c).get(a).getLabel());
				}
			}
		}	
	}
}
