package transitionView;

import static main.Functions.withinRange;
import static main.Functions.withinRegion;

import static processing.core.PApplet.constrain;
import static processing.core.PConstants.CENTER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import commands.addState;
import commands.addTransition;

import main.Consts;
import main.HistoryHandler;
import main.PFLAP;

import p5.AbstractArrow;
import p5.BezierArrow;
import p5.DirectArrow;
import p5.EntryArrow;
import p5.SelfArrow;
import p5.State;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Renders the view of the abstract transitions etc.
 * @author micycle1
 */
public final class View {

	private MutableNetwork<State, LogicalTransition> transitionGraph; // logical transitions
	private AbstractArrow cache; // most recently added transition

	private ArrayList<AbstractArrow> liveTransitions; // rendered transitions (type of transition) or hashmap of tail state to transition
	private HashMap<State, ArrayList<AbstractArrow>> liveTransitions1; // rendered transitions (type of transition) or hashmap of tail state to transition
	// private BiMap<LogicalTransition, AbstractArrow> mapping = HashBiMap.create();

	private ArrayList<State> states = new ArrayList<>();

	private PApplet p;

	private EntryArrow entryArrow;

	public View(PApplet parent) {
		p = parent;
		transitionGraph = NetworkBuilder.directed().allowsParallelEdges(true).expectedNodeCount(100)
				.expectedEdgeCount(200).allowsSelfLoops(true).build();
		liveTransitions = new ArrayList<>();
	}

	public void addTransition(LogicalTransition t) {
		transitionGraph.addEdge(t.tail, t.head, t);
		HistoryHandler.buffer(new addTransition(t)); // todo buffer here?
		rebuild();
	}

	public void deleteTransition(AbstractArrow a) {
		for (LogicalTransition t : a.transitions) {
			transitionGraph.removeEdge(t); // delete all transitions it represented
		}
		rebuild();
	}

	public void addState(State s) {
		transitionGraph.addNode(s);
		states.add(s);
	}

	public void entryArrow(State head, State tail) {
		entryArrow = new EntryArrow(head, tail);
	}

	/**
	 * Called when user clicks to create a new state
	 */
	public State newState(PVector mouseCoords) {
		State s = new State(mouseCoords, states.size());
		HistoryHandler.buffer(new addState(s));
		return s;
	}

	public void dragging(State s, PVector mouseCoords) {
		if (transitionGraph.nodes().contains(s)) {
			liveTransitions.forEach(t -> t.update());
		}
		s.setPosition(mouseCoords);
	}

	/**
	 * Multi-state drag
	 */
	public void dragging(PVector mouseClickXY, PVector mouseCoords) {
		PVector offset = new PVector(mouseCoords.x - mouseClickXY.x, mouseCoords.y - mouseClickXY.y);
		for (State s : states) {
			if (s.isSelected()) {
				s.setPosition(new PVector(constrain(offset.x + s.getSelectedPosition().x, 0, p.width),
						constrain(offset.y + s.getSelectedPosition().y, 0, p.height)));
			}
		}
		liveTransitions.forEach(t -> t.update());
	}

	@Deprecated
	public void undoAddTransition() {
		cache.kill();
		liveTransitions.remove(cache);
	}

	@Deprecated
	public void redo() {
		liveTransitions.add(cache);
	}

	public void draw() {
		p.textAlign(CENTER, CENTER);
		p.noFill();
		p.strokeWeight(2);
		p.stroke(PFLAP.transitionColour.getRGB()); // todo
		p.textSize(18);
		// p.textFont(comfortaaRegular); // todo
		liveTransitions.forEach(t -> t.draw());
		if (entryArrow != null) {
			if (entryArrow.dispose) {
				entryArrow = null;
			} else {
				entryArrow.draw();
			}
		}

		p.textSize(Consts.stateFontSize);
		// p.textFont(comfortaaBold);
		p.stroke(0);
		p.strokeWeight(3);
		p.textAlign(CENTER, CENTER);
		states.forEach(s -> s.draw());
	}

	public void selectAllStates() {
		for (State state : states) {
			state.select();
		}
	}

	public void deselectAllStates() {
		for (State state : states) {
			state.deselect();
		}
	}

	/**
	 * Invert user selection
	 */
	public void invertSelectedStates() {
		for (State state : states) {
			if (state.isSelected()) {
				state.deselect();
			} else {
				state.select();
			}
		}
	}

	public ArrayList<State> getSelectedStates() {
		ArrayList<State> selected = new ArrayList<>();
		for (State state : states) {
			if (state.isSelected()) {
				selected.add(state);
			}
		}
		return selected;
	}

	public void hideUI() {
		liveTransitions.forEach(a -> a.hideUI());
	}

	/**
	 * Rebuilds list of p5 arrows
	 * todo Rebuild only those that are affected?
	 */
	private void rebuild() {
		liveTransitions.clear();
		LinkedList<LogicalTransition> edges = new LinkedList<>(transitionGraph.edges());

		while (!edges.isEmpty()) {
			LogicalTransition edge = edges.poll();
			AbstractArrow a;
			if (edge.head.equals(edge.tail)) {
				a = new SelfArrow(edge.head);
			} else {
				if (transitionGraph.edgesConnecting(edge.head, edge.tail).size() == 0) {
					a = new DirectArrow(edge.head, edge.tail,
							new ArrayList<LogicalTransition>(transitionGraph.edgesConnecting(edge.tail, edge.head)));
				} else {
					a = new BezierArrow(edge.head, edge.tail,
							new ArrayList<LogicalTransition>(transitionGraph.edgesConnecting(edge.tail, edge.head)));
				}
			}
			edges.removeAll(transitionGraph.edgesConnecting(edge.tail, edge.head));
			liveTransitions.add(a);
			// mapping.put(t, a); // map from abstract transition to concrete p5 arrow
		}
	}

	public void reset() {
		liveTransitions.forEach(t -> t.disposeUI());
		liveTransitions.clear();
		states.forEach(n -> n.disposeUI());
		states.clear();
	}

	public AbstractArrow transitionMouseOver(PVector mouseClick) {
		for (AbstractArrow a : liveTransitions) {
			if (a.isMouseOver(mouseClick)) {
				return a;
			}
		}
		return null;
	}

	public State stateMouseOver(PVector mouseClick) {
		for (State s : states) {
			if (withinRange(s.getPosition().x, s.getPosition().y, s.getRadius(), mouseClick.x, mouseClick.y)
					|| s.isMouseOver()) {
				return s;
			}
		}
		return null;
	}

	public void statesInRegion(PVector startPos, PVector mouseCoords) {
		states.forEach(s -> s.deselect());
		for (State s : states) { // check for nodes in selection box
			if (withinRegion(s.getPosition(), startPos, mouseCoords)) {
				s.select();
			}
		}
	}

	public int nTransitions() {
		return transitionGraph.edges().size();
	}
	
	public int nStates() {
		return transitionGraph.nodes().size();
	}
}
