package machines;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.util.Pair;
import p5.Arrow;
import p5.State;

public class DPA implements Machine {
	/**
	 * Deterministic Pushdown Automaton Accept iff accept state and stack empty
	 */

	private Deque<Character> stack = new ArrayDeque<Character>();
	private State initial;
	private char initialStackSymbol;
	private Map<State, Map<Pair<Character, char[]>, State>> transitions = new HashMap<>();
	
	public DPA() {
	}

	public void setInitialState(State s) {
		initial = s;
	}

	public State getInitialState() {
		return initial;
	}

	public void setInitialStackSymbol(char ss) {
		initialStackSymbol = ss;
	}

	public char getInitialStackSymbol() {
		return initialStackSymbol;
	}

	public void addNode(State s) {
		transitions.put(s, new HashMap<>());
	}

	public void deleteNode(State s) {
		transitions.remove(s);
	}
	
	@Override
	public void addTransition(Arrow a) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeTransition(Arrow a) {
		// TODO Auto-generated method stub
		
	}

//	public void addTransition(State tail, State head, Character symbol, Character pop, Character push) {
//		Pair<Character, char[]> key = new Pair<Character, char[]>(symbol, new char[]{pop, push});
//		// if (!(transitions.get(tail).containsKey(symbol))) {
//		// transitions.get(tail).put(symbol, new HashMap<char[], State>());
//		// }
//		// if (!(transitions.get(tail).get(symbol).containsKey(new char[] {pop,
//		// push}))) { //indicate it replaces
//		// transitions.get(tail).get(symbol).put(new char[] {pop, push}, head);
//		// }
//		// else {
//		// //notify transition already exists (non-determinism)
//		// }
//
//		// transitions.get(tail).keySet().contains(key)
//
//		// System.out.println(key.getKey());
//		// System.out.println(key.getValue()[0]);
//		// System.out.println(key.getValue()[1]);
//		//
//		// if (!(transitions.get(tail).keySet().)) {
//		// transitions.get(tail).put(key, head);
//		// } else {
//		// System.out.println("already key");
//		// // notify transition already exists (non-determinism)
//		// }
//
//		if (transitions.get(tail).size()  == 0) {
//			transitions.get(tail).put(key, head);
//		}
//		check : for (Pair<Character, char[]> key1 : transitions.get(tail).keySet()) {
//			if (key1.equals(key)) {
//				System.out.println("already key");
//				break check;
//			}
//			transitions.get(tail).put(key, head);
//		}
//	}

//	public void removeTransition(State tail, State head, Character symbol, Character pop, Character push) {

		// remove the state always
		// remove

		// transitions.get(tail).get(symbol).get(new char[] {pop, push})
		// if (transitions.get(tail).get(symbol).size() > 1) {
		// //transitions.get(tail).get(new char[] {pop, push}).remove(head);
		// }
		// else {
		// transitions.get(tail).get(symbol).get(new char[] {pop, push});
		// }
//	}

	public boolean run(String input) {
//		State s = initial;
//		stack.push(initialStackSymbol);
//		while (!(input.isEmpty())) {
//			char symbol = input.charAt(0);
//			if (transitions.get(s).containsKey(symbol)
//					&& transitions.get(s).keySet() {
//				// s = transitions.get(s).get(symbol);
//				input = input.substring(1);
//			} else {
//				return false;
//			}
//		}
//		return s.isAccepting();
		return false;
	}

	public int totalTransitions() {
		int n = 0;
		for (Map<Pair<Character, char[]>, State> m : transitions.values()) {
			n += m.size();
		}
		return n;
	}

	public void debug() {
		if (initial != null) {
			System.out.println("Initial: " + initial.getLabel());
		}
		System.out.println("Transitions: " + String.valueOf(totalTransitions()));
		for (State tail : transitions.keySet()) {
			for (Pair<Character, char[]> key : transitions.get(tail).keySet()) {
				System.out.println(tail.getLabel() + " -> " + key.getKey() + "; " + key.getValue()[0] + "/"
						+ key.getValue()[1] + " -> " + transitions.get(tail).get(key).getLabel());
			}
		}
		System.out.println("");
	}
}
