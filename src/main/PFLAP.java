package main;

import static main.Functions.angleBetween;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;

import org.gicentre.utils.move.ZoomPan;
import org.gicentre.utils.move.ZoomPanListener;

import commands.Batch;
import commands.Command;
import commands.moveState;

import controlP5.ControlFont;
import controlP5.ControlP5;
import controlP5.Textarea;

import machines.DFA;
import machines.DPA;
import machines.Machine;
import machines.Mealy;
import machines.Moore;

import p5.AbstractArrow;
import p5.Notification;
import p5.SelectionBox;
import p5.State;

import processing.core.PFont;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import transitionView.View;

/**
 * sync view with machine
 * @author micycle1
 * @version 1.x
 */
public final class PFLAP {

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

	public static void reset() {
		PApplet.reset = true;
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
		private static SelectionBox selectionBox;
		private static boolean fullScreen = false, newState = false, drawingArrow = false, reset = false;
		private static PVector mouseClickXY, mouseReleasedXY, mouseCoords;
		protected static Textarea trace;
		private static ZoomPan zoomPan;
		private static ArrayList<Command> multiMoveCache; 

		public static View view;

		private static void init() {
			main(PApplet.class);
		}

		@Override
		public void settings() {
			size(Consts.WIDTH, Consts.HEIGHT);
			// size(Consts.WIDTH, Consts.HEIGHT, FX2D);
			smooth(4);
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
					// TODO show new zoom GUI
				}
				@Override
				public void panEnded() {
					// TODO show new zoom GUI
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
			view = new View(this);
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

			if (dragState != null) {
				view.dragging(dragState, mouseCoords);
			}

			HistoryHandler.executeBufferedCommands();
			Notification.run();
			Step.draw();
			view.draw();

			if (reset) {
				reset();
			}
		}

		private static void reset() {
			// HistoryHandler.resetAll();

			// arrows.forEach(a -> a.disposeUI());
			// arrows.clear();
			view.reset();
			// nodes.forEach(n -> n.disposeUI());
			// nodes.clear();
			// selected.clear();
			Notification.clear();
			Step.endStep();
			mouseOverState = null;
			arrowTailState = null;
			arrowHeadState = null;
			dragState = null;
			drawingArrow = false;
			// mouseOverTransition = null;
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
			reset = false;
		}

		protected static void setZoom(float zoom) {
			zoomPan.reset();
			zoomPan.setZoomScale(zoom);
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
					// if (!selected.isEmpty()) {
					// if (selected.size() == 1) {
					// HistoryHandler.buffer(new deleteState(selected.iterator().next()));
					// } else {
					// HistoryHandler.buffer(new Batch(Batch.createDeleteBatch(selected)));
					// }
					// selected.clear();
					// }
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
			AbstractArrow mouseOver = view.transitionMouseOver(mouseClickXY);
			switch (m.getButton()) {
				case LEFT :
					mouseOverState = view.stateMouseOver(mouseClickXY);
					if (mouseOverState == null && mouseOver == null) { // mouse over empty region
						if (!(view.getSelectedStates().isEmpty())) {
							view.deselectAllStates();
						} else {
							if (selectionBox == null) { // new state
								cursor(HAND);
								dragState = view.newState(mouseCoords);
								newState = true;
							}
						}
					} else {
						if (mouseOverState != null) {
							if (!mouseOverState.isMouseOver()) { // move existing state
								cursor(HAND);
								dragState = mouseOverState;
								dragState.select();
							}
						} else {
							if (mouseOver != null) { // clicked on transition GUI
							}
						}
					}
					break;

				case RIGHT :
					if (dragState == null) {
						mouseOverState = view.stateMouseOver(mouseClickXY);
						view.deselectAllStates();
						if (!(mouseOverState == null) && allowGUIInterraction && mouseOver == null) {
							arrowTailState = mouseOverState;
							drawingArrow = true;
							cursor(CROSS);
						}
					}
					break;
				case CENTER :
					 multiMoveCache = Batch.createMoveBatch(view.getSelectedStates());
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
			view.hideUI();
			switch (m.getButton()) {
				case LEFT :
					mouseOverState = view.stateMouseOver(mouseClickXY);
					if (dragState != null) { // drop dragged state
						if (newState) {
							newState = false;
						} else { // drag existing state
							HistoryHandler.buffer(new moveState(dragState, mouseClickXY));
						}
						dragState.deselect();
						dragState = null;
					}
					break;
				case RIGHT :
					if (selectionBox != null) {
						selectionBox = null;
					} else {
						if (!(mouseClickXY.equals(mouseReleasedXY))) {
							mouseOverState = view.stateMouseOver(mouseReleasedXY);
							arrowHeadState = mouseOverState;
							if (arrowTailState != arrowHeadState && (arrowHeadState != null) && drawingArrow == true) {
								allowGUIInterraction = false;
								view.entryArrow(arrowHeadState, arrowTailState);
							}
							drawingArrow = false;
							if (arrowHeadState == null) {
								allowGUIInterraction = true;
							}
						} else { // right-click on state
							drawingArrow = false;
							if (mouseOverState != null) {
								mouseOverState.select();
								mouseOverState.showUI();
							} else {
								AbstractArrow mouseOver = view.transitionMouseOver(mouseClickXY);
								if (mouseOver != null) {
									mouseOver.showUI();
								}
							}
						}
					}
					break;

				case CENTER :
					if (!multiMoveCache.isEmpty() && !mouseClickXY.equals(mouseReleasedXY)) {
						multiMoveCache.forEach(c -> ((moveState) c).updatePos());
						HistoryHandler.buffer(new Batch(multiMoveCache));
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
					if (selectionBox == null) {
						if (drawingArrow == false && allowGUIInterraction && !HistoryList.isMouseOver()) {
							selectionBox = new SelectionBox(mouseCoords);
						}
					} else {
						view.statesInRegion(selectionBox.startPosition, mouseCoords);
					}
					break;
				case CENTER :
					view.dragging(mouseClickXY, mouseCoords);
					break;
				default :
					break;
			}
		}
	}
}