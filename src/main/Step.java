package main;

import static main.Consts.notificationData.machineRejected;
import static main.Consts.notificationData.machineAccepted;
import static main.PFLAP.p;
import static main.PFLAP.machine;

import static main.Consts.stepGUIPadding;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import p5.Notification;
import p5.State;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Call beginstep() when user clicks step-by-state.
 * <p>
 * Class is non-initialisable.
 * <p/>
 */
public class Step {

	protected static boolean live = false, completed = false, accepted;
	private static String initialInput, remainingInput, stack;
	private static State liveState;
	private static ArrayList<State> visited;
	private static int visitedIndex;
	private static Button closeStep, help;
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
		.setColorBackground(p.color(80, 0, 0))
		.setColorForeground(p.color(200, 0, 0))
		.setColorActive(p.color(255, 0, 0))
		.hide()
		;
		help = new Button(PFLAP.cp5, "helpSTEP");
		help.addListener(new ControlListener() {
			@Override
			public void controlEvent(ControlEvent onClick) {
				JOptionPane.showMessageDialog(p.frame, Consts.helpStep, "Help: Step Mode", JOptionPane.INFORMATION_MESSAGE);
			}
		})
		.setSize(30, 18)
		.setLabel(" ?")
		.setPosition(p.width - stepGUIPadding - 60 - 1, stepGUIPadding + 2)
		.setColorBackground(p.color(0, 0, 80))
		.setColorForeground(p.color(0, 0, 200))
		.setColorActive(p.color(0, 0, 255))
		.hide()
		;
		//@formatter:on
	}

	protected static void beginStep(String input) {
		if (live) {
			throw new IllegalStateException("End the live stepping sequence before starting another.");
		}
		closeStep.show();
		help.show();
		live = true;
		liveState = PFLAP.machine.getInitialState();
		initialInput = input;
		remainingInput = initialInput;
		PFLAP.machine.beginStep(input);
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

	public static void setLiveState(State s) {
		liveState = s;
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
			liveState = machine.stepForward();
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
				visitedIndex -= 1;
				remainingInput = initialInput.substring(visitedIndex);
				liveState = visited.get(visitedIndex);
				machine.stepBackward(visited.get(visitedIndex), remainingInput);
			}
		}
	}

	protected static void draw() {
		if (live) {
			p.image(staticGUI, 0, 0);
			p.textSize(20);
			p.fill(0, 0, 0);
			p.textAlign(PConstants.LEFT, PConstants.BOTTOM);
			p.text("Current State: " + liveState.getLabel(), stepGUIPadding * 2, p.height - 70);
			p.text("Input: [" + remainingInput + "]", stepGUIPadding * 2, p.height - 50);
			p.text("Stack: [" + stack + "]", stepGUIPadding * 2, p.height - 30);
			liveState.highLight(p.color(255, 0, 255)); // specify color
			if (completed && visitedIndex == visited.size() - 1) {
				if (accepted) {
					liveState.highLight(p.color(0, 255, 0));
				} else {
					liveState.highLight(p.color(255, 0, 0));
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