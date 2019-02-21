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
import main.Functions;
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
import processing.core.PVector;

/**
 * Renders the view of the abstract transitions etc.
 * handle initial & accepting state here , not in state class
 * @author micycle1
 */
public final class View {

	private final ArrayList<AbstractArrow> liveTransitions; // rendered transitions (type of transition) or hashmap of tail state to transition
	private final BiMap<Integer, State> liveStates; // record of cp5 state objects
	private final BiMap<Integer, State> disposedStates; // non-live (after undo, etc)
	
	private PApplet p;

	private EntryArrow entryArrow;

	public View(PApplet parent) {
		p = parent;
		liveTransitions = new ArrayList<>();
		liveStates = HashBiMap.create();
		disposedStates = HashBiMap.create();
	}
	
	public void moveState(State s, PVector pos) {
		s.setPosition(pos);
		liveTransitions.forEach(t -> t.update());
	}
	
	public void entryArrow(State head, State tail) {
		entryArrow = new EntryArrow(head, tail);
	}

	/**
	 * Called when user clicks to create a new state.
	 * Here the view calls the model, which then updates the view.
	 */
	public State newState(PVector mouseCoords) {
		State s = new State(mouseCoords, liveStates.size());
		HistoryHandler.buffer(new addState(Model.getNextIdAndInc()));
		liveStates.put(Model.getLastID(), s);
		return s;
	}
	
	/**
	 * Called by model only
	 */
	public void deleteState(int n) {
		disposedStates.put(n, liveStates.get(n));
		disposedStates.get(n).deselect();
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
		p.strokeWeight(2);
		p.stroke(Functions.colorToRGB(PFLAP.transitionColour));
		p.textSize(18);
		p.noFill();
		liveTransitions.forEach(t -> t.draw());
		if (entryArrow != null) {
			if (entryArrow.dispose) {
				entryArrow = null;
			} else {
				entryArrow.draw();
			}
		}

		p.textSize(Consts.stateFontSize);
		p.stroke(0);
		p.strokeWeight(3);
		p.textAlign(CENTER, CENTER);
		liveStates.values().forEach(s -> s.draw());
	}
	
	public void highlightState(Integer state, Integer color) {
		liveStates.get(state).highLight(color);
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
	
	public HashSet<Integer> getSelectedStates() {
		HashSet<Integer> selected = new HashSet<>();		
		for (Integer n : liveStates.keySet()) {
			if (liveStates.get(n).isSelected()) {
				selected.add(n);
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
	
	public Integer getIDByState(State s) {
		return liveStates.inverse().get(s);
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
				a = new SelfArrow(head, new ArrayList<LogicalTransition>(Model.transitionGraph.edgesConnecting(edge.tail, edge.head)));
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
		}
	}
	
	public void rebuild(Integer n) {
		if (disposedStates.keySet().contains(n)) {
			liveStates.put(n, disposedStates.get(n));
			disposedStates.remove(n);
		}
		rebuild();
	}
	
	public void reset() {
		liveTransitions.forEach(t -> t.disposeUI());
		liveTransitions.clear();
		liveStates.values().forEach(n -> n.disposeUI());
		liveStates.clear();
		disposedStates.values().forEach(n -> n.disposeUI());
		disposedStates.clear();
	}
	
	/**
	 * Export live GUI states to HistoryHandler
	 */
	public BiMap<Integer, State> save() {
		return liveStates;
	}
	
	@SuppressWarnings("unchecked")
	public void load(Object liveStates) {
		this.liveStates.putAll((BiMap<Integer, State>) liveStates);
		this.liveStates.values().forEach(s -> s.initCP5());
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