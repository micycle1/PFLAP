package machines;

import static main.Consts.lambda;

import java.util.LinkedList;
import java.util.Queue;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Toggle;

import main.Functions;
import main.PFLAP;
import main.Step;
import model.LogicalTransition;
import model.Machine;
import model.Model;

/**
 * <p><b> Deterministic Pushdown Automaton</b>
 * <p> Accepts on accept state or stack empty.
 */
public class DPA implements Machine {

	private Queue<Character> stack, previousStack;
	private Character initialStackSymbol;
	private String stepInput;
	private int stepState, stepIndex; // index to slice stepinput

	private static final Toggle toggleAcceptByStackEmpty, toggleAcceptByAcceptState;

	static {
		// @formatter:off
		toggleAcceptByStackEmpty = 
				PFLAP.cp5.addToggle("Accept by\nEmpty Stack")
				.setValue(true)
				.setColorLabel(0)
				.setPosition(5, 5)
				.setWidth(80)
				.setColorActive(Functions.colorToRGB(0, 200, 0))
				.setColorBackground(Functions.colorToRGB(200, 0, 0))
				.setColorForeground(Functions.colorToRGB(0, 50, 0))
				.addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent event) {
				        if (event.getAction() == 100 && !toggleAcceptByAcceptState.getBooleanValue() 
				        		&& !toggleAcceptByStackEmpty.getBooleanValue()) {
				        	toggleAcceptByAcceptState.toggle();
				        }}})
				;
		toggleAcceptByAcceptState = 
				PFLAP.cp5.addToggle("Accept by\nAccepting State")
				.setValue(false)
				.setColorLabel(0)
				.setPosition(5, 55)
				.setWidth(80)
				.setColorActive(Functions.colorToRGB(0, 200, 0))
				.setColorBackground(Functions.colorToRGB(200, 0, 0))
				.setColorForeground(Functions.colorToRGB(0, 50, 0))
				.addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent event) {
				        if (event.getAction() == 100 && !toggleAcceptByAcceptState.getBooleanValue() 
				        		&& !toggleAcceptByStackEmpty.getBooleanValue()) {
				        	toggleAcceptByStackEmpty.toggle();
				        }}})
				;
		// @formatter:on
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
	}

	public void setInitialStackSymbol(Character c) {
		initialStackSymbol = c;
	}

	private void setStepStack() {
		String stackString = "";
		for (char c : stack) {
			stackString += c;
		}
		Step.setStack(stackString);
	}

	@Override
	public void beginStep(String input) {
		stack.clear();
		stack.add(initialStackSymbol);
		stepState = Model.initialState;
		stepInput = input;
		stepIndex = 0;
		previousStack = new LinkedList<>();
		setStepStack();
	}
	
	@Override
	public Integer stepForward() {
		Integer prevState = stepState;

		if (stepIndex < stepInput.length()) {
			previousStack = new LinkedList<>(stack);
			char symbol = stepInput.charAt(stepIndex);

			boolean ok = false;
			for (LogicalTransition t : Model.transitionGraph.outEdges(prevState)) {
				if (t.getSymbol() == symbol && (t.getStackPop() == stack.peek() || t.getStackPop() == lambda)) {
					stack.poll();
					stepState = t.head;
					t.getStackPush().chars().forEach(c -> {
						if (c != lambda) {
							stack.add((char) c);
						}
					});
					ok = true;
					break;
				}
			}
			setStepStack();
			if (!ok) {
				Step.setMachineOutcome(false);
			} else {
				stepIndex++;
			}
			return stepState;

		} else {
			Step.setMachineOutcome((Model.isAccepting(stepState) && toggleAcceptByAcceptState.getBooleanValue())
					|| (stack.isEmpty() && toggleAcceptByStackEmpty.getBooleanValue()));
			stepState = prevState;
			return prevState;
		}
	}

	@Override
	public void stepBackward(Integer s) {
		stepState = s;
		stepIndex--;
		stack = new LinkedList<>(previousStack);
		setStepStack();
	}

	@Override
	public status run(String input) {

		int s = Model.initialState;
		char symbol;

		while (!(input.isEmpty())) {
			symbol = input.charAt(0);
			boolean ok = false;
			for (LogicalTransition t : Model.transitionGraph.outEdges(s)) {
				if (t.getSymbol() == symbol && (t.getStackPop() == stack.peek() || t.getStackPop() == lambda)) {
					stack.poll();
					t.getStackPush().chars().forEach(c -> {
						if (c != lambda) {
							stack.add((char) c);
						}
					});
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
		if (toggleAcceptByAcceptState.getBooleanValue() && (Model.acceptingStates.contains(s) || stack.isEmpty())) {
			return status.SUCCESS;
		} else {
			return status.NOTACCEPTING;
		}
	}
}