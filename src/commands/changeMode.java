package commands;

import main.PFLAP;
import main.PFLAP.PApplet;
import p5.Notification;

public class changeMode implements Command {
	
	private PFLAP.modes mode, oldMode;
	
	public changeMode(PFLAP.modes mode) {
		this.mode = mode;
		oldMode = PFLAP.mode;
	}

	@Override
	public void execute() {
		PFLAP.mode = mode;
		PApplet.reset();
		Notification.addNotification("Mode Changed", mode + " mode selected.");
	}

	@Override
	public void undo() {
		PFLAP.mode = oldMode;
		PApplet.reset();
		Notification.addNotification("Mode Changed", mode + " mode selected.");
	}

	@Override
	public String description() {
		return "New PFLAP Mode: " + mode.toString();
	}

}
