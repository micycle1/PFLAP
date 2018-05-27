package main;

public final class Consts {

	public static final int stateRadius = 50, WIDTH = 800, HEIGHT = 800, SBNodeRadius = 5, stateFontSize = 16;

	public static final boolean arrowLabelTextRotate = true; // TODO

	// @formatter:off
	public static final String about = "PFLAP: Processing Formal Languages and Automata Package.\r\n" + 
			"A JFLAP alternative using the Processing library as the graphics backend.",

			help = "Mouse:\r\n" + 
					"\r\n" + 
					"Left-Click (press): Add New State / Select State.\r\n" + 
					"Left-Click (hold & drag): Move State.\r\n" + 
					"Right-Click (press): Open State options menu.\r\n" + 
					"Right-Click (start on state & drag): Add new transition.\r\n" + 
					"Right-Click (start on empty region & drag): Selection Box.\r\n" +
					"Middle-Mouse (Drag): Move all selected states.\r\n" +
					"\r\n" + 
					"Keyboard:\r\n" + 
					"\r\n" + 
					"DEL: Delete Selected States.\r\n" + 
					"ESC: Exit Program.\r\n";
	// @formatter:on

}
