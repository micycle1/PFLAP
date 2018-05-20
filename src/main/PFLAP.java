package main;

import java.util.ArrayList;
import java.util.HashSet;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;
import controlP5.Textfield;
import p5.Arrow;
import p5.SelectionBox;
import p5.State;

public class PFLAP extends PApplet {

	public static HashSet<Character> keysDown = new HashSet<Character>();
	public static HashSet<Integer> mouseDown = new HashSet<Integer>();

	public static ArrayList<Arrow> arrows = new ArrayList<>();
	public static ArrayList<State> Nodes = new ArrayList<>();

	private static ControlP5 cp5;
	private static ScrollableList mouseFunction;
	private static Textfield tf1; // TODO User text entry

	private static State mouseOverState, arrowTailState, arrowHeadState, dragState;
	private static Arrow drawingArrow;
	private static SelectionBox selectionBox = null;

	private PVector mouseClickXY, mouseReleasedXY, mouseCoords;

	private static String debug = "debug"; // TODO remove

	public static void main(String[] args) {
		PApplet.main(PFLAP.class);
	}

	@Override
	public void setup() {
		State.p = this;
		Arrow.p = this;
		surface.setTitle("JFLAP ~ micycle");
		frameRate(60);
		strokeJoin(MITER);
		strokeWeight(2);
		stroke(0);
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

		surface.setTitle(String.valueOf(mouseX) + "," + String.valueOf(mouseY)); // TODO
		textAlign(LEFT, TOP);
		background(255);
		fill(0);
		text(debug, 1, 1);

		if (!(drawingArrow == null)) {
			stroke(0, 0, 0, 80);
			drawingArrow.setHeadXY(mouseCoords);
			drawingArrow.draw();
		}

		if (selectionBox != null) {
			selectionBox.setEndPosition(mouseCoords);
			selectionBox.draw();
		}

		stroke(0);
		for (Arrow a : arrows) {
			a.draw();
		}

		textAlign(CENTER, CENTER);
		for (State s : Nodes) {
			s.draw();
		}
		if (dragState != null) {
			dragState.setPosition(mouseCoords);
			dragState.draw();
		}
	}

	private void initCp5() {
		cp5 = new ControlP5(this);
		// @formatter:off
		mouseFunction = cp5.addScrollableList("Mouse Function");
		mouseFunction.setOpen(false)
		.setPosition(400 - mouseFunction.getWidth() / 2, 10)
		.addItem("Drag", 0)
		.addItem("Add Node/State", 1)
		.addItem("Add Transition", 2)
		.addItem("Delete", 3)
		.changeValue(1f)
		.onChange(new CallbackListener() {
			@Override
			public void controlEvent(CallbackEvent changeCursor) {
				switch ((int) mouseFunction.getValue()) {
					case 0 : // Drag
						cursor(HAND);
						break;
					case 1 : // Add Node
						cursor(ARROW);
						break;
					case 2 : // Add Transition
						cursor(CROSS);
						break;
					case 3 : // Delete
						cursor(ARROW);
						break;
					default :
						cursor(ARROW);
						break;
				}
			}
		});
		// @formatter:on
	}

	private static boolean withinRange(float x, float y, float diameter, float x2, float y2) {
		return (sqrt(sq(y - y2) + sq(x - x2)) < diameter / 2);
	}

	public static float angleBetween(PVector tail, PVector head) {
		return -atan2(tail.x - head.x, tail.y - head.y) - (PI / 2);
	}

	private static boolean withinSelection(State s) {
		PVector sXY = new PVector(s.getPosition().x, s.getPosition().y);
		PVector bSP = new PVector(selectionBox.startPosition.x, selectionBox.startPosition.y);
		PVector bEP = new PVector(selectionBox.endPosition.x, selectionBox.endPosition.y);
		return sXY.x >= bSP.x && sXY.y >= bSP.y && sXY.x <= bEP.x && sXY.y <= bEP.y;
	}

	public void nodeMouseOver() {
		for (State s : Nodes) {
			if (withinRange(s.getPosition().x, s.getPosition().y, Consts.stateRadius, mouseX, mouseY)) {
				mouseOverState = s;
				return;
			}
		}
		mouseOverState = null;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keysDown.add(e.getKey());
	}

	@Override
	public void keyReleased(KeyEvent key) {
		switch (key.getKey()) {
			case 'm' :
				mouseFunction.changeValue(0);
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
		mouseClickXY = mouseCoords;
		mouseReleasedXY = null;

		switch ((int) mouseFunction.getValue()) { // TODO
			case 0 : // Drag
				// thread("nodeMouseOver");
				nodeMouseOver();
				dragState = mouseOverState;
				Nodes.remove(dragState);
				break;
			case 1 : // Add Node
				break;
			case 2 : // Add Transition
				// thread("nodeMouseOver");
				nodeMouseOver();
				if (!(mouseOverState == null)) {
					arrowTailState = mouseOverState;
					drawingArrow = new Arrow(mouseClickXY, arrowTailState);

				}
				break;
			case 3 : // Delete
				break;
			default :
				break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent m) {
		mouseReleasedXY = mouseCoords;
		if (cp5.isMouseOver()) {
			return;
		}

		if (selectionBox != null) {
			for (State s : Nodes) {
				s.selected = withinSelection(s);
			}
			selectionBox = null;
		}

		switch ((int) mouseFunction.getValue()) { // TODO
			case 0 : // Drag
				if (dragState != null) {
					Nodes.add(dragState);
					dragState = null;
				}
				break;
			case 1 : // Add Node
				if (m.getButton() == LEFT) {
					Nodes.add(new State(mouseReleasedXY, Nodes.size()));
				}
				break;
			case 2 : // Add Transition
				nodeMouseOver();
				arrowHeadState = mouseOverState;
				if (arrowTailState != arrowHeadState && (arrowHeadState != null)) {
					float theta1 = angleBetween(arrowHeadState.getPosition(), arrowTailState.getPosition());
					PVector newHeadXy = new PVector(
							arrowHeadState.getPosition().x + Consts.stateRadius * 0.5f * cos(theta1),
							arrowHeadState.getPosition().y + Consts.stateRadius * 0.5f * sin(theta1));
					drawingArrow.setHeadXY(newHeadXy);

					float theta2 = angleBetween(arrowTailState.getPosition(), arrowHeadState.getPosition());
					PVector newTailXy = new PVector(
							arrowTailState.getPosition().x + Consts.stateRadius * 0.5f * cos(theta2),
							arrowTailState.getPosition().y + Consts.stateRadius * 0.5f * sin(theta2));
					drawingArrow.setTailXY(newTailXy);
					arrows.add(drawingArrow);
					
					arrowTailState.addArrowTail(drawingArrow);
					arrowHeadState.addArrowHead(drawingArrow);
					
					drawingArrow.head = arrowHeadState;
				}
				drawingArrow = null;
				break;
			case 3 : // Delete
				if (mouseReleasedXY.equals(mouseClickXY)) {
					nodeMouseOver();
					if (mouseOverState != null) {
						Nodes.remove(mouseOverState);
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
		if (selectionBox == null && dragState == null && drawingArrow == null && mouseFunction.getValue() != 1f) {
			nodeMouseOver();
			if (mouseOverState == null) {
				selectionBox = new SelectionBox(this, mouseCoords);
			}
		}
	}
}