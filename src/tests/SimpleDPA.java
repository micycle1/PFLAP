package tests;

import java.util.LinkedList;
import java.util.Queue;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

final class SimpleDPA {

	/**
	 * Deterministic Pushdown Automaton Accept iff accept state and stack empty
	 */

	private Queue<Character> stack;
	private SimpleState initial;
	private MutableNetwork<SimpleState, SimpleArrow> transitionGraph;
	private Character initialStackSymbol;

	public SimpleDPA() {
		stack = new LinkedList<Character>();
		transitionGraph = NetworkBuilder.directed().allowsParallelEdges(true).expectedNodeCount(100)
				.expectedEdgeCount(200).allowsSelfLoops(true).build();
	}

	protected void setInitialState(SimpleState s1) {
		initial = s1;
	}

	protected SimpleState getInitialState() {
		return initial;
	}

	protected void setInitialStackSymbol(char ss) {
		initialStackSymbol = ss;
	}

	protected void addNode(SimpleState s1) {
		transitionGraph.addNode(s1);
	}

	protected void deleteNode(SimpleState s) {
		transitionGraph.removeNode(s);
	}

	protected void addTransition(SimpleArrow a) {
		transitionGraph.addEdge(a.getTail(), a.getHead(), a);
	}

	protected void removeTransition(SimpleArrow a) {
		transitionGraph.removeEdge(a);

	}

	protected boolean run(String input) {
		stack.clear();
		stack.add(initialStackSymbol);
		SimpleState s = initial;

		while (!input.isEmpty()) {
			char symbol = input.charAt(0);
			input = input.substring(1);

			checkOneState : {
				for (SimpleArrow a : transitionGraph.outEdges(s)) {
					if ((a.getSymbol() == symbol || symbol == ' ')
							&& (a.getStackPop() == stack.peek() || stack.peek() == ' ')) {
						// catch stack empty
						System.out.println(input + ", " + a.getStackPop() + "/" + a.getStackPush()+"; "+stack.peek() + "; "+stack.size());
						stack.poll();
						if (!(a.getStackPush() == ' ')) {
							stack.add(a.getStackPush());
						}
						s = a.getHead();

						break checkOneState;
					}
				}
				return false;
			}
		}
		return (s.isAccepting() && (stack.isEmpty() || stack.poll() == ' '));
	}

	protected int totalTransitions() {
		return transitionGraph.edges().size();
	}

	protected void debug() {
	}
}
