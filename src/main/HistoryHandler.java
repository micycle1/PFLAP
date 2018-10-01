package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import commands.Command;
import commands.addState;
import commands.addTransition;
import commands.moveState;
import p5.Notification;

import static main.PFLAP.p;

public final class HistoryHandler {

	private static int historyStateIndex = -1;
	private static ArrayList<Command> history = new ArrayList<>();
	private static Queue<Command> pendingExecute = new LinkedList<>();

	private HistoryHandler() {
		throw new AssertionError();
	}

	public static void resetAll() {
		if (history != null) {
			for (int i = history.size() - 1; i > -1; i--) {
				history.get(i).undo();
			}
			history.clear();
			pendingExecute.clear();
		}
		historyStateIndex = -1;
		HistoryList.update();
	}

	public static void buffer(Command c) {
		pendingExecute.add(c);
	}

	public static void executeBufferedCommands() {
		if (!(pendingExecute.isEmpty())) {
			InitUI.undo.setEnabled(true);
			if (historyStateIndex != history.size() - 1) {
				// if index not at end clear history from then
				for (int i = history.size() - 1; i > historyStateIndex; i--) {
					history.remove(i);
				}
			}

			while (!(pendingExecute.isEmpty())) {
				pendingExecute.peek().execute();
				history.add(pendingExecute.poll());
				historyStateIndex += 1;
			}
			HistoryList.update();
		}
	}

	public static void undo() {
		if (historyStateIndex > -1) {
			// can undo first command
			history.get(historyStateIndex).undo();
			historyStateIndex -= 1;
			InitUI.redo.setEnabled(true);
		} else {
			InitUI.undo.setEnabled(false);
		}
	}

	public static void redo() {
		if (historyStateIndex <= history.size() - 2) {
			// if not at end of history
			historyStateIndex += 1;
			history.get(historyStateIndex).execute();
			InitUI.undo.setEnabled(true);
		} else {
			InitUI.redo.setEnabled(false);
		}
	}

	protected static void movetoIndex(int index) {
		if (index > history.size() + 1 || index < -1) {
			return;
		}

		if (index > historyStateIndex) {
			while (index > historyStateIndex) {
				redo();
			}
		} else {
			if (index < historyStateIndex) {
				while (index < historyStateIndex) {
					undo();
				}
			}
		}
	}

	public static int getHistoryStateIndex() {
		return historyStateIndex;
	}

	protected static ArrayList<Command> export() {
		return history;
	}

	public static void saveHistory(String path) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
			ArrayList<Command> liveHistory = new ArrayList<>(
					history.subList(history.size() - historyStateIndex - 1, history.size()));
			out.writeObject(liveHistory);
			out.close();
		} catch (IOException e) {
			Notification.addNotification("Saving Failed", "Could not save the machine file.");
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadHistory(String path) {
		p.noLoop();
		try {
			resetAll();
			PFLAP.PApplet.reset();
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
			history.addAll((ArrayList<Command>) in.readObject());
			Queue<Command> executeLast = new LinkedList<>();
			for (Command c : history) {
				if (c instanceof moveState) {
					executeLast.add(c);
				} else {
					c.execute();
				}
				if (c instanceof addState) {
					PFLAP.nodes.get(PFLAP.nodes.size() - 1).initCP5();
				}
				if (c instanceof addTransition) {
					PFLAP.arrows.get(PFLAP.arrows.size() - 1).initCP5();
					PFLAP.arrows.get(PFLAP.arrows.size() - 1).update(true);
				}
			}
			while (!executeLast.isEmpty()) {
				executeLast.poll().execute();
			}
			historyStateIndex = history.size() - 1;
			HistoryList.update();
			in.close();
		} catch (InvalidClassException e) {
			Notification.addNotification("Loading Failed",
					"Could not load the machine file. (You are attempting to load a save from a different version of PFLAP).");
		} catch (IOException | ClassNotFoundException e) {
			Notification.addNotification("Loading Failed", "Could not load the machine file (IO Error).");
		}
		p.loop();
	}

	public static void debug() {
		System.out.println("HISTORY TRACE");
		for (int i = 0; i < history.size(); i++) {
			System.out.println((i == historyStateIndex ? "[X] " : "[ ] ") + history.get(i).description());
		}
		System.out.println("");
	}
}