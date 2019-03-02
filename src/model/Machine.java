package model;

public interface Machine {
	
	enum status {
		/**
		 * Machine consumed input and final state is accepting
		 */
		SUCCESS
		/**
		 * Machine consumed input and final state is not accepting
		 */
		, NOTACCEPTING,
		/**
		 * Machine did not consume input fully (invalid transition)
		 */
		FAIL,
		/**
		 * Machine completed (for Mealy/Moore machines, where there is no accept/reject)
		 */
		COMPLETE
		}

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
	void stepBackward(Integer s);
}
