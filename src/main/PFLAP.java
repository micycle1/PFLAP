package main;

import static main.Functions.angleBetween;

import java.util.ArrayList;
import java.util.HashSet;

import org.gicentre.utils.move.ZoomPan;

import commands.Batch;
import commands.Command;
import commands.deleteState;
import commands.moveState;

import controlP5.ControlFont;
import controlP5.ControlP5;

import javafx.App;
import javafx.Controller;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

import machines.DFA;
import machines.DPA;
import machines.Mealy;
import machines.Moore;

import model.Model;

import p5.AbstractArrow;
import p5.Notification;
import p5.SelectionBox;
import p5.State;

import processing.core.PFont;
import processing.core.PSurface;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.javafx.PSurfaceFX;
import transitionView.View;

/**
 * @author micycle1
 * @version 1.5
 */
public final class PFLAP {

	public static boolean allowGUIInterraction = true;

	public static PApplet p;

	public static ControlP5 cp5;
	public static ControlFont cp5Font;

	public static enum modes {
		DFA, DPA, MEALY, MOORE;
	}

	public static modes mode;

	public static Color stateColour = Color.rgb(255, 220, 0), stateSelectedColour = Color.rgb(0, 35, 255),
			transitionColour = Color.rgb(0, 0, 0), bgColour = Color.rgb(255, 255, 255);

	public static void main(String[] args) {
		PApplet.init();
	}

	public static void reset(modes m) { // mode change
		mode = m;
		PApplet.reset = true;
	}
	
	public static void reset() { // same mode
		PApplet.reset = true;
	}

	/**
	 * PFLAP runs here.
	 * This is where Processing's draw(), etc. are located.
	 */
	public final static class PApplet extends processing.core.PApplet {

		private static final HashSet<Integer> keysDown = new HashSet<Integer>();
		private static PFont comfortaaRegular, comfortaaBold;
		private static State mouseOverState, arrowTailState, arrowHeadState, dragState;
		private static SelectionBox selectionBox;
		private static boolean newState = false, drawingArrow = false, reset = false;
		private static PVector mouseClickXY, mouseReleasedXY, mouseCoords, zoomPanOffsetp;
		private static ZoomPan zoomPan;
		private static ArrayList<Command> multiMoveCache;
		public static HistoryList historyList;
		private static int zoomPanLastTime = -128;
		private double zoomPanScalep;

		public static View view;

		public static Controller controller;

		private static void init() {
			main(PApplet.class);
		}

		@Override
		protected PSurface initSurface() {
			g = createPrimaryGraphics();
			PSurface genericSurface = g.createSurface();
			PSurfaceFX fxSurface = (PSurfaceFX) genericSurface;
			fxSurface.sketch = this;
			App.surface = fxSurface;
			Controller.surface = fxSurface;

			new Thread(() -> Application.launch(App.class)).start();

			while (fxSurface.stage == null) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}
			}
			this.surface = fxSurface;
			return surface;
		}

		@Override
		public void settings() {
			size(0, 0, FX2D);
			smooth(4);
		}

		@SuppressWarnings("unused")
		@Override
		public void setup() {
			p = this;

			zoomPan = new ZoomPan(this);
			zoomPan.setMouseMask(CONTROL);
			zoomPan.setMaxZoomScale(3);
			zoomPan.setMinZoomScale(0.5);

			FontTextParameters : {
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
			cp5 = new ControlP5(p);
			cp5.setFont(PFLAP.cp5Font);
			mode = modes.DFA;
			view = new View(this);
			historyList = new HistoryList();

			zoomPanScalep = zoomPan.getZoomScale();
			zoomPanOffsetp = zoomPan.getPanOffset();
			
			Controller.stage.widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					width = newValue.intValue() - 16;
					main.Step.stageResized();
				}
			});
			Controller.stage.heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					height = newValue.intValue() - 68;
					main.Step.stageResized();
				}
			});
		}

		@Override
		public void draw() {
			background(Functions.colorToRGB(bgColour));

			HistoryHandler.executeBufferedCommands();
			Step.draw();
			Notification.run();

			if (zoomPan.getZoomScale() != zoomPanScalep || !zoomPan.getPanOffset().equals(zoomPanOffsetp)) {
				zoomPanLastTime = frameCount;
			}
			if (frameCount - zoomPanLastTime < 128) {
				fill(0, 255 - (frameCount - zoomPanLastTime) * 2);
				textSize(12);
				textAlign(LEFT, TOP);
				text("Zoom: " + String.format("%.3g%n", zoomPan.getZoomScale()) + "X-pos: "
						+ String.format("%.3g%n", zoomPan.getPanOffset().x) + "Y-pos: "
						+ String.format("%.3g%n", zoomPan.getPanOffset().y), width - 80, 10);
			}

			zoomPan.transform();
			mouseCoords = new PVector(constrain(zoomPan.getMouseCoord().x, 0, width),
					constrain(zoomPan.getMouseCoord().y, 0, height));

			if (drawingArrow) {
				float angle = angleBetween(mouseClickXY, new PVector(mouseCoords.x, mouseCoords.y)) + PI % TWO_PI;
				noFill();
				stroke((float) transitionColour.getRed(), (float) transitionColour.getGreen(),
						(float) transitionColour.getBlue(), 80);
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

			view.draw();

			if (reset) {
				reset();
			}

			zoomPanScalep = zoomPan.getZoomScale();
			zoomPanOffsetp = zoomPan.getPanOffset();
		}

		public static void reset() {
			view.reset();
			Step.endStep();
			HistoryHandler.reset();
			mouseOverState = null;
			arrowTailState = null;
			arrowHeadState = null;
			dragState = null;
			drawingArrow = false;
			DPA.hideUI();
			switch (mode) {
				case DFA :
					Model.reset(new DFA());
					break;
				case DPA :
					Model.reset(new DPA());
					DPA.showUI();
					break;
				case MEALY :
					Model.reset(new Mealy());
					break;
				case MOORE :
					Model.reset(new Moore());
					break;
				default :
					break;
			}
			historyList.update();
			reset = false;
		}

		public static void setZoom(float zoom) {
			zoomPan.reset();
			zoomPan.setZoomScale(zoom);
		}

		public void deleteSelection() {
			if (!view.getSelectedStates().isEmpty()) {
				if (view.getSelectedStates().size() == 1) {
					HistoryHandler.buffer(new deleteState(view.getSelectedStates().iterator().next()));
				} else {
					HistoryHandler.buffer(new Batch(Batch.createDeleteBatch(view.getSelectedStates())));
				}
			}
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
			switch (key.getKeyCode()) {
				case 127 : // 127 == delete key
					deleteSelection();
					break;
				default :
					break;
			}
			keysDown.remove(key.getKeyCode());
		}

		@Override
		public void mousePressed(MouseEvent m) {
			if (cp5.isMouseOver() || !allowGUIInterraction || historyList.isMouseOver() || keysDown.contains(SHIFT)) {
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
						view.deselectAllStates();
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
						if (drawingArrow == false && allowGUIInterraction && !historyList.isMouseOver()) {
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