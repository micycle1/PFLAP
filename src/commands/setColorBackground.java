package commands;

import javafx.scene.paint.Color;
import main.PFLAP;
import main.PFLAP.PApplet;

public final class setColorBackground implements Command {

	private final Color oldColor, newColor;
	
	public setColorBackground(Color c) {
		oldColor = PFLAP.bgColour;
		this.newColor = c;
	}

	@Override
	public void execute() {
		PFLAP.bgColour = newColor;
	}

	@Override
	public void undo() {
		PFLAP.bgColour = oldColor;
		PApplet.controller.colourPicker_background.setValue(oldColor);
	}

	@Override
	public String description() {
		return "Set background color: " + newColor.toString();
	}

}
