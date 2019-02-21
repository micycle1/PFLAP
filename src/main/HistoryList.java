package main;

import static main.PFLAP.p;

import java.awt.Color;

import commands.Command;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.DropdownList;

/**
 * Manages the controlP5 representation of the user's action history.
 * @author micycle1
 */
public final class HistoryList {

	private static final ControlP5 cP5;
	private static final DropdownList history;

	static {
		p.println(p.frameCount);
		cP5 = new ControlP5(p);
		cP5.hide();

		// @formatter:off
		history = cP5.addDropdownList("History")
				.setWidth(160)
//				.setColorBackground(new Color(50, 50, 50).getRGB())
//				.setColorForeground(new Color(150, 150, 150).getRGB())
				.setBarHeight(30)
				.setItemHeight(20)
				.setHeight(200)
//				.addCallback(new CallbackListener() {
//					@Override
//					public void controlEvent(CallbackEvent theEvent) {
//						if (theEvent.getAction() == 100) { // clicked
//							p.println("INIT");
//							HistoryHandler.movetoIndex((int) history.getValue() - 1);
//						}
//					}
//				})
				;
		// @formatter:on
	}

	/**
	 * Call when {@link HistoryHandler} executes a command, 
	 * or changes current history state.
	 */
	protected static void update() {
		history.clear();
		history.open();
		history.addItem("{Init}", null);
		for (Command c : HistoryHandler.export()) {
			history.addItem(c.description(), c);
		}
	}

	protected static boolean isMouseOver() {
		return cP5.isMouseOver();
	}

	public static void toggleVisible() {
//		cP5.setVisible(!cP5.isVisible());
//		history.open();
	}

}
