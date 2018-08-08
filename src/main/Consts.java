package main;

/**
 * Stores global constants and strings. All members are immutable.
 */
public final class Consts {

	public static final int stateRadius = 50, WIDTH = 800, HEIGHT = 800, SBNodeRadius = 5, stateFontSize = 18,
			initialNodeIndicatorSize = 12;

	public static final int notificationWidth = 275, notificationHeight = 125, notificationTextPadding = 10,
			notificationLifetime = 240, notificationLifetimeFast = 150, notificationLifetimeVeryFast = 60;

	public static final boolean arrowLabelTextRotate = true; // TODO

	public static final String title = "PFLAP: Processing Formal Languages and Automata Package";

	public static enum notificationData {
		// @formatter:off
		noInitialState(new String[] {"No Initial State", "Elect an initial state before running."}),
		symbolNotValid(new String[] {"Symbol Entry Invalid", "Symbols must be one character in length."}),
		machineAccepted(new String[] {"String Accepted", "The automaton accepted the input."}),
		machineRejected(new String[] {"String Rejected", "The automaton rejected the input."}),
		;
		// @formatter:on

		private String title;
		private String message;

		notificationData(String[] info) {
			title = info[0];
			message = info[1];
		}

		public String title() {
			return title;
		}

		public String message() {
			return message;
		}
	}

	// @formatter:off
	public static final String about = "PFLAP: Processing Formal Languages and Automata Package.\r\n" + 
			"A JFLAP alternative using the Processing library as the graphics backend.",

			help = "In PFLAP, spaces (' ') are read as lambda (blank) transitions.\r\n" +
					"Mouse:\r\n" + 
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

	private Consts() {
		throw new AssertionError();
	}
}
