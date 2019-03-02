package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import machines.DFA;
import machines.DPA;
import machines.Mealy;
import machines.Moore;
import main.PFLAP;
import p5.Notification;

/**
 * Contains live machine, and data needed by machine to run
 * @author micycle1
 */
public final class Model {

	private static Machine m = new DFA(); // runs on model data
	public static MutableNetwork<Integer, LogicalTransition> transitionGraph; // logical transitions

	public static int initialState = -1; // -1 = none
	public static final HashSet<Integer> acceptingStates;

	private static int nextStateID = 0;

	static {
		transitionGraph = NetworkBuilder.directed().allowsParallelEdges(true).expectedNodeCount(50)
				.expectedEdgeCount(200).allowsSelfLoops(true).build();
		acceptingStates = new HashSet<>();
	}

	public static void reset(Machine m) {
		Model.m = m;
		transitionGraph = NetworkBuilder.directed().allowsParallelEdges(true).expectedNodeCount(50)
				.expectedEdgeCount(200).allowsSelfLoops(true).build();
		acceptingStates.clear();
		initialState = -1;
		nextStateID = 0;
	}

	public static void runMachine(String input) {
		switch (m.run(input)) {
			case SUCCESS :
				Notification.addNotification(main.Consts.notificationData.machineAccepted);
				break;
			case NOTACCEPTING :
				Notification.addNotification(main.Consts.notificationData.machineRejected);
				break;
			case FAIL :
				Notification.addNotification(main.Consts.notificationData.machineFailed);
				break;
			case COMPLETE :
				if (m instanceof Mealy) {
					Notification.addNotification("Machine Terminated",
							"The machine terminated with output: " + ((machines.Mealy) m).getOutput());
				}
				if (m instanceof Moore) {
					Notification.addNotification("Machine Terminated",
							"The machine terminated with output: " + ((machines.Moore) m).getOutput());
				}
				break;
			default :
				break;
		}
	}

	public static void beginStep(String input) {
		m.beginStep(input);
	}

	public static int stepForward() {
		return m.stepForward();
	}

	public static void stepBackward(Integer s) {
		m.stepBackward(s);
	}

	public static boolean assureUniqueTransition(LogicalTransition t) { // does this work for all?
		for (LogicalTransition a : transitionGraph.outEdges(t.getTail())) {
			if (a.getSymbol() == t.getSymbol() && a.getStackPop() == t.getStackPop()
					&& a.getStackPush().equals(t.getStackPush())) {
				return false;
			}
		}
		return true;
	}

	public static void addTransition(LogicalTransition t) {
		transitionGraph.addEdge(t.tail, t.head, t);
		PFLAP.PApplet.view.rebuild();
	}

	public static void addTransition(ArrayList<LogicalTransition> transitions) {
		for (LogicalTransition t : transitions) {
			transitionGraph.addEdge(t.tail, t.head, t);
		}
		PFLAP.PApplet.view.rebuild();
	}

	public static void deleteTransition(LogicalTransition t) {
		transitionGraph.removeEdge(t);
		PFLAP.PApplet.view.rebuild();
	}

	public static void deleteTransition(ArrayList<LogicalTransition> transitions) {
		for (LogicalTransition t : transitions) {
			transitionGraph.removeEdge(t); // delete all transitions it represented
		}
		PFLAP.PApplet.view.rebuild();
	}

	public static void addState(Integer n) {
		transitionGraph.addNode(n);
		PFLAP.PApplet.view.rebuild(n);
	}

	public static void deleteState(Integer n) {
		transitionGraph.removeNode(n);
		PFLAP.PApplet.view.deleteState(n);
	}
	
	public static String getOutput() {
		if (m instanceof Mealy) {
			return ((machines.Mealy) m).getOutput();
		}
		if (m instanceof Moore) {
			return ((machines.Moore) m).getOutput();
		}
		return "";
	}

	public static ArrayList<LogicalTransition> getConnectingTransitions(Integer s) {
		return new ArrayList<>(transitionGraph.incidentEdges(s));
	}
	
	public static void setInitialStackSymbol(Character c) {
		if (m instanceof DPA) {
			((DPA) m).setInitialStackSymbol(c);
		}
	}

	/**
	 * After load, set nextStateID to next free int.
	 */
	public static void setnextStateID(int n) {
		nextStateID = n;
	}

	public static void setInitialState(int n) {
		initialState = n;
	}

	public static int getNextIdAndInc() {
		nextStateID++;
		return nextStateID - 1;
	}

	public static int getLastID() {
		return nextStateID - 1;
	}

	public static Set<Integer> getStates() {
		return transitionGraph.nodes();
	}

	public static boolean isAccepting(Integer i) {
		return acceptingStates.contains(i);
	}

	public static int nTransitions() {
		return transitionGraph.edges().size();
	}

	public static int nStates() {
		return transitionGraph.nodes().size();
	}

}
