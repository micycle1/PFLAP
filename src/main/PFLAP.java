package main;

import java.awt.Color;

import java.util.ArrayList;
import java.util.HashSet;

import commands.Batch;
import commands.addState;

import commands.moveState;
import commands.addTransition;

import processing.core.PFont;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import controlP5.ControlP5;
import controlP5.Textarea;
import machines.DFA;
import machines.DPA;
import machines.Machine;
import machines.Mealy;
import machines.Moore;

import p5.Arrow;
import p5.Notification;
import p5.SelectionBox;
import p5.State;

import static main.Functions.withinRange;
import static main.Functions.withinRegion;

/**
 * DPA fully integrated with states and transitions.
 * DFA: if adding transition w/ same head & tail, merge into existing
 * multi-character DPA transition
 * lambda transitions (space)
 */

public class PFLAP {

	public static ArrayList<Arrow> arrows = new ArrayList<>();
	public static ArrayList<State> nodes = new ArrayList<>();
	public static HashSet<State> selected = new HashSet<>();

	public static boolean allowGUIInterraction = true;

	public static PApplet p;

	public static ControlP5 cp5;

	public static enum modes {
		DFA, DPA, MEALY, MOORE;
	}

	public static Machine machine;

	public static modes mode = modes.DPA; // TODO change for test

	public static Color stateColour = new Color(255, 220, 0), stateSelectedColour = new Color(0, 35, 255),
			transitionColour = new Color(0, 0, 0), bgColour = new Color(255, 255, 255);

	public static void main(String[] args) {
		PApplet.init();
	}

	public static void changeMode(modes newMode) {
		mode = newMode;
		PApplet.reset();
		Notification.addNotification("Mode Changed", mode + " mode selected.");
	}

	public final static class PApplet extends processing.core.PApplet {

		private static HashSet<Integer> keysDown = new HashSet<Integer>();
		private static HashSet<Integer> mouseDown = new HashSet<Integer>();
		private static PFont comfortaaRegular, comfortaaBold;
		private static State mouseOverState, arrowTailState, arrowHeadState, dragState;
		private static Arrow drawingArrow, mouseOverTransition;
		private static SelectionBox selectionBox;
		private static boolean fullScreen = false, newState = false;
		private static PVector mouseClickXY, mouseReleasedXY, mouseCoords;
		protected static Textarea trace;

		public static void init() {
			main(PApplet.class);
		}

