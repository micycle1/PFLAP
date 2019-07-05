package main;

import static main.Functions.colorToRGB;

import commands.Command;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ListBox;

/**
 * Manages the controlP5 representation of the user's action history.
 * @author micycle1
 */
public final class HistoryList {

	private ListBox history;

	public HistoryList() {
		// @formatter:off
		history = PFLAP.cp5.addListBox("History")
				.setWidth(240)
				.setColorBackground(colorToRGB(50, 50, 50))
				.setColorForeground(colorToRGB(50, 50, 50))
				.setBarHeight(30)
				.setItemHeight(20)
				.setHeight(200)
				.hide()
				.addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						if (theEvent.getAction() == 100 && PFLAP.allowGUIInterraction) { // clicked
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
		history.addItem("{Init}", null);
		for (Command c : HistoryHandler.export()) {
			history.addItem(c.description(), c);
		}
	}

	protected boolean isMouseOver() {
		return history.isMouseOver();
	}

	public void toggleVisible() {
		history.setVisible(!history.isVisible());
		history.open();
	}

}
