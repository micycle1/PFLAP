package main;

import static main.Consts.stepGUIPadding;
import static main.Consts.notificationData.machineAccepted;
import static main.Consts.notificationData.machineRejected;

import static main.Functions.colorToRGB;
import static main.PFLAP.p;

import java.util.ArrayList;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlListener;

import main.PFLAP.PApplet;
import model.Model;

import p5.Notification;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Call beginstep() when user clicks step-by-state.
 * <p>Class is non-initialisable.<p/>
 */
public class Step {

	private static boolean live = false, completed = false, accepted;
	private static String initialInput, remainingInput, stack;
	private static Integer liveState;
	private static ArrayList<Integer> visited;
	private static int visitedIndex;
	private static final Button closeStep, help;
	private static PGraphics staticGUI;

	private Step() {
		throw new AssertionError();
	}

	static {
		drawStaticGui();

		closeStep = new Button(PFLAP.cp5, "STEPCLOSE");
		//@formatter:off
		closeStep.addListener(new ControlListener() {
			@Override
			public void controlEvent(ControlEvent onClick) {
				endStep();
			}
		})
		.setSize(30, 18)
		.setLabel(" X")
		.setPosition(p.width - stepGUIPadding - 30 - 1, stepGUIPadding + 2)
		.setColorBackground(colorToRGB(80, 0, 0))
		.setColorForeground(colorToRGB(200, 0, 0))
		.setColorActive(colorToRGB(255, 0, 0))
		.hide()
		;
		help = new Button(PFLAP.cp5, "helpSTEP");
		help.addListener(new ControlListener() {
			@Override
			public void controlEvent(ControlEvent onClick) {
				PApplet.controller.stepModeHelp();
			}
		})
		.setSize(30, 18)
		.setLabel(" ?")
		.setPosition(p.width - stepGUIPadding - 60 - 1, stepGUIPadding + 2)
		.setColorBackground(colorToRGB(0, 0, 80))
		.setColorForeground(colorToRGB(0, 0, 200))
		.setColorActive(colorToRGB(0, 0, 255))
		.hide()
		;
		//@formatter:on
	}

	public static void beginStep(String input) {
		if (live) {
			throw new IllegalStateException("End the live stepping sequence before starting another.");
		}
		closeStep.show();
		help.show();
		live = true;
		liveState = Model.initialState;
		initialInput = input;
		remainingInput = initialInput;
		Model.beginStep(input);
		PFLAP.allowGUIInterraction = false;

		visited = new ArrayList<>();
		visited.add(liveState);
		visitedIndex = 0;
	}

	protected static void endStep() {
		live = false;
		completed = false;
		initialInput = null;
		liveState = null;
		PFLAP.allowGUIInterraction = true;
		closeStep.hide();
		help.hide();
	}

	public static void setMachineOutcome(boolean accepted) {
		if (!live) {
			throw new IllegalStateException("Begin stepping first.");
		} else {
			if (!completed) {
				completed = true;
				Step.accepted = accepted;
				if (accepted) {
					Notification.addNotification(machineAccepted);
				} else {
					Notification.addNotification(machineRejected);
				}
			}
		}
	}

	public static void setStack(String stackRepresentation) {
		stack = stackRepresentation;
	}

	protected static void stepForward() {
		if (live) {
			if (!(completed && visitedIndex == visited.size() - 1)) {
				liveState = Model.stepForward();
			}

			if (visitedIndex == visited.size() - 1) {
				if (completed) {
					return;
				}
				visited.add(liveState);
				visitedIndex += 1;
			} else {
				if (visitedIndex < visited.size() - 1) {
					visitedIndex += 1;
				}
			}
			remainingInput = initialInput.substring(visitedIndex);
		}
	}

	protected static void stepBackward() {
		if (live) {
			if (visitedIndex > 0) {
				visitedIndex--;
				remainingInput = initialInput.substring(visitedIndex);
				liveState = visited.get(visitedIndex);
				Model.stepBackward(liveState);
			}
		}
	}

	protected static void draw() {
		if (live) {
			p.image(staticGUI, 0, 0);
			p.textSize(20);
			p.fill(0, 0, 0);
			p.textAlign(PConstants.LEFT, PConstants.BOTTOM);
			p.text("Input: [" + remainingInput + "]", stepGUIPadding * 2, p.height - 50);
			switch (PFLAP.mode) {
				case DFA :
					break;
				case DPA :
					p.text("Stack: [" + stack + "]", stepGUIPadding * 2, p.height - 30);
					break;
				case MEALY :
				case MOORE :
					p.text("Output: [" + model.Model.getOutput() + "]", stepGUIPadding * 2, p.height - 30);
					break;
			}
			
			PApplet.view.highlightState(liveState, colorToRGB(255, 0, 255));// specify color
			if (completed && visitedIndex == visited.size() - 1) {
				if (accepted) {
					PApplet.view.highlightState(liveState, colorToRGB(0, 255, 0));
				} else {
					PApplet.view.highlightState(liveState, colorToRGB(255, 0, 0));
				}
			}
		}
	}

	/**
	 * Creates and caches step mode GUI border into a PGraphics object.
	 */
	private static void drawStaticGui() {
		int w = p.width;
		int h = p.height;
		staticGUI = p.createGraphics(w, h);
		staticGUI.beginDraw();
		staticGUI.stroke(0);
		staticGUI.strokeWeight(3);
		staticGUI.line(stepGUIPadding, stepGUIPadding, stepGUIPadding, h - stepGUIPadding);
		staticGUI.line(stepGUIPadding, stepGUIPadding, w - stepGUIPadding, stepGUIPadding);
		staticGUI.line(stepGUIPadding, h - stepGUIPadding, w - stepGUIPadding, h - stepGUIPadding);
		staticGUI.line(w - stepGUIPadding, stepGUIPadding, w - stepGUIPadding, h - stepGUIPadding);
		staticGUI.textSize(12);
		staticGUI.fill(0);
		staticGUI.textAlign(PConstants.LEFT, PConstants.TOP);
		staticGUI.text("Step Mode", stepGUIPadding + 5, stepGUIPadding + 5);
		staticGUI.strokeWeight(1);
		staticGUI.line(stepGUIPadding, stepGUIPadding + 20, w - stepGUIPadding, stepGUIPadding + 20);
		staticGUI.endDraw();
	}

	/**
	 * To be called when the app is resized by user to update button locations.
	 */
	protected static void stageResized() {
		drawStaticGui();
		closeStep.setPosition(p.width - stepGUIPadding - 30 - 1, stepGUIPadding + 2);
		help.setPosition(p.width - stepGUIPadding - 60 - 1, stepGUIPadding + 2);
	}

}