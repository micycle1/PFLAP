package main;

import java.awt.Color;

import java.util.ArrayList;
import java.util.HashSet;

import commands.Batch;
import commands.Command;
import commands.addState;
import commands.moveState;
import commands.addTransition;
import commands.deleteState;

import processing.core.PFont;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import controlP5.ControlFont;
import controlP5.ControlP5;
import controlP5.Textarea;

import org.gicentre.utils.move.*;

import machines.DFA;
import machines.DPA;
import machines.Machine;
import machines.Mealy;
import machines.Moore;
import p5.AbstractArrow;
import p5.Notification;
import p5.SelectionBox;
import p5.State;

import static main.Functions.angleBetween;
import static main.Functions.withinRange;
import static main.Functions.withinRegion;

/**
 * @author micycle1
 * @version 1.0
 * todo existing arrow gui showing when new one added
 * remove cp5 when arrow/states deleted
 */
public final class PFLAP {

	public static final ArrayList<AbstractArrow> arrows = new ArrayList<>();
	public static final ArrayList<State> nodes = new ArrayList<>();
	protected static final HashSet<State> selected = new HashSet<>();

	public static boolean allowGUIInterraction = true;

	public static PApplet p;

	public static ControlP5 cp5;
	public static ControlFont cp5Font;

	public static enum modes {
		DFA, DPA, MEALY, MOORE;
	}

	public static Machine machine;

	public static modes mode;

	public static Color stateColour = new Color(255, 220, 0), stateSelectedColour = new Color(0, 35, 255),
			transitionColour = new Color(0, 0, 0), bgColour = new Color(255, 255, 255);

	public static float zoom;

	public static void main(String[] args) {
		PApplet.init();
	}

	/**
	 * PFLAP runs here.
	 * This is where Processing's draw(), etc. are located.
	 * @author micycle1
	 */
	public final static class PApplet extends processing.core.PApplet {

		private static final HashSet<Integer> keysDown = new HashSet<Integer>();
		private static PFont comfortaaRegular, comfortaaBold;
		private static State mouseOverState, arrowTailState, arrowHeadState, dragState;
		private static AbstractArrow mouseOverTransition;
		private static SelectionBox selectionBox;
		private static boolean fullScreen = false, newState = false, drawingArrow = false;
		private static PVector mouseClickXY, mouseReleasedXY, mouseCoords;
		protected static Textarea trace;
		private static ArrayList<Command> moveCache;
		private static ZoomPan zoomPan;

		private static void init() {
			main(PApplet.class);
		}

		@Override
		public void settings() {
			size(Consts.WIDTH, Consts.HEIGHT);
			smooth(8);
		}

		@SuppressWarnings("unused")
		@Override
		public void setup() {
			p = this;

			surface.setTitle(Consts.title);
			surface.setLocation(displayWidth / 2 - width / 2, displayHeight / 2 - height / 2);
			surface.setResizable(true);

			zoom = 1;
			zoomPan = new ZoomPan(this);
			zoomPan.setMouseMask(SHIFT);
			zoomPan.setMaxZoomScale(3);
			zoomPan.setMinZoomScale(0.5);
			zoomPan.addZoomPanListener(new ZoomPanListener() {
				@Override
				public void zoomEnded() {
					// TODO
				}
				@Override
				public void panEnded() {
					// TODO
				}
			});

			FontTextParameters : {
				try {
					surface.setIcon(loadImage("icon_small.png"));
				} catch (NullPointerException e) {
				}

				comfortaaRegular = createFont("Comfortaa.ttf", 24, true);
				if (comfortaaRegular == null) {
					comfortaaRegular = createDefaultFont(24);
				}
				comfortaaBold = createFont("Comfortaa-Bold.ttf", Consts.stateFontSize, true);
				if (comfortaaBold == null) {
					comfortaaRegular = createDefaultFont(Consts.stateFontSize);
				}
				textFont(comfortaaBold);
				textSize(Consts.stateFontSize);
				textAlign(CENTER, CENTER);
				cp5Font = new ControlFont(comfortaaBold, 11);
			}

			DrawingParameters : {
				frameRate(60);
				strokeJoin(MITER);
				strokeWeight(3);
				stroke(0);
				rectMode(CORNER);
				ellipseMode(CENTER);
				curveTightness(-4);
				colorMode(RGB);
			}

			cursor(ARROW);
			InitUI.initCp5();
			InitUI.initMenuBar();
			mode = modes.DFA;
			reset();
		}

