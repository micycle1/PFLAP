package commands;

import static main.PFLAP.machine;

import main.PFLAP;
import p5.State;

public final class deleteState implements Command {

	private final State s;
	private final Batch affectedTransitions;

	public deleteState(State s) {
		this.s = s;
		affectedTransitions = new Batch(Batch.createDeleteTransitionBatch(s.getConnectedArrows()));
	}

	@Override
	public void execute() {
		affectedTransitions.execute();
		s.kill();
		s.deselect();
		PFLAP.nodes.remove(s);
	}

	@Override
	public void undo() {
		machine.addNode(s);
		PFLAP.nodes.add(s);
		affectedTransitions.undo();
	}

	@Override
	public String description() {
		return "Deleted the state " + s.getLabel();
	}

}
