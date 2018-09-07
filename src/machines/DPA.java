package machines;

import java.util.LinkedList;
import java.util.Queue;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import p5.Arrow;
import p5.Notification;
import p5.State;

/**
 * <p>
 * <b>Deterministic Pushdown Automaton</b>
 * <p>
 * Accepts iff on accept state and stack empty.
 */

public class DPA implements Machine {

	private Queue<Character> stack;
	private State initial;
	private MutableNetwork<State, Arrow> transitionGraph;
	private Character initialStackSymbol;
	private String stepInput;

	public DPA() {
		stack = new LinkedList<Character>();
		transitionGraph = NetworkBuilder.directed().allowsParallelEdges(true).expectedNodeCount(100)
				.expectedEdgeCount(200).allowsSelfLoops(true).build();
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

	public void addNode(State s) {
		transitionGraph.addNode(s);
	}

	public void deleteNode(State s) {
		transitionGraph.removeNode(s);
	}

	@Override
	public void addTransition(Arrow a) {
		transitionGraph.addEdge(a.getTail(), a.getHead(), a);
	}

	@Override
	public void removeTransition(Arrow a) {
		transitionGraph.removeEdge(a);
	}
	
	public void beginStep(String input) {
		stack.clear();
		stack.add(initialStackSymbol);
		State s = initial;
		stepInput = input;
	}
	
	@Override
	public State stepForward() {
		if (!stepInput.isEmpty()) {
//			char symbol = input.charAt(0);
//			stepInput = input.substring(1);
			
		}
		
		return initial;
	}
	
	@Override
	public void stepBackward(State s, String input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean run(String input) {
		debug(); //remove
		
		stack.clear();
		stack.add(initialStackSymbol);
		State s = initial;

		while (!input.isEmpty()) {
			char symbol = input.charAt(0);
			input = input.substring(1);

			checkOneState : {
				for (Arrow a : transitionGraph.outEdges(s)) {
					if ((a.getSymbol() == symbol || symbol == ' ')
							&& (a.getStackPop() == stack.peek() || stack.peek() == ' ')) {
						// catch stack empty
						System.out.println(input + ", " + a.getStackPop() + "/" + a.getStackPush() + "; " + stack.peek()
								+ "; " + stack.size());
						stack.poll();
						if (!(a.getStackPush() == ' ')) {
							stack.add(a.getStackPush());
						}
						s = a.getHead();
						break checkOneState;
					}
				}
				Notification.addNotification(main.Consts.notificationData.machineRejected);
				return false;
			}
		}
		return (s.isAccepting() && (stack.isEmpty() || stack.poll() == ' '));
	}

	public int totalTransitions() {
		return transitionGraph.edges().size();
	}

	@Override
	public void debug() {
		for (char c :stack) {
			System.out.println(c);
		}
	}
}