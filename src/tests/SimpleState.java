package tests;

final class SimpleState {
	
	private int ID;
	
	private boolean accepting = false;
	
	protected SimpleState(int ID) {
		this.ID = ID;
	}
	
	protected void setAccepting(boolean accepting) {
		this.accepting = accepting;
	}
	
	protected boolean isAccepting() {
		return accepting;
	}

}
