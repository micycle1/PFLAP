package machines;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import main.Step;
import model.Machine;
import p5.AbstractArrow;
import p5.State;

/**
 * <b>Mealy Machine</b>
 * <p>Each transition specifies an input and output symbol.
 * An output tape is built up as the machine transitions.
 */
public final class Mealy implements Machine {

	private String output = "", stepInput;
	private State initial, stepState;
	private MutableNetwork<State, AbstractArrow> transitionGraph;

	public Mealy() {
		transitionGraph = NetworkBuilder.directed().allowsParallelEdges(true).expectedNodeCount(100)
				.expectedEdgeCount(200).allowsSelfLoops(true).build();
	}

	@Override
	public void addNode(State s) {
		transitionGraph.addNode(s);
	}

	@Override
	public void deleteNode(State s) {
		transitionGraph.removeNode(s);
	}

	@Override
	public int totalTransitions() {
		return transitionGraph.edges().size();
	}

	@Override
	public void setInitialState(State s) {
		initial = s;
	}

	@Override
	public State getInitialState() {
		return initial;
	}

	@Override
	public void addTransition(AbstractArrow a) {
		transitionGraph.addEdge(a.getTail(), a.getHead(), a);
	}

	@Override
	public void removeTransition(AbstractArrow a) {
		transitionGraph.removeEdge(a);
	}

	@Override
	public boolean run(String input) {
		State s = initial;
		output = "";
		while (!input.isEmpty()) {
			char symbol = input.charAt(0);
			input = input.substring(1);
			for (AbstractArrow a : transitionGraph.outEdges(s)) {
				if (a.getSymbol() == symbol) {
					output += a.getStackPush();
					s = a.getHead();
				} else {
					return true;
				}
			}
		}
		return true;
	}

	@Override
	public void beginStep(String input) {
		stepInput = input;
		stepState = initial;
		output = "";
	}

	@Override
	public State stepForward() {
		State prevState = stepState;
		if (!stepInput.isEmpty()) {
			char symbol = stepInput.charAt(0);
			stepInput = stepInput.substring(1);
			for (AbstractArrow a : transitionGraph.outEdges(stepState)) {
				if (a.getSymbol() == symbol) {
					output += a.getStackPush();
					stepState = a.getHead();
					return stepState;
				} else {
					return prevState;
				}
			}
		}
		Step.setMachineOutcome(true);
		stepState = prevState;
		return stepState;
	}

	@Override
	public void stepBackward(State s, String input) {
		output = output.substring(0, (output.length() - 1) - (stepState.getMoorePush().length() - 1));
		stepState = s;
		stepInput = input;
	}

	public String getOutput() {
		return output;
	}

	@Override
	public boolean testUniqueTransition(AbstractArrow transition, char symbol, char stackPop, String stackPush) {
		for (AbstractArrow a : transitionGraph.outEdges(transition.getTail())) {
			if (a.getSymbol() == symbol && a.getStackPush().equals(stackPush)) {
				return false;
			}
		}
		return true;
	}

}
