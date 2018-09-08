package commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import p5.Arrow;
import p5.State;

/**
 * Container object for multiple Commands, such as when multiple states are
 * deleted/moved at once.
 * <p>
 * Allows a single undo/redo action to undo/redo many different Commands.
 */
public final class Batch implements Command {

	private final ArrayList<Command> commandBatch;

	public Batch(ArrayList<Command> batch) {
		commandBatch = batch;
	}

	public Batch(Command... batch) {
		commandBatch = new ArrayList<>();
		commandBatch.addAll(Arrays.asList(batch));
	}

	@Override
	public void execute() {
		commandBatch.forEach(c -> c.execute());
	}

	@Override
	public void undo() {
		commandBatch.forEach(c -> c.undo());
	}

	@Override
	public String description() {
		return "Command batch of size " + commandBatch.size();
	}

	/**
	 * Include description of child Commands.
	 */
	public String extendedDescription() {
		StringBuilder sb = new StringBuilder();
		commandBatch.forEach(c -> sb.append(c.description() + "\n"));
		return "Command batch of size " + commandBatch.size() + "{ " + sb.toString() + " }";
	}

	/**
	 * Creates a an ArrayList of Commands; suitable for input into a Batch instance.
	 * @param states HashSet of {@link State States} to be deleted.
	 * @return ArrayList of {@link deleteState} Commands.
	 */
	public static ArrayList<Command> createDeleteBatch(HashSet<State> states) {
		ArrayList<Command> tempCommands = new ArrayList<Command>();
		for (State s : states) {
			tempCommands.add(new deleteState(s));
		}
		return tempCommands;
	}

	/**
	 * Creates a an ArrayList of Commands; suitable for input into a Batch instance.
	 * @param states
	 * @return ArrayList of {@link deleteState} Commands.
	 */
	public static ArrayList<Command> createDeleteBatch(ArrayList<State> states) {
		ArrayList<Command> tempCommands = new ArrayList<Command>();
		for (State s : states) {
			tempCommands.add(new deleteState(s));
		}
		return tempCommands;
	}

	/**
	 * Creates a an ArrayList of Commands; suitable for input into a Batch instance.
	 * @param states HashSet of {@link State States} to be moved.
	 * @return ArrayList of {@link moveState} Commands.
	 */
	public static ArrayList<Command> createMoveBatch(HashSet<State> states) {
		ArrayList<Command> tempCommands = new ArrayList<Command>();
		for (State s : states) {
			tempCommands.add(new moveState(s, s.getPosition()));
		}
		return tempCommands;
	}
	
	/**
	 * Creates a an ArrayList of Commands; suitable for input into a Batch instance.
	 * @param arrows Arraylist of {@link Arrow Arrows} to be deleted.
	 * @return ArrayList of {@link deleteTransition} Commands.
	 */
	public static ArrayList<Command> createDeleteTransitionBatch(ArrayList<Arrow> arrows) {
		ArrayList<Command> tempCommands = new ArrayList<Command>();
		for (Arrow a : arrows) {
			tempCommands.add(new deleteTransition(a));
		}
		return tempCommands;
	}
}
