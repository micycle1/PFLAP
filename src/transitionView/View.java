package transitionView;

import static main.Functions.withinRange;
import static main.Functions.withinRegion;

import static processing.core.PApplet.constrain;
import static processing.core.PConstants.CENTER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import commands.addState;

import main.Consts;
import main.HistoryHandler;
import main.PFLAP;

import model.LogicalTransition;
import model.Model;

import p5.AbstractArrow;
import p5.BezierArrow;
import p5.DirectArrow;
import p5.EntryArrow;
import p5.SelfArrow;
import p5.State;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Renders the view of the abstract transitions etc.
 * handle initial & accepting state here , not in state class
 * @author micycle1
 */
public final class View {

//	private MutableNetwork<State, LogicalTransition> transitionGraph; // logical transitions

	private final ArrayList<AbstractArrow> liveTransitions; // rendered transitions (type of transition) or hashmap of tail state to transition
//	private HashMap<State, ArrayList<AbstractArrow>> liveTransitions1; // rendered transitions (type of transition) or hashmap of tail state to transition
	public final BiMap<Integer, State> liveStates;

//	private final  ArrayList<State> states = new ArrayList<>(); // todo use transitionGraph.nodes? 
	
//	private HashMap<Integer, State> liveStates;
	
	// map from int to states

	private PApplet p;

	private EntryArrow entryArrow;

	public View(PApplet parent) {
		p = parent;
		liveTransitions = new ArrayList<>();
		liveStates = HashBiMap.create();
	}
	
	public void moveState(State s, PVector pos) {
		s.setPosition(pos);
		liveTransitions.forEach(t -> t.update());
	}
	
	public void entryArrow(State head, State tail) {
		entryArrow = new EntryArrow(head, tail);
	}

	/**
	 * Called when user clicks to create a new state
	 */
	public State newState(PVector mouseCoords) {
		State s = new State(mouseCoords, liveStates.size());
		HistoryHandler.buffer(new addState(Model.getNextIdAndInc()));
		liveStates.put(Model.getLastID(), s);
		return s;
	}
	
	public void deleteState(int n) {
		p.println(n);
		liveStates.get(n).disposeUI();
		liveStates.remove(n);
		rebuild();
	}

	public void dragging(State s, PVector mouseCoords) {
		if (liveStates.values().contains(s)) {
			liveTransitions.forEach(t -> t.update());
		}
		s.setPosition(mouseCoords);
	}

	/**
	 * Multi-state drag
	 */
	public void dragging(PVector mouseClickXY, PVector mouseCoords) {
		PVector offset = new PVector(mouseCoords.x - mouseClickXY.x, mouseCoords.y - mouseClickXY.y);
		for (State s : liveStates.values()) {
			if (s.isSelected()) {
				s.setPosition(new PVector(constrain(offset.x + s.getSelectedPosition().x, 0, p.width),
						constrain(offset.y + s.getSelectedPosition().y, 0, p.height)));
			}
		}
		liveTransitions.forEach(t -> t.update());
	}

	public void draw() {
		p.textAlign(CENTER, CENTER);
		p.noFill();
		p.strokeWeight(2);
		p.stroke(PFLAP.transitionColour.getRGB()); // todo
		p.textSize(18);
		// p.textFont(comfortaaRegular); // todo
		p.textAlign(PConstants.CENTER, PConstants.BOTTOM);
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
//		states.forEach(s -> s.draw());
		liveStates.values().forEach(s -> s.draw());
	}

	public void selectAllStates() {
		for (State state : liveStates.values()) {
			state.select();
		}
	}

	public void deselectAllStates() {
		for (State state : liveStates.values()) {
			state.deselect();
		}
	}

	/**
	 * Invert user selection
	 */
	public void invertSelectedStates() {
		for (State state : liveStates.values()) {
			if (state.isSelected()) {
				state.deselect();
			} else {
				state.select();
			}
		}
	}

	public HashSet<State> getSelectedStates() {
		HashSet<State> selected = new HashSet<>();
		for (State state : liveStates.values()) {
			if (state.isSelected()) {
				selected.add(state);
			}
		}
		return selected;
	}
	
	/**
	 * Return live state GUI object corresponding to abstract ID
	 * @return
	 */
	public State getStateByID(int n) {
		return liveStates.get(n);
	}

	public void hideUI() {
		liveTransitions.forEach(a -> a.hideUI());
	}

	/**
	 * Rebuilds list of p5 arrows
	 * todo Rebuild only those that are affected?
	 */
	public void rebuild() {
		
		liveTransitions.forEach(t -> t.disposeUI());
		liveTransitions.clear();
		LinkedList<LogicalTransition> edges = new LinkedList<>(Model.transitionGraph.edges());

		while (!edges.isEmpty()) {
			LogicalTransition edge = edges.poll();
			AbstractArrow a;
			State head = liveStates.get(edge.head); // need to save livestates
			State tail = liveStates.get(edge.tail);
			
			if (head.equals(tail)) {
				a = new SelfArrow(head);
			} else {
				if (Model.transitionGraph.edgesConnecting(edge.head, edge.tail).size() == 0) {
					a = new DirectArrow(head, tail,
							new ArrayList<LogicalTransition>(Model.transitionGraph.edgesConnecting(edge.tail, edge.head)));
				} else {
					a = new BezierArrow(head, tail,
							new ArrayList<LogicalTransition>(Model.transitionGraph.edgesConnecting(edge.tail, edge.head)));
				}
			}
			edges.removeAll(Model.transitionGraph.edgesConnecting(edge.tail, edge.head));
			liveTransitions.add(a);
			// mapping.put(t, a); // map from abstract transition to concrete p5 arrow
		}
	}
	
	public void reskin() {
		// call when states are changed looks
	}

	public void reset() {
		liveTransitions.forEach(t -> t.disposeUI());
		liveTransitions.clear();
		liveStates.values().forEach(n -> n.disposeUI());
		liveStates.clear();
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
		for (State s : liveStates.values()) {
			if (withinRange(s.getPosition().x, s.getPosition().y, s.getRadius(), mouseClick.x, mouseClick.y)
					|| s.isMouseOver()) {
				return s;
			}
		}
		return null;
	}

	public void statesInRegion(PVector startPos, PVector mouseCoords) {
		liveStates.values().forEach(s -> s.deselect());
		for (State s : liveStates.values()) { // check for nodes in selection box
			if (withinRegion(s.getPosition(), startPos, mouseCoords)) {
				s.select();
			}
		}
	}
}