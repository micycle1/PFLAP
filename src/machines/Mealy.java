package machines;

import main.Step;
import model.LogicalTransition;
import model.Machine;
import model.Model;

/**
 * <b>Mealy Machine</b>
 * <p>Each transition specifies an input and output symbol.
 * An output tape is built up as the machine transitions.
 */
public final class Mealy implements Machine {

	private String output = "", stepInput;
	private Integer stepState, stepIndex;

	public Mealy() {
	}

	@Override
	public status run(String input) {

		int s = Model.getInitialState();
		char symbol;
		output = "";

		while (!(input.isEmpty())) {
			symbol = input.charAt(0);
			boolean ok = false;
			for (LogicalTransition t : Model.transitionGraph.outEdges(s)) {
				if (t.getSymbol() == symbol) {
					output += t.getStackPush();
					input = input.substring(1);
					s = t.head;
					ok = true;
					break;
				}
			}
			if (!ok) {
				return status.COMPLETE;
			}
		}
		return status.COMPLETE;
	}

	@Override
	public void beginStep(String input) {
		stepState = Model.getInitialState();
		stepInput = input;
		output = "";
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
					output += t.getStackPush();
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
			Step.setMachineOutcome(true);
			stepState = prevState;
			return prevState;
		}
	}

	@Override
	public void stepBackward(Integer s) {
		stepIndex--;
		for (LogicalTransition t : Model.transitionGraph.edgesConnecting(s, stepState)) {
			if (t.getSymbol() == stepInput.charAt(stepIndex) && t.getTail().equals(s)) {
				output = output.substring(0, output.length() - t.getStackPush().length());
				break;
			}
		}
		stepState = s;
	}

	public String getOutput() {
		return output;
	}
	
}
