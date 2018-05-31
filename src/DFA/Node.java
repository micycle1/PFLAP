package DFA;

import java.util.HashMap;

public class Node {
	
	public int hash;
	private boolean accepting;
	
	public HashMap<Character, Node> transitions = new HashMap<>();
	
	protected Node(int ID, boolean accepting) {
		this.hash = ID;
		this.accepting = accepting;
	}
	
	public void addTransition(Node n, char symbol) {
		transitions.put(symbol, n);
	}
	
	public void update(String input) {
		if (input.length() == 0) {
			if (accepting) {
				Machine.terminate(this, true, "Input fully accepted.");
			}
			else {
				Machine.terminate(this, false, "Input fully consumed but did not terminate on accepting state.");
			}
			
		}
		else {
			if (transitions.containsKey(input.charAt(0))) {
		transitions.get(input.charAt(0)).update(input.substring(1));
			}
			else {
				Machine.terminate(this, false, "Input rejected (no transition).");
			}
		}
	}
	
	public void toggleAccepting() {
		this.accepting = !this.accepting;
	}
	
	public void deleteTransitions() {
		
	}
	
	public void recursiveDeleteTransitions() {
		for (Node n : transitions.values()) {
			n.deleteTransitions();
		}
	}

}