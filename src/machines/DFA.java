package machines;

import static main.Consts.notificationData.machineAccepted;
import static main.Consts.notificationData.machineRejected;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import main.Consts;
import main.Step;
import model.LogicalTransition;
import model.Machine;
import model.Model;
import p5.AbstractArrow;
import p5.Notification;
import p5.State;

/**
 * <p><b>Deterministic Finite Automaton</b></p>
 * Accepts iff accepting state and input consumed
 */
public class DFA implements Machine {

	private int stepState;
	private String stepInput;

	public DFA() {
	}

	@Override
	public status run(String input) { // todo lambda
		
		int s = Model.initialState;
		char symbol;

		while (!(input.isEmpty())) {
			symbol = input.charAt(0);
			boolean ok = false;
			for (LogicalTransition t : Model.transitionGraph.incidentEdges(s)) {
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
	}

	@Override
	public Integer stepForward() {
		Integer prevState = stepState;
		if (!stepInput.isEmpty()) {
			char symbol = stepInput.charAt(0);
			stepInput = stepInput.substring(1);

			if (transitionTable.row(prevState).containsKey(symbol)) {
				stepState = transitionTable.get(stepState, symbol);
			} else {
				if (transitionTable.row(prevState).containsKey(Consts.lambda)) {
					stepState = transitionTable.get(prevState, Consts.lambda);
					return stepState;
				} else {
					Step.setMachineOutcome(false);
				}
			}
			return stepState;

		} else {
			Step.setMachineOutcome(stepState.isAccepting());
			stepState = prevState;
			return prevState;
		}
	}

	@Override
	public void stepBackward(Integer s, String input) {
		stepState = s;
		stepInput = input;
	}
}