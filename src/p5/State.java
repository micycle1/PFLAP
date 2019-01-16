package p5;

import static main.Consts.initialNodeIndicatorSize;
import static main.Consts.stateRadius;
import static main.PFLAP.cp5Font;
import static main.PFLAP.machine;
import static main.PFLAP.p;
import static main.PFLAP.stateColour;
import static main.PFLAP.stateSelectedColour;

import java.io.Serializable;
import java.util.ArrayList;

import commands.addTransition;
import commands.setInitialState;
import commands.toggleAccepting;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ListBox;
import controlP5.Slider;
import controlP5.Textfield;
import main.Consts;
import main.Functions;
import main.HistoryHandler;
import main.PFLAP;
import main.PFLAP.modes;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class State implements Serializable {

	private String label, moorePush = "";
	private transient ControlP5 cp5, resizeGUI;
	private transient ListBox stateOptions;
	private transient Slider sizeSlider;
	private transient ControlListener listener, sizeSliderListener;
	private transient Textfield moorePushInput;
	private PVector position, selectedPosition;
	private transient ArrayList<AbstractArrow> arrowHeads = new ArrayList<>();
	private transient ArrayList<AbstractArrow> arrowTails = new ArrayList<>();
	private boolean initial = false;
	private transient boolean accepting = false, selected = false, highlighted = false;
	private int radius = stateRadius, highlightColor;
	private final int liveID;
	private static State renameState;
	private static final Textfield rename;
	private static final PGraphics initialIndicator;

	static { // only one rename box for class
		// @formatter:off
		rename = PFLAP.cp5.addTextfield("Rename State");
		rename.hide()
		.setSize(45, 20)
		.setFocus(false)
		.addCallback(new CallbackListener() {
			@Override
			public void controlEvent(CallbackEvent input) {
				if (input.getAction() == 100) {
				renameState.label = rename.getStringValue();
				rename.setFocus(false).hide();
				rename.clear();
				renameState = null;
				}
			}
		});
		// @formatter:on

		initialIndicator = p.createGraphics(50, 50);
		initialIndicator.beginDraw();
		initialIndicator.fill(255, 0, 0);
		initialIndicator.noStroke();
		initialIndicator.triangle(0, 0, 0, initialNodeIndicatorSize * 2, initialNodeIndicatorSize,
				initialNodeIndicatorSize);
		initialIndicator.endDraw();
	}

	public State(PVector XY, int liveID) {
		label = "q" + liveID;
		this.liveID = liveID;
		position = XY;
		initCP5();
//		arrowHeads = new ArrayList<>();
//		arrowTails = new ArrayList<>();
	}

	public void initCP5() {
		cp5 = new ControlP5(p);
		cp5.setFont(cp5Font);
		resizeGUI = new ControlP5(p);
		resizeGUI.setFont(cp5Font);

		// @formatter:off
		sizeSlider = resizeGUI.addSlider("Size Slider")
				.setWidth(100)
				.setHeight(15)
				.setValue(stateRadius)
				.setMin(25)
				.setMax(150)
				.setSliderMode(Slider.FLEXIBLE)
				.hide()
				;
		// @formatter:on
		sizeSliderListener = new ControlListener() {
			@Override
			public void controlEvent(ControlEvent radiusChange) {
				radius = (int) sizeSlider.getValue();
				arrowHeads.forEach(a -> a.update());
				arrowTails.forEach(a -> a.update());
			}
		};
		sizeSlider.addListener(sizeSliderListener);

		listener = new ControlListener() {
			@Override
			public void controlEvent(ControlEvent optionSelected) {
				stateOptions.hide();
				switch ((int) optionSelected.getValue()) {
					case 0 : // Add Self-Transition
						PFLAP.allowGUIInterraction = false;
//						HistoryHandler.buffer(new addTransition(State.this, State.this)); // todo 
						break;
					case 1 : // Set As Initial
						HistoryHandler.buffer(new setInitialState(State.this));
						break;
					case 2 : // Toggle Accepting
						HistoryHandler.buffer(new toggleAccepting(State.this));
						break;
					case 3 : // Relabel
						renameState = State.this;
						rename.setPosition(position.x - rename.getWidth() / 2, position.y + 30);
						rename.setFocus(true);
						rename.show();
						break;
					case 4 : // Resize
						sizeSlider.bringToFront();
						resizeGUI.show();
						sizeSlider.show();
						break;
					case 5 : // Delete
						HistoryHandler.buffer(new commands.deleteState(State.this));
						break;
					case 6 : // Moore Input
						moorePushInput.setFocus(true).show();
						PFLAP.allowGUIInterraction = false;
						break;
				}
			}
		};
		stateOptions = cp5.addListBox("          Options");
		// @formatter:off
		stateOptions.addItems(new String[]{"Add Self-Transition", "Set As Inititial", "Toggle Accepting", "Relabel", "Resize", "Delete"})
			.setWidth(140)
			.setBarHeight(35)
			.setColorLabel(Functions.invertColor(PFLAP.stateColour))
			.setItemHeight(25)
			.setColorValue(Functions.invertColor(PFLAP.stateColour))
			.setColorBackground(PFLAP.stateColour.getRGB())
			.setColorActive(PFLAP.stateColour.getRGB())
			.setColorForeground(Functions.darkenColor(PFLAP.stateColour, 0.33f)) // Mouseover
			.addListener(listener)
			.hide();
		// @formatter:on
		if (PFLAP.mode == modes.MOORE) {
			stateOptions.addItems(new String[]{"Set State Output"});
			moorePushInput = cp5.addTextfield("Set State Output").setSize(45, 20).keepFocus(true)
					.addCallback(new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent input) {
							if (input.getAction() == 100) {
								if (moorePushInput.getStringValue().length() > 0) {
									moorePush = Functions.testForLambda(moorePushInput.getStringValue());
									PFLAP.allowGUIInterraction = true;
									moorePushInput.setFocus(false).hide();
								} else {
									Notification.addNotification(Consts.notificationData.symbolInvalid);
								}
							}
						}
					});
			if (moorePush == "") {
				moorePushInput.setFocus(true).show();
				PFLAP.allowGUIInterraction = false;
			} else {
				moorePushInput.setFocus(false).hide();
				PFLAP.allowGUIInterraction = true;
			}

		}
		cp5.setPosition((int) this.position.x + 10, (int) this.position.y + 10);
		resizeGUI.setPosition((int) (position.x) - 50, (int) (position.y));
	}

	public void kill() {
		for (AbstractArrow a : arrowHeads) {
//			machine.removeTransition(a); // todo
		}
		machine.deleteNode(this);
		if (initial) {
			machine.setInitialState(null);
		}
		arrowHeads.forEach(a -> a.parentKill());
		arrowTails.forEach(a -> a.parentKill());
	}
	
	public final void disposeUI() {
		cp5.dispose();
	}

	protected void childKill(AbstractArrow a) {
		// Transition arrows call this.
		if (arrowHeads.contains(a)) {
			arrowHeads.remove(a);
		} else {
			arrowTails.remove(a);
		}
	}

	public void draw() {

		if (initial) {
			p.image(initialIndicator, position.x - radius / 2 - 14, position.y - initialNodeIndicatorSize);
		}
		if (!selected) {
			if (highlighted) {
				p.fill(highlightColor);
			} else {
				p.fill(stateColour.getRGB());
			}
			p.ellipse(position.x, position.y, radius, radius);
			p.fill(0);
		} else {
			p.fill(stateSelectedColour.getRGB(), 200);
			p.ellipse(position.x, position.y, radius, radius);
			p.fill(255);
		}
		if (accepting) {
			p.noFill();
			p.strokeWeight(2);
			p.ellipse(position.x, position.y, radius - 9, radius - 9);
			p.strokeWeight(3);
		}
		p.textSize(PApplet.max(14, (PApplet.sqrt(radius) * 10) - 50));
		p.text(label, position.x, position.y);
		if (PFLAP.mode == modes.MOORE) {
			p.fill(0);
			p.textAlign(PApplet.LEFT);
			p.textSize(18);
			p.text("[" + moorePush + "]", position.x + 5 + radius / 2, position.y + 5 - radius / 2);
			p.textAlign(PApplet.CENTER, PApplet.CENTER);
		}
		highlighted = false;
	}

	public void setPosition(PVector position) {
		this.position = position;
		cp5.setPosition((int) this.position.x + 10, (int) this.position.y + 10);
		resizeGUI.setPosition((int) (position.x) - 50, (int) (position.y));
		arrowHeads.forEach(a -> a.update());
		arrowTails.forEach(a -> a.update());
	}

	public void select() {
		stateOptions.open();
		selected = true;
		selectedPosition = position;
	}

	public void deselect() {
		hideUI();
		resizeGUI.hide();
		selected = false;
		selectedPosition = null;
	}
	
	public void load() {
		arrowHeads = new ArrayList<>();
		arrowTails = new ArrayList<>();
	}

	public void toggleAccepting() {
		accepting = !accepting;
	}

	public void highLight(int highlightColor) {
		this.highlightColor = highlightColor;
		highlighted = true;
		deselect();
	}

	public ArrayList<AbstractArrow> getConnectedArrows() {
		ArrayList<AbstractArrow> all = new ArrayList<>();
		all.addAll(arrowHeads);
		all.addAll(arrowTails);
		return all;
	}

	public ArrayList<AbstractArrow> getOutgoingArrows() {
		return arrowTails;
	}

	public PVector getPosition() {
		return position;
	}

	public String getMoorePush() {
		return moorePush;
	}

	public void setAsInitial() {
		initial = true;
	}

	public void deInitial() {
		initial = false;
	}

	public PVector getSelectedPosition() {
		return selectedPosition;
	}

	public String getLabel() {
		return label;
	}

	public int getRadius() {
		return radius;
	}

	public int getID() {
		return liveID;
	}

	public void addArrowHead(AbstractArrow a) {
		arrowHeads.add(a);
	}

	public void addArrowTail(AbstractArrow a) {
		arrowTails.add(a);
	}

	private void hideUI() {
		stateOptions.hide();
	}

	public void showUI() {
		stateOptions.show();
	}

	public boolean isAccepting() {
		return accepting;
	}
	
	public boolean isMouseOver() {
		return (cp5.isMouseOver() && cp5.isVisible()) || (resizeGUI.isMouseOver() && sizeSlider.isVisible());
	}
}