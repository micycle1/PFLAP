package transitionView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import static main.PFLAP.p;
import static processing.core.PConstants.CENTER;
import commands.deleteTransition;
import p5.AbstractArrow;
import p5.BezierArrow;
import p5.DirectArrow;
import p5.SelfArrow;
import p5.State;
import processing.core.PVector;

/**
 * Create arrow objects after transition info entry.
 * They are rendered in this class
 * @author micycle1
 *
 */
public class View {

	private static MutableNetwork<State, LogicalTransition> transitionGraph; // logical transitions
	private static AbstractArrow cache; // most recently added transition

	static ArrayList<AbstractArrow> liveTransitions; // rendered transitions (type of transition) or hashmap of tail state to transition
	static HashMap<State, ArrayList<AbstractArrow>> liveTransitions1; // rendered transitions (type of transition) or hashmap of tail state to transition
	static BiMap<LogicalTransition, AbstractArrow> mapping = HashBiMap.create();

	static {
		transitionGraph = NetworkBuilder.directed().allowsParallelEdges(true).expectedNodeCount(100)
				.expectedEdgeCount(200).allowsSelfLoops(true).build();
		liveTransitions = new ArrayList<>();
	}

	public static void addTransition(LogicalTransition t) {
		transitionGraph.addEdge(t.tail, t.head, t);
		rebuild();
	}

	public static void deleteTransition(AbstractArrow a) {
		transitionGraph.removeEdge(mapping.inverse().get(a));
		rebuild();
	}

	public static void dragging(State s) {
		if (transitionGraph.nodes().contains(s)) {
		for (LogicalTransition t : transitionGraph.incidentEdges(s)) {
			mapping.get(t).update();
		}
		}
	}

	public static void dragging(HashSet<State> states) {
		for (State s : states) {
			for (LogicalTransition t : transitionGraph.incidentEdges(s)) {
//				mapping.get(t).update();
			}
		}
	}

	public static void undoAddTransition() {
		cache.kill();
		liveTransitions.remove(cache);
	}

	public static void redo() {
		liveTransitions.add(cache);
	}

	public static void draw() {
		p.textAlign(CENTER, CENTER);
		p.noFill();
		p.strokeWeight(2);
//		p.stroke(transitionColour.getRGB()); //todo
		p.textSize(18);
//		p.textFont(comfortaaRegular);
		liveTransitions.forEach(t -> t.draw());
	}

	public static void hideUI() {
		liveTransitions.forEach(a -> a.hideUI());
	}

	/**
	 * Rebuild 
	 */
	private static void rebuild() {
		liveTransitions.clear();
		for (LogicalTransition t : transitionGraph.edges()) {
			AbstractArrow a;
			if (t.head.equals(t.tail)) {
				a = new SelfArrow(t.head);
			} else {
				if (transitionGraph.edgesConnecting(t.tail, t.head).size() == 1) {
					a = new DirectArrow(t.head, t.tail);
				} else {
					a = new BezierArrow(t.head, t.tail, t.transitionSymbol, t.stackPop, t.stackPush);
				}
			}
			liveTransitions.add(a);
			mapping.put(t, a);
		}
	}

	public static void reset() {
		liveTransitions.forEach(t -> t.disposeUI());
		liveTransitions.clear();
		mapping.clear();
	}

	public static AbstractArrow transitionMouseOver(PVector mouseClick) {
		for (AbstractArrow a : liveTransitions) {
			if (a.isMouseOver(mouseClick)) {
				return a;
			}
		}
		return null;
	}

}
