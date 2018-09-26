package commands;

import java.io.Serializable;

public interface Command extends Serializable {
	public abstract void execute();
	public abstract void undo();
	public abstract String description();
}