		@Override
		public void settings() {
			print("a");
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

			FontTextParameters : {
				try {
					surface.setIcon(loadImage("assets\\icon_small.png"));
				} catch (NullPointerException e) {
					System.err.println("Icon file not found.");
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
			machine = new DFA(); // TODO Change based on option.
			changeMode(modes.DFA); // todo
		}

		@SuppressWarnings("unused")
		@Override
		public void draw() {
			mouseCoords = new PVector(constrain(mouseX, 0, width), constrain(mouseY, 0, height));
			background(bgColour.getRGB());

			if (drawingArrow != null) {
				stroke(transitionColour.getRed(), transitionColour.getGreen(), transitionColour.getBlue(), 80);
				strokeWeight(2);
				drawingArrow.setHeadXY(mouseCoords);
				drawingArrow.tempUpdate();
				drawingArrow.draw();
			}
			if (selectionBox != null) {
				selectionBox.setEndPosition(mouseCoords);
				selectionBox.draw();
			}

			drawTransitions : {
				textAlign(CENTER, CENTER);
				fill(0);
				strokeWeight(2);
				stroke(transitionColour.getRGB());
				textSize(18);
				textFont(comfortaaRegular);
				arrows.forEach(a -> a.draw());
			}

			drawStates : {
				textSize(Consts.stateFontSize);
				textFont(comfortaaBold);
				stroke(0);
				strokeWeight(2);
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
			HistoryHandler.resetAll();
			arrows.clear();
			nodes.clear();
			selected.clear();
			Notification.clear();
			mouseOverState = null;
			arrowTailState = null;
			arrowHeadState = null;
			dragState = null;
			drawingArrow = null;
			mouseOverTransition = null;
			switch (mode) {
				case DFA :
					machine = new DFA();
					break;
				case DPA :
					machine = new DPA();
					break;
				case MEALY :
					machine = new Mealy();// TODO
					break;
				case MOORE :
					machine = new Moore();
					break;
				default :
					break;
			}
		}

		private void nodeMouseOver() {
			for (State s : nodes) {
				if (withinRange(s.getPosition().x, s.getPosition().y, s.getRadius(), mouseX, mouseY)
						|| s.isMouseOver()) {
					mouseOverState = s;
					return;
				}
			}
			mouseOverState = null;
		}

		private void transitionMouseOver() {
			for (Arrow a : arrows) {
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
			switch (e.getKey()) { // TODO change hotkey
				case 'x' :
					HistoryHandler.debug();
				default :
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
					// HistoryHandler.buffer(new deleteState(selected));
					HistoryHandler.buffer(new Batch(Batch.createDeleteBatch(selected)));
					selected.clear();
					break;
				case 32 : // 32 == ' '
					machine.debug(); // TODO remove (temp)
					break;
				case LEFT :
					Step.stepBackward();
					break;
				case RIGHT :
					Step.stepForward();
					break;
				case 122 : // F11
					if (fullScreen) {
						surface.setSize(Consts.WIDTH, Consts.HEIGHT);
						surface.setLocation(displayWidth / 2 - width / 2, displayHeight / 2 - height / 2);
						// constrain node loc? todo
					} else {
						surface.setSize(displayWidth, displayHeight);
						surface.setLocation(0, 0);
					}
					fullScreen = !fullScreen;
					break;
				default :
					break;
			}
			keysDown.remove(key.getKeyCode());
		}

		@Override
		public void mousePressed(MouseEvent m) {
			if (cp5.isMouseOver() || !allowGUIInterraction) {
				return;
			}
			mouseDown.add(m.getButton());
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
							drawingArrow = new Arrow(mouseClickXY, arrowTailState);
							cursor(CROSS);
						}
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
			if (cp5.isMouseOver() || !allowGUIInterraction) {
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
							newState = false;
						} else {
							// dragged existing state
							HistoryHandler.buffer(new moveState(dragState, mouseClickXY));
							nodes.add(dragState); // re-add dragstate to list
													// but not command
						}
						selected.remove(dragState);
						dragState.deselect();
						dragState = null;
					}
					break;
				case RIGHT :
					if (selectionBox != null) {
						selected.forEach(s -> s.deselect());
						selected.clear();
						for (State s : nodes) {
							if (withinRegion(s.getPosition(), selectionBox.startPosition, selectionBox.endPosition)) {
								s.select();
								selected.add(s);
							}
						}
						selectionBox = null;
					} else {
						if (!(mouseClickXY.equals(mouseReleasedXY))) {
							nodeMouseOver();
							arrowHeadState = mouseOverState;
							if (arrowTailState != arrowHeadState && (arrowHeadState != null) && drawingArrow != null) {
								// TODO change logic for self transition
								allowGUIInterraction = false;
								HistoryHandler.buffer(new addTransition(arrowTailState, arrowHeadState));
							}
							drawingArrow = null;
							if (arrowHeadState == null) {
								allowGUIInterraction = true;
							}
						} else {
							drawingArrow = null;
							if (mouseOverState != null) {
								selected.add(mouseOverState);
								mouseOverState.select();
								mouseOverState.showUI();
							} else {
								transitionMouseOver();
								if (mouseOverTransition != null) {
									mouseOverTransition.showUI(); // TODO
								}
							}
						}
					}
					break;

				case CENTER :
					selected.forEach(s -> s.select());
					break;

				default :
					break;
			}
			mouseDown.remove(m.getButton());
		}

		@Override
		public void mouseDragged(MouseEvent m) {
			switch (m.getButton()) {
				case LEFT :
					break;
				case RIGHT :
					if (selectionBox == null && drawingArrow == null && allowGUIInterraction) {
						selectionBox = new SelectionBox(mouseCoords);
					}
					break;
				case CENTER :
					PVector offset = new PVector(mouseX - mouseClickXY.x, mouseY - mouseClickXY.y);
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