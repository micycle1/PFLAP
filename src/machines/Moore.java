package machines;

import main.Step;
import main.PFLAP.PApplet;
import model.LogicalTransition;
import model.Machine;
import model.Model;

/**
 * <b>Moore Machine</b>
 * <p>Each transition specifies only a symbol to consume.
 * Unlike other machines, states must specify an output symbol.
 * Transitioning from a state appends its output symbol to the machine output.
 */

public final class Moore implements Machine {

	private Integer stepIndex, stepState;
	private String output, stepInput;

	public Moore() {
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
					input = input.substring(1);
					s = t.head;
					output += PApplet.view.getStateByID(s).getMoorePush();
					ok = true;
					break;
				}
			}
			if (!ok) {
				break;
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
					stepState = t.head;
					output += PApplet.view.getStateByID(prevState).getMoorePush();
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
		output = output.substring(0, output.length() - PApplet.view.getStateByID(s).getMoorePush().length());
		stepState = s;
	}

	public String getOutput() {
		return output;
	}
	
}
