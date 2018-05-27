package main;

import java.util.ArrayList;
import java.util.HashSet;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import controlP5.ControlP5;
import controlP5.Textfield;
import p5.Arrow;
import p5.SelectionBox;
import p5.State;

public class PFLAP extends PApplet {

	public static HashSet<Character> keysDown = new HashSet<Character>();
	public static HashSet<Integer> mouseDown = new HashSet<Integer>();

	public static ArrayList<Arrow> arrows = new ArrayList<>();
	public static ArrayList<State> nodes = new ArrayList<>();
	public static HashSet<State> selected = new HashSet<>();

	public static ControlP5 cp5;
	private static Textfield consumable; // TODO User text entry

	public static State initialState = null;
	private static State mouseOverState, arrowTailState, arrowHeadState, dragState;
	private static Arrow drawingArrow;
	private static SelectionBox selectionBox = null;

	private PVector mouseClickXY, mouseReleasedXY, mouseCoords;

	public static boolean allowNewArrow = true;

	public static void main(String[] args) {
		PApplet.main(PFLAP.class);
	}

	@Override
	public void setup() {
		State.p = this; // Static PApplet for State objects
		Arrow.p = this; // Static PApplet for Arrow objects
		surface.setTitle("JFLAP ~ micycle");
		surface.setLocation(displayWidth / 2 - width / 2, displayHeight / 2 - height / 2);
		surface.setResizable(false);
		frameRate(60);
		strokeJoin(MITER);
		strokeWeight(3);
		stroke(0);
		textSize(16);
		textAlign(CENTER, CENTER);
		rectMode(CORNER);
		ellipseMode(CENTER);
		cursor(ARROW);
		initCp5();
	}

	@Override
	public void settings() {
		size(Consts.WIDTH, Consts.HEIGHT);
		smooth(4);
	}

	@Override
	public void draw() {
		mouseCoords = new PVector(constrain(mouseX, 0, width), constrain(mouseY, 0, height));
		// textAlign(LEFT, TOP);
		background(255);
		fill(0);

		if (!(drawingArrow == null)) {
			stroke(0, 0, 0, 80);
			strokeWeight(2);
			drawingArrow.setHeadXY(mouseCoords);
			drawingArrow.draw();
		}

		if (selectionBox != null) {
			selectionBox.setEndPosition(mouseCoords);
			selectionBox.draw();
		}

		stroke(0);
		fill(0);

		strokeWeight(2);
		for (Arrow a : arrows) {
			a.draw();
		}

		for (State s : nodes) {
			s.draw();
		}
		if (dragState != null) {
			dragState.setPosition(mouseCoords);
			dragState.draw();
		}
	}

	private void initCp5() {
		cp5 = new ControlP5(this);
	}

	private static boolean withinRange(float x, float y, float diameter, float x2, float y2) {
		return (sqrt(sq(y - y2) + sq(x - x2)) < diameter / 2);
	}

	public static float angleBetween(PVector tail, PVector head) {
		return -atan2(tail.x - head.x, tail.y - head.y) - (PI / 2);
	}

	public static boolean numberBetween(float n, float a1, float a2) {
		return (n >= min(a1, a2) && n <= max(a1, a2));
	}

	private static boolean withinSelection(State s) {
		PVector sXY = s.getPosition();
		PVector bSP = selectionBox.startPosition;
		PVector bEP = selectionBox.endPosition;
		return sXY.x >= bSP.x && sXY.y >= bSP.y && sXY.x <= bEP.x && sXY.y <= bEP.y;
	}

	public void nodeMouseOver() {
		for (State s : nodes) {
			if (withinRange(s.getPosition().x, s.getPosition().y, Consts.stateRadius, mouseX, mouseY)
					|| s.cp5.isMouseOver()) {
				mouseOverState = s;
				return;
			}
		}
		mouseOverState = null;
	}

	public void deleteState(State s) {
		// call state functions to delete arrows
		s.kill();
		nodes.remove(s);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keysDown.add(e.getKey());
	}

	@Override
	public void keyReleased(KeyEvent key) {
		switch (key.getKeyCode()) {
			case 127 : // delete key
				for (State s : selected) {
					deleteState(s);
				}
				selected.clear();
				break;
			default :
				break;
		}
		keysDown.remove(key.getKey());
	}

	@Override
	public void mousePressed(MouseEvent m) {
		if (cp5.isMouseOver()) {
			return;
		}

		mouseDown.add(m.getButton());
		mouseClickXY = mouseCoords.copy();
		mouseReleasedXY = null;

		switch (m.getButton()) {
			case LEFT :
				nodeMouseOver();
				if (mouseOverState == null) {
					if (!(selected.isEmpty())) {
						selected.forEach(s -> s.deselect());
						selected.clear();
					} else {
						cursor(HAND);
						dragState = new State(mouseClickXY, nodes.size());
					}

				} else {
					if (!mouseOverState.cp5.isMouseOver()) {
						cursor(HAND);
						dragState = mouseOverState;
						nodes.remove(dragState);
						selected.add(dragState);
						dragState.select();
					}
				}
				break;

			case RIGHT :
				nodeMouseOver();
				if (!(mouseOverState == null) && allowNewArrow) {
					arrowTailState = mouseOverState;
					drawingArrow = new Arrow(mouseClickXY, arrowTailState);
					cursor(CROSS);
				}
				break;

			default :
				break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent m) {
		cursor(ARROW);
		mouseReleasedXY = mouseCoords.copy();
		if (cp5.isMouseOver()) {
			return;
		}

		if (selectionBox != null) {
			selected.forEach(s -> s.deselect());
			selected.clear();
			for (State s : nodes) {
				if (withinSelection(s)) {
					s.selected = true;
					selected.add(s);
				}
			}
			selectionBox = null;
		}

		switch (m.getButton()) {
			case LEFT :
				nodeMouseOver();
				if (dragState != null) {
					selected.remove(dragState);
					dragState.deselect();
					nodes.add(dragState);
					dragState = null;
				}
				break;

			case RIGHT :
				if (!(mouseClickXY.equals(mouseReleasedXY))) {
					nodeMouseOver();
					arrowHeadState = mouseOverState;
					if (arrowTailState != arrowHeadState && (arrowHeadState != null) && drawingArrow != null) {
						drawingArrow.tail = arrowTailState;
						drawingArrow.head = arrowHeadState;
						drawingArrow.update();
						arrowTailState.addArrowTail(drawingArrow);
						arrowHeadState.addArrowHead(drawingArrow);
						arrows.add(drawingArrow);
					}
					drawingArrow = null;
					if (arrowHeadState == null) {
						allowNewArrow = true;
					}
				} else {
					drawingArrow = null;
					// nodeMouseOver();
					if (mouseOverState != null) {
						selected.add(mouseOverState);
						mouseOverState.select();
						mouseOverState.showUI();
					}
				}
				break;

			default :
				break;
		}
		mouseDown.remove(m.getButton());
		mouseClickXY = null;
	}

	public void mouseDragged(MouseEvent m) {
		if (mouseDown.contains(RIGHT) && selectionBox == null && drawingArrow == null) {
			selectionBox = new SelectionBox(this, mouseCoords);
		}
	}
}