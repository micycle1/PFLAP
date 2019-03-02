package commands;

import javafx.scene.paint.Color;
import main.PFLAP;
import main.PFLAP.PApplet;

public final class setColorState implements Command {
	
	private final Color oldColor, newColor;
	
	public setColorState(Color c) {
		oldColor = PFLAP.stateColour;
		this.newColor = c;
	}

	@Override
	public void execute() {
		PFLAP.stateColour = newColor;
	}

	@Override
	public void undo() {
		PFLAP.stateColour = oldColor;
		PApplet.controller.colourPicker_state.setValue(oldColor);
	}

	@Override
	public String description() {
		return "Set state color: " + newColor.toString();
	}

}
