package commands;

import javafx.scene.paint.Color;
import main.PFLAP;
import main.PFLAP.PApplet;

public final class setColorTransition implements Command {
	
	private final Color oldColor, newColor;
	
	public setColorTransition(Color c) {
		oldColor = PFLAP.transitionColour;
		this.newColor = c;
	}

	@Override
	public void execute() {
		PFLAP.transitionColour = newColor;
	}

	@Override
	public void undo() {
		PFLAP.transitionColour = oldColor;
		PApplet.controller.colourPicker_transition.setValue(oldColor);
	}

	@Override
	public String description() {
		return "Set transition color: " + newColor.toString();
	}

}
