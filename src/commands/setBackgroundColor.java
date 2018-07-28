package commands;

//import main.appletVariables;

public class setBackgroundColor implements Command {

	private int oldColor = 255, color;
	
	public setBackgroundColor() {
		// TODO Auto-generated constructor stub
	}

	public setBackgroundColor(int color) {
		this.color = color;

	}

	@Override
	public void execute() {
//		oldColor = appletVariables.backgroundColor;
		// backgroundColorController.setbackgroundColor(this.color);
//		appletVariables.backgroundColor = this.color;
	}

	@Override
	public void undo() {
//		appletVariables.backgroundColor = oldColor;
	}

	@Override
	public String description() {
		return "Set background color: " + color;
	}

}