		@SuppressWarnings("unused")
		@Override
		public void draw() {
			background(bgColour.getRGB());
			zoomPan.transform();
			zoom = (float) zoomPan.getZoomScale();
			mouseCoords = new PVector(constrain(zoomPan.getMouseCoord().x, 0, width),
					constrain(zoomPan.getMouseCoord().y, 0, height));

			if (drawingArrow) {
				float angle = angleBetween(mouseClickXY, new PVector(mouseCoords.x, mouseCoords.y)) + PI % TWO_PI;
				noFill();
				stroke(transitionColour.getRed(), transitionColour.getGreen(), transitionColour.getBlue(), 80);
				pushMatrix();
				translate(mouseCoords.x, mouseCoords.y);
				rotate(angle);
				beginShape();
				vertex(-10, -7);
				vertex(0, 0);
				vertex(-10, 7);
				endShape();
				popMatrix();
				strokeWeight(2);
				line(mouseClickXY.x, mouseClickXY.y, mouseCoords.x, mouseCoords.y);
			}
			if (selectionBox != null) {
				selectionBox.setEndPosition(mouseCoords);
				selectionBox.draw();
			}

			drawTransitions : {
				textAlign(CENTER, CENTER);
				noFill();
				strokeWeight(2);
				stroke(transitionColour.getRGB());
				textSize(18);
				textFont(comfortaaRegular);
				arrows.forEach(a -> a.draw());
			}

			drawStates : {
				textAlign(CENTER, CENTER);
				textSize(Consts.stateFontSize);
				textFont(comfortaaBold);
				stroke(0);
				strokeWeight(3);
				nodes.forEach(s -> s.draw());
			}

			if (dragState != null) {
				dragState.setPosition(mouseCoords);
				dragState.draw();
			}

			HistoryHandler.executeBufferedCommands();
			Notification.run();
			Step.draw();
		}

		public static void reset() {
			// HistoryHandler.resetAll();
			arrows.clear();
			nodes.clear();
			selected.clear();
			Notification.clear();
			Step.endStep();
			mouseOverState = null;
			arrowTailState = null;
			arrowHeadState = null;
			dragState = null;
			drawingArrow = false;
			mouseOverTransition = null;
			DPA.hideUI();
			switch (mode) {
				case DFA :
					machine = new DFA();
					break;
				case DPA :
					machine = new DPA();
					DPA.showUI();
					break;
				case MEALY :
					machine = new Mealy();
					break;
				case MOORE :
					machine = new Moore();
					break;
				default :
					break;
			}
		}

		protected static void setZoom(float zoom) {
			zoomPan.reset();
			zoomPan.setZoomScale(zoom);
		}

		private void nodeMouseOver() {
			for (State s : nodes) {
				if (withinRange(s.getPosition().x, s.getPosition().y, s.getRadius(), mouseCoords.x, mouseCoords.y)
						|| s.isMouseOver()) {
					mouseOverState = s;
					return;
				}
			}
			mouseOverState = null;
		}

