package commands;

import javafx.scene.paint.Color;
import main.PFLAP;
import main.PFLAP.PApplet;

public final class setColorStateSelected implements Command {

	private final Color oldColor, newColor;
	
	public setColorStateSelected(Color c) {
		oldColor = PFLAP.stateSelectedColour;
		this.newColor = c;
	}

	@Override
	public void execute() {
		PFLAP.stateSelectedColour = newColor;
	}

	@Override
	public void undo() {
		PFLAP.stateSelectedColour = oldColor;
		PApplet.controller.colourPicker_stateSelected.setValue(oldColor);
	}

	@Override
	public String description() {
		return "Set state-selected color: " + newColor.toString();
	}

}
