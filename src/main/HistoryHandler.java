package main;

import static main.PFLAP.p;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import commands.Command;

import main.PFLAP.PApplet;

import p5.Notification;

public final class HistoryHandler {

	private static int historyStateIndex = -1;
	private static final ArrayList<Command> history = new ArrayList<>();
	private static final Queue<Command> pendingExecute = new LinkedList<>();

	private HistoryHandler() {
		throw new AssertionError();
	}

	protected static void resetAll() {
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

	protected static void executeBufferedCommands() {
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

	protected static void undo() {
		if (historyStateIndex > -1) {
			// can undo first command
			history.get(historyStateIndex).undo();
			historyStateIndex -= 1;
			InitUI.redo.setEnabled(true);
		} else {
			InitUI.undo.setEnabled(false);
		}
	}

	protected static void redo() {
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

	protected static int getHistoryStateIndex() {
		return historyStateIndex;
	}

	protected static ArrayList<Command> export() {
		return history;
	}

	protected static void saveHistory(String path) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
			ArrayList<Command> liveHistory = new ArrayList<>(
					history.subList(history.size() - historyStateIndex - 1, history.size()));
			Object[] objects = new Object[]{liveHistory, PApplet.view.save()};
			out.writeObject(objects);
			out.close();
		} catch (IOException e) {
			Notification.addNotification("Saving Failed", "Could not save the machine file.");
		}
	}

	@SuppressWarnings("unchecked")
	protected static void loadHistory(String path) {
		try {
			resetAll();
			// PFLAP.reset();
			p.noLoop();
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
			Object[] data = (Object[]) in.readObject();
			PApplet.view.load(data[1]);
			history.addAll((ArrayList<Command>) data[0]);
			history.forEach(c -> c.execute());
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
}