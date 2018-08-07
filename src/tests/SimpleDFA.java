package tests;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * <p>
 * <b>Deterministic Finite Automaton</b>
 * </p>
 * <p>
 * Accepts iff accepting state and input consumed
 */

final class SimpleDFA {

	Table<SimpleState, Character, SimpleState> transitionTable;
	private SimpleState initial;

	public SimpleDFA() {
		transitionTable = HashBasedTable.create();
	}

	public void setInitialSimpleState(SimpleState s) {
		initial = s;
	}

	public SimpleState getInitialSimpleState() {
		return initial;
	}

	public void addTransition(SimpleArrow a) {
		transitionTable.put(a.getTail(), a.getSymbol(), a.getHead());
	}

	public void removeTransition(SimpleArrow a) {
		transitionTable.remove(a.getTail(), a.getSymbol());
	}

	public boolean run(String input) {
		System.out.println("~~~~~~~~~~");
		SimpleState s = initial;

		while (!(input.isEmpty())) {
			char symbol = input.charAt(0);
			s = transitionTable.get(s, symbol);
			System.out.println(symbol);
			if (s == null) {
				return false;
			}
			input = input.substring(1);

		}
		System.out.println("~~~~~~~~~~");
		return s.isAccepting();
	}

	public int totalTransitions() {
		return transitionTable.cellSet().size();
	}

}
