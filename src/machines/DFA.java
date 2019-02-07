package machines;

import main.Step;
import model.LogicalTransition;
import model.Machine;
import model.Model;

/**
 * <p><b>Deterministic Finite Automaton</b></p>
 * Accepts iff accepting state and input consumed
 */
public class DFA implements Machine {

	private int stepState;
	private String stepInput;
	private int stepIndex; // index to slice stepinput

	public DFA() {
	}

	@Override
	public status run(String input) { // todo lambda

		int s = Model.initialState;
		char symbol;

		while (!(input.isEmpty())) {
			symbol = input.charAt(0);
			boolean ok = false;
			for (LogicalTransition t : Model.transitionGraph.outEdges(s)) {
				if (t.getSymbol() == symbol) {
					input = input.substring(1);
					s = t.head;
					ok = true;
					break;
				}
			}
			if (!ok) {
				return status.FAIL;
			}
		}
		if (Model.acceptingStates.contains(s)) {
			return status.SUCCESS;
		} else {
			return status.NOTACCEPTING;
		}
	}

	public void beginStep(String input) {
		stepState = Model.initialState;
		stepInput = input;
		stepIndex = 0;
	}

	@Override
	public Integer stepForward() {
		Integer prevState = stepState;

		if (stepIndex < stepInput.length()) {
			char symbol = stepInput.charAt(stepIndex);

			boolean ok = false;
			for (LogicalTransition t : Model.transitionGraph.outEdges(prevState)) {
				if (t.getSymbol() == symbol) {
					stepState = t.head;
					ok = true;
					break;
				}
			}
			if (!ok) {
				Step.setMachineOutcome(false);
			} else {
				stepIndex++;
			}
			return stepState;

		} else {
			Step.setMachineOutcome(Model.isAccepting(stepState));
			stepState = prevState;
			return prevState;
		}
	}

	@Override
	public void stepBackward(Integer s) {
		stepState = s;
		stepIndex--;
	}
}