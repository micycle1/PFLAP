package DFA;

import java.util.HashMap;

import p5.State;

public class Machine {

	public static HashMap<Integer, Node> nodes = new HashMap<>();
	private static String input;

	public static void run(String sequence, int startID) {
		if (nodes.containsKey(startID)) {
			input = sequence;
			nodes.get(startID).update(sequence);
		}
	}

	public static void addNode(State s) {
		System.out.println(s.nodeID);
		Node n = new Node(s.nodeID, s.isAccepting());
		nodes.put(s.nodeID, n);
	}
	
	public static void deleteNode(State s) {
		for (Node n : nodes.values()) {
			if (n.transitions.containsValue(s.nodeID)) { //TODO
				n.transitions.remove(s.nodeID);
			}
		}
		nodes.remove(s.nodeID);
	}

	public static int totalStates() {
		return nodes.size();
	}

	public static int totalTransitions() {
		int i = 0;
		for (Node n : nodes.values()) {
			i += n.transitions.size();
		}
		return i;
	}

	public static void terminate(Node n, boolean accepted, String reason) {
		System.out.println(input + " : " + accepted + " at State " + n.hash + ". " + reason);
	}

}