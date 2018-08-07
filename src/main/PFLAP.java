package main;

//import java.io.File;
import java.awt.Color;
import java.awt.Frame;


import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashSet;

import commands.addState;

import commands.moveState;
import commands.addTransition;
import commands.deleteState;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import controlP5.ControlP5;
import controlP5.Textarea;

import machines.DFA;
import machines.DPA;
import machines.Machine;
import p5.Arrow;

import p5.Notification;
import p5.SelectionBox;
import p5.State;

import processing.awt.*;

import static main.Functions.withinRange;
import static main.Functions.withinRegion;

//@formatter:off
/**
 * State self bezier-arrows. 
 * Right-click menu on arrows 
 * DPA fully integrated with states and transitions.
 * delete transitions
 * modify transitions
 * info about machine (#states, etc)
 * mutli selection creating transtion makes multiple transitions
 * state resize / transition thickness
 * PGraphics.begindraw for screenshot transparency
 * DFA: if adding transition w/ same head & tail, merge into existing
 * make machine non-static of type generic
 * split arrow 2: temp arrow and final arrow, pass only head tail to constructor for final
 * BUG: symbol length > 1 notifcation multiple appearance
 * history handler GUI for user
 * initial state glitched
 * make states arrows etc pgraphics objects 
 * add undo redo hotkeys and button in edit menu
 * multi-character DPA transition
 */
//@formatter:on

public class PFLAP {

	public static ArrayList<Arrow> arrows = new ArrayList<>();
	public static ArrayList<State> nodes = new ArrayList<>();
	public static HashSet<State> selected = new HashSet<>();
	
	public static boolean allowGUIInterraction = true;

	public static PApplet p;
	
	public static ControlP5 cp5;
	
	public static enum modes {
		DFA, DPA;
	}

	public static Machine machine;

	public static modes mode = modes.DFA; // TODO change for test

	public static Color stateColour = new Color(255, 220, 0), stateSelectedColour = new Color(0, 35, 255),
			transitionColour = new Color(0, 0, 0), bgColour = new Color(255, 255, 255);

	public static void main(String[] args) {
		processing.init();
	}

	public final static class processing extends PApplet {
		
		private static HashSet<Character> keysDown = new HashSet<Character>();
		private static HashSet<Integer> mouseDown = new HashSet<Integer>();
		private static PFont comfortaaRegular, comfortaaBold;
		private static State mouseOverState, arrowTailState, arrowHeadState, dragState;
		private static Arrow drawingArrow, mouseOverTransition;
		private static SelectionBox selectionBox = null;
		private static boolean fullScreen = false, newState = false;
		private static PVector mouseClickXY, mouseReleasedXY, mouseCoords;
		protected static Textarea trace;
		
		public static void init() {
			PApplet.main(processing.class);
		}

		@Override
		public void setup() {
			p = this;
			frame = getFrame();
			surface.setTitle(Consts.title);
			surface.setLocation(displayWidth / 2 - width / 2, displayHeight / 2 - height / 2);
			surface.setResizable(false);
			surface.setResizable(true);
			comfortaaRegular = createFont("Comfortaa Regular", 24, true);
			comfortaaBold = createFont("Comfortaa Bold", Consts.stateFontSize, true);
			textFont(comfortaaBold);
			frameRate(60);
			strokeJoin(MITER);
			strokeWeight(3);
			stroke(0);
			textSize(Consts.stateFontSize);
			textAlign(CENTER, CENTER);
			rectMode(CORNER);
			ellipseMode(CENTER);
			cursor(ARROW);
			colorMode(RGB);
			InitUI.initCp5();
			InitUI.initMenuBar(getFrame());
			machine = new DFA(); // TODO Change based on option.
		}

		@Override
		public void settings() {
			size(Consts.WIDTH, Consts.HEIGHT);
			smooth(8);
		}

		@Override
		public void draw() {
			mouseCoords = new PVector(constrain(mouseX, 0, width), constrain(mouseY, 0, height));
			background(bgColour.getRGB());

			if (drawingArrow != null) {
				stroke(0, 0, 0, 80);
				strokeWeight(2);
				drawingArrow.setHeadXY(mouseCoords);
				drawingArrow.draw();
			}
			if (selectionBox != null) {
				selectionBox.setEndPosition(mouseCoords);
				selectionBox.draw();
			}

			fill(0);
			strokeWeight(2);
			stroke(transitionColour.getRGB());
			textSize(18);
			textFont(comfortaaRegular);
			arrows.forEach(a -> a.draw());

			textSize(Consts.stateFontSize);
			textFont(comfortaaBold);
			stroke(0);
			strokeWeight(2);
			nodes.forEach(s -> s.draw());

			if (dragState != null) {
				dragState.setPosition(mouseCoords);
				dragState.draw();
			}
			HistoryHandler.executeBufferedCommands(); // TODO
			Notification.run();
		}

		private Frame getFrame() {
			Frame frame = null;
			try {
				Field f = ((PSurfaceAWT) surface).getClass().getDeclaredField("frame");
				f.setAccessible(true);
				frame = (Frame) (f.get(((PSurfaceAWT) surface)));
			} catch (Exception e) {
				println(e);
			}
			return frame;
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
			keysDown.add(e.getKey());
			switch (e.getKey()) { // TODO change hotkey
				case 'n' :
					HistoryHandler.undo();
					break;
				case 'm' :
					HistoryHandler.redo();
					break;
				case 'x' :
					HistoryHandler.debug();
				default :
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent key) {
			switch (key.getKeyCode()) {
				case 127 : // 127 == delete key
					HistoryHandler.buffer(new deleteState(selected));
					selected.clear();
					break;
				case 32 : // TODO remove (temp)
					machine.debug();
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
				default :
					break;
			}
			keysDown.remove(key.getKey());
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
					nodeMouseOver();
					transitionMouseOver();
					if (!(mouseOverState == null) && allowGUIInterraction && mouseOverTransition == null) {
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
			if (cp5.isMouseOver() || !allowGUIInterraction) {
				return;
			}

			switch (m.getButton()) {
				case LEFT :
					// selected.forEach(s -> {
					// if (!(s.UIOpen())) {
					// println("asd");
					// s.deselect();
					// selected.remove(s); //TODO
					// }
					// });

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
													// but
													// not command
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