		private void transitionMouseOver() {
			for (AbstractArrow a : arrows) {
				if (a.isMouseOver(mouseClickXY)) {
					mouseOverTransition = a;
					return;
				}
			}
			mouseOverTransition = null;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			keysDown.add(e.getKeyCode());
			switch (e.getKey()) {
				default :
					switch (e.getKeyCode()) {
						case LEFT :
							Step.stepBackward();
							break;
						case RIGHT :
							Step.stepForward();
							break;
						default :
							break;
					}
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent key) {
			if (this.key == 26) { // CTRL-Z
				HistoryHandler.undo();
			}
			if (this.key == 25) { // CTRL-Y
				HistoryHandler.redo();
			}
			switch (key.getKeyCode()) {
				case 127 : // 127 == delete key
					if (!selected.isEmpty()) {
						if (selected.size() == 1) {
							HistoryHandler.buffer(new deleteState(selected.iterator().next()));
						} else {
							HistoryHandler.buffer(new Batch(Batch.createDeleteBatch(selected)));
						}
						selected.clear();
					}
					break;
				case 122 : // F11
					if (fullScreen) {
						surface.setSize(Consts.WIDTH, Consts.HEIGHT);
						surface.setLocation(displayWidth / 2 - width / 2, displayHeight / 2 - height / 2);
					} else {
						surface.setSize(displayWidth, displayHeight);
						surface.setLocation(0, 0);
					}
					fullScreen = !fullScreen;
					break;
				case 72 : // CTRL-H
					if (keysDown.contains(CONTROL)) {
						HistoryList.toggleVisible(); // not synced with gui
					}
					break;
				default :
					break;
			}
			keysDown.remove(key.getKeyCode());
		}

		@Override
		public void mousePressed(MouseEvent m) {
			if (cp5.isMouseOver() || !allowGUIInterraction || HistoryList.isMouseOver() || keysDown.contains(SHIFT)) {
				return;
			}
			mouseClickXY = mouseCoords.copy();
			switch (m.getButton()) {
				case LEFT :
					nodeMouseOver();
					transitionMouseOver();
					if (mouseOverState == null && mouseOverTransition == null) {
						// mouse over empty region
						if (!(selected.isEmpty())) {
							selected.forEach(s -> s.deselect());
							selected.clear();
						} else {
							if (selectionBox == null) {
								// create new state
								cursor(HAND);
								dragState = new State(mouseClickXY, nodes.size());
								newState = true;
							}
						}
					} else {
						if (mouseOverState != null) {
							if (!mouseOverState.isMouseOver()) {
								// move existing state
								cursor(HAND);
								dragState = mouseOverState;
								nodes.remove(dragState);
								selected.add(dragState);
								dragState.select();
							}
						} else {
							if (mouseOverTransition != null) {
								// clicked on transition GUI
							}
						}
					}
					break;

				case RIGHT :
					if (dragState == null) {
						nodeMouseOver();
						selected.forEach(s -> s.deselect());
						selected.clear();
						transitionMouseOver();
						if (!(mouseOverState == null) && allowGUIInterraction && mouseOverTransition == null) {
							arrowTailState = mouseOverState;
							drawingArrow = true;
							cursor(CROSS);
						}
					}
					break;
				case CENTER :
					if (!selected.isEmpty()) {
						moveCache = Batch.createMoveBatch(selected);
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
			if (cp5.isMouseOver() || (!allowGUIInterraction && dragState == null) || keysDown.contains(SHIFT)) {
				return;
			}
			arrows.forEach(a -> a.hideUI());
			switch (m.getButton()) {
				case LEFT :
					nodeMouseOver();
					if (dragState != null) {
						// drop dragged state
						if (newState) {
							HistoryHandler.buffer(new addState(dragState));
							// todo check for cycle then create bezier
							// then update outgoing arrows here
							newState = false;
						} else {
							// dragged existing state
							HistoryHandler.buffer(new moveState(dragState, mouseClickXY));
							nodes.add(dragState); // re-add dragstate to list
													// but not command
						}
						selected.forEach(s -> s.deselect());
						selected.remove(dragState);
						dragState = null;
					}
					break;
				case RIGHT :
					if (selectionBox != null) {
						selected.forEach(s -> s.deselect());
						selected.clear();
						for (State s : nodes) {
							if (withinRegion(s.getPosition(), selectionBox.startPosition, mouseCoords)) {
								s.select();
								selected.add(s);
							}
						}
						selectionBox = null;
					} else {
						if (!(mouseClickXY.equals(mouseReleasedXY))) {
							nodeMouseOver();
							arrowHeadState = mouseOverState;
							if (arrowTailState != arrowHeadState && (arrowHeadState != null) && drawingArrow == true) {
								allowGUIInterraction = false;
								HistoryHandler.buffer(new addTransition(arrowTailState, arrowHeadState));
							}
							drawingArrow = false;
							if (arrowHeadState == null) {
								allowGUIInterraction = true;
							}
						} else {
							drawingArrow = false;
							if (mouseOverState != null) {
								selected.add(mouseOverState);
								mouseOverState.select();
								mouseOverState.showUI();
							} else {
								transitionMouseOver();
								if (mouseOverTransition != null) {
									mouseOverTransition.showUI();
								}
							}
						}
					}
					break;

				case CENTER :
					if (!selected.isEmpty() && !mouseClickXY.equals(mouseReleasedXY)) {
						selected.forEach(s -> s.select());
						moveCache.forEach(c -> ((moveState) c).updatePos());
						HistoryHandler.buffer(new Batch(moveCache));
					}
					break;

				default :
					break;
			}
		}

		@Override
		public void mouseDragged(MouseEvent m) {
			if (keysDown.contains(SHIFT)) {
				return;
			}
			switch (m.getButton()) {
				case LEFT :
					break;
				case RIGHT :
					if (selectionBox == null && drawingArrow == false && allowGUIInterraction
							&& !HistoryList.isMouseOver()) {
						selectionBox = new SelectionBox(mouseCoords);
					}
					break;
				case CENTER :
					PVector offset = new PVector(mouseCoords.x - mouseClickXY.x, mouseCoords.y - mouseClickXY.y);
					for (State s : selected) {
						s.setPosition(new PVector(constrain(offset.x + s.getSelectedPosition().x, 0, width),
								constrain(offset.y + s.getSelectedPosition().y, 0, height)));
					}
					break;
				default :
					break;
			}
		}

		@Override
		public void exit() {
			/**
			 * Finish-up
			 */
			super.exit();
		}
	}
}