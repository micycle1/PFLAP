package main;

import static main.Functions.color;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ListBox;
import main.PFLAP.PApplet;

/**
 * Manages the controlP5 representation of the user's action history.
 * @author micycle1
 */
public final class HistoryList {

	// private static final ControlP5 cP5;
	// private static final DropdownList history;

	private PApplet p;
	private ControlP5 cp5;
	private ListBox history;

	public HistoryList(PApplet p) {
		cp5 = new ControlP5(p);
		cp5.hide();
		// @formatter:off
		history = cp5.addListBox("HistoryList")
				.setWidth(160)
				.setColorBackground(color(50, 50, 50))
				.setColorForeground(color(50, 50, 50))
				.setBarHeight(30)
				.setItemHeight(20)
				.setHeight(200)
				.addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						if (theEvent.getAction() == 100) { // clicked
							HistoryHandler.movetoIndex((int) history.getValue() - 1);
						}
					}
				});
		// @formatter:on
	}

	/**
	 * Call when {@link HistoryHandler} executes a command, 
	 * or changes current history state.
	 */
	protected void update() {
		history.clear();
		history.open();
		// history.addItem("{Init}", null);
		// for (Command c : HistoryHandler.export()) {
		// history.addItem(c.description(), c);
		// }
		history.addItem("asds", null);
	}

	protected boolean isMouseOver() {
		return cp5.isMouseOver();
	}

	public void toggleVisible() {
		cp5.setVisible(!cp5.isVisible());
		history.open();
	}

}
