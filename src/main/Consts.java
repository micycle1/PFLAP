package main;

/**
 * Stores global constants and strings. All members are immutable.
 */
public final class Consts {

	public static final int stateRadius = 50, WIDTH = 800, HEIGHT = 800, SBNodeRadius = 5, stateFontSize = 18,
			initialNodeIndicatorSize = 12, transitionBezierCurve = 20, CTRL = 17, miniumWidth = 300,
			minimumHeight = 300, selfTransitionLength = 105;

	public static final int notificationWidth = 275, notificationHeight = 125, notificationTextPadding = 10,
			notificationLifetime = 240, notificationLifetimeFast = 150, notificationLifetimeVeryFast = 60;

	public static final int stepGUIPadding = 20;

	public static final String title = "PFLAP: Processing Formal Languages and Automata Package";

	public static final String directory = System.getProperty("user.dir");

	public static final char lambda = 'λ';

	public static enum notificationData {
		// @formatter:off
		noInitialState(new String[] {"No Initial State", "Elect an initial state before running."}),
		symbolInvalid(new String[] {"Symbol Entry Invalid", "Symbols must be one character in length."}),
		transitionInvalid(new String[] {"Non-Unique Transition", "Transitions from each state in a deterministic machine must be unique."}),
		machineAccepted(new String[] {"String Accepted", "The automaton accepted the input."}),
		machineRejected(new String[] {"String Rejected", "The automaton rejected the input."}),
		screenShot(new String[] {"String Rejected", "The automaton rejected the input."}),
		;
		// @formatter:on

		private String title;
		private String message;

		notificationData(String[] notification) {
			title = notification[0];
			message = notification[1];
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

			helpPFLAP = "In PFLAP, spaces (' ') are read as lambda (blank) transitions.\r\n" +
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
	
	public static final String helpStep = "Step backward and forward with the left and right arrow keys respectively.\r\n" +
	"Once the machine terminates, the terminating state will turn green to indicate acceptance or red to indicate the contrary.\r\n" +
	"A pink-colored state indicates the progress of the current step procedure; this is the state the machine is currently at.\r\n" +
	"\r\n" +
	"Note: Step mode will not automatically terminate upon the machine terminating, \r\n allowing you to step backwards. " +
	"Use the cross button to exit step mode.";
	// @formatter:on

	private Consts() {
		throw new AssertionError();
	}
}
