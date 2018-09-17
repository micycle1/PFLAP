package machines;

import java.util.LinkedList;
import java.util.Queue;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import main.PFLAP;
import main.Step;
import p5.Arrow;
import p5.Notification;
import p5.State;

import controlP5.Toggle;

import static main.Consts.lambda;

/**
 * <p><b> Deterministic Pushdown Automaton</b>
 * <p> Accepts on accept state or stack empty.
 */

public class DPA implements Machine {

	private Queue<Character> stack, previousStack;
	private State initial, stepState;
	private MutableNetwork<State, Arrow> transitionGraph;
	private Character initialStackSymbol;
	private String stepInput;
	private static final Toggle toggleAcceptByStackEmpty, toggleAcceptByAcceptState;

	static {
		toggleAcceptByStackEmpty = PFLAP.cp5.addToggle("DPA: Accept by\n Empty Stack").setValue(true).setColorLabel(0)
				.setPosition(5, 5);

		toggleAcceptByAcceptState = PFLAP.cp5.addToggle("DPA: Accept by\n Accepting State").setValue(false)
				.setColorLabel(0).setPosition(5, 50);
	}

	public static void hideUI() {
		toggleAcceptByAcceptState.hide();
		toggleAcceptByStackEmpty.hide();
	}

	public static void showUI() {
		toggleAcceptByAcceptState.show();
		toggleAcceptByStackEmpty.show();
	}

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

	private void setStepStack() {
		String stackString = "";
		for (char c : stack) {
			stackString += c;
		}
		Step.setStack(stackString);
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
		stepState = initial;
		stepInput = input;
		previousStack = new LinkedList<>();
		setStepStack();
	}

	@Override
	public State stepForward() {
		State prevState = stepState;
		if (!stepInput.isEmpty()) {
			previousStack = new LinkedList<>(stack);
			char symbol = stepInput.charAt(0);
			stepInput = stepInput.substring(1);
			checkOneState : {
				for (Arrow a : transitionGraph.outEdges(stepState)) {
					if ((a.getSymbol() == symbol) && (a.getStackPop() == stack.peek() || a.getStackPop() == lambda)) {
						stack.poll();
						for (Character c : a.getStackPush().toCharArray()) {
							if (c != lambda) {
								stack.add(c);
							}
						}
						stepState = a.getHead();
						break checkOneState;
					}
				}
				stack = new LinkedList<>(previousStack);
				Step.setMachineOutcome(false);
				stepState = prevState;
			}
			setStepStack();
			return stepState;

		} else {
			Step.setMachineOutcome(
					(stepState.isAccepting() && toggleAcceptByAcceptState.getBooleanValue())
					|| (stack.isEmpty() && toggleAcceptByStackEmpty.getBooleanValue()));
			stepState = prevState;
			setStepStack();
			return prevState;
		}
	}

	@Override
	public void stepBackward(State s, String input) {
		stepState = s;
		stepInput = input;
		stack = new LinkedList<>(previousStack);
		setStepStack();
	}

	@Override
	public boolean run(String input) {
		stack.clear();
		stack.add(initialStackSymbol);
		State s = initial;

		while (!input.isEmpty()) {
			char symbol = input.charAt(0);
			input = input.substring(1);

			checkOneState : {
				for (Arrow a : transitionGraph.outEdges(s)) {
					if ((a.getSymbol() == symbol) && (a.getStackPop() == stack.peek() || a.getStackPop() == lambda)) {
						stack.poll();
						for (Character c : a.getStackPush().toCharArray()) {
							if (c != lambda) {
								stack.add(c);
							}
						}
						s = a.getHead();
						break checkOneState;
					}
				}
				Notification.addNotification(main.Consts.notificationData.machineRejected);
				return false;
			}
		}
		if ((s.isAccepting() && toggleAcceptByAcceptState.getBooleanValue())
				|| (stack.isEmpty() && toggleAcceptByStackEmpty.getBooleanValue())) {
			Notification.addNotification(main.Consts.notificationData.machineAccepted);
		} else {
			Notification.addNotification(main.Consts.notificationData.machineRejected);
		}
		return (s.isAccepting() && toggleAcceptByAcceptState.getBooleanValue())
				|| (stack.isEmpty() && toggleAcceptByStackEmpty.getBooleanValue());
	}

	public int totalTransitions() {
		return transitionGraph.edges().size();
	}

	@Override
	public void debug() {
		System.out.println("Stack:");
		for (char c : stack) {
			System.out.println(c);
		}
	}
}