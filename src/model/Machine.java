package model;

public interface Machine {
	
	enum status {SUCCESS, NOTACCEPTING, FAIL} // NOTACCEPTING = consumed, but didn't finish on final state

	/**
	 * Run machine on input
	 * @param input
	 * @return Status of machine acceptance on input.
	 */
	status run(String input);
	
	void beginStep(String input);

	/**
	 * @return The next state
	 */
	Integer stepForward();
	
	/**
	 * {@link main.Step Step} calls this to revert the machine's State.
	 * @param s Previous State
	 * @param input Previous input (one character longer)
	 */
	void stepBackward(Integer s, String input);
}
