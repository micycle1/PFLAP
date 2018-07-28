package commands;

import java.util.ArrayList;
import java.util.HashSet;

import main.PFLAP;
import p5.State;

public class deleteState implements Command {

	private ArrayList<State> deleteCache;

	public deleteState(ArrayList<State> s) {
		deleteCache = new ArrayList<>(s);
	}

	public deleteState(HashSet<State> s) {
		deleteCache = new ArrayList<>(s);
	}

	public deleteState(State s) {
		deleteCache = new ArrayList<>();
		deleteCache.add(s);
	}

	@Override
	public void execute() {
		deleteCache.forEach(s -> s.kill());
		PFLAP.nodes.removeAll(deleteCache);
	}

	@Override
	public void undo() {
		// re add arrows?
		PFLAP.nodes.addAll(deleteCache);
	}

	@Override
	public String description() {
		if (deleteCache.size() > 1) {
			return "Deleted " + deleteCache.size() +" states.";
		} else {
			return "Deleted state " + deleteCache.get(0).getLabel();
		}
	}

}
