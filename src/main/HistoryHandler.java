package main;

//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParser;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.Writer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import commands.Command;

public final class HistoryHandler {

	private static int historyStateIndex = -1;
	private static ArrayList<Command> history = new ArrayList<>();
	private static Queue<Command> pendingExecute = new LinkedList<>();
	
	static {
		loadHistory();
	}

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
		}
	}

	public static void undo() {
		if (historyStateIndex > -1) {
			// can undo first command
			history.get(historyStateIndex).undo();
			historyStateIndex -= 1;
			InitUI.redo.setEnabled(true);
		}
		else {
			InitUI.undo.setEnabled(false);
		}
	}

	public static void redo() {
		if (historyStateIndex <= history.size() - 2) {
			// if not at end of history
			historyStateIndex += 1;
			history.get(historyStateIndex).execute();
			InitUI.undo.setEnabled(true);
		}
		else {
			InitUI.redo.setEnabled(false);
		}
	}
	
	public static int getHistoryStateIndex() {
		return historyStateIndex;
	}

	public static void saveHistory() {
//		Gson gson = new Gson();
//		System.out.println("saved");
//		try (Writer writer = new OutputStreamWriter( new FileOutputStream("history.json"))) {
//			gson.newBuilder().create();
//			gson.toJson(history, writer);
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public static void loadHistory() {
//		resetAll();
//		Gson gson = new Gson();
//		try (BufferedReader reader = new BufferedReader(new FileReader("history.json"))) {
////			String loadHistory = reader.readLine();
////			reader.close();
//			
//			 StringBuilder sb = new StringBuilder();
//			 String line;
//			 while ((line = reader.readLine()) != null) {
//			     sb.append(line);
//			 }
//			 String in = sb.toString();
//
//			System.out.println(in);
//
//			TypeToken<ArrayList<Command>> type = new TypeToken<ArrayList<Command>>() {
//			};
//			
//			java.lang.reflect.Type type1 = new TypeToken<ArrayList<Integer>>() {}.getType();
//			
////			System.out.println(jp.parse(loadHistory).getAsJsonObject().toString());
//			
////			history = gson.fromJson("[{\"pObject\":{\"x\":176,\"y\":162}},{\"pObject\":{\"x\":237,\"y\":260}}]", type.getType());
////			
////			String s = "[1,2]";
////			System.out.println(s);
////			System.out.println(s.equals('"'+in+'"'));
////			test = gson.fromJson("[1,3]", type1);
//			
//			//execute history
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		// if (history == null) {
//		// history = new ArrayList<>();
//		// System.out.print("ad");
//		// }
//		// else {
//		// history.forEach(c -> c.execute());
//		// }

	}

	public static void debug() {
		System.out.println("HISTORY TRACE");
		for (int i = 0; i < history.size(); i++) {
			System.out.println((i == historyStateIndex ? "[X] " : "[ ] ") + history.get(i).description());
		}
		System.out.println("");
	}

}