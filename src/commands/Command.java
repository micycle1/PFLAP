package commands;

public interface Command {
	public abstract void execute();
	public abstract void undo();
	public abstract String description();
//	public default void notifyHandler() {
//		HistoryHandler.buffer(this);
//	}
}
