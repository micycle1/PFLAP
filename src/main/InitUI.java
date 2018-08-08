package main;

import static main.Consts.notificationHeight;
import static main.Consts.notificationWidth;
import static main.Consts.notificationData.noInitialState;
import static main.PFLAP.p;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import commands.Batch;
import commands.setBackgroundColor;

import controlP5.ControlP5;

import machines.DPA;
import main.PFLAP.modes;
import p5.Notification;
import p5.State;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

final class InitUI {

	public static final MenuItem undo = new MenuItem("Undo"), redo = new MenuItem("Redo");

	public static void initMenuBar(Frame f) {

		undo.setEnabled(false);
		redo.setEnabled(false);

		f.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				Notification.positionTarget = new PVector(p.width - notificationWidth, p.height - notificationHeight);
			}
		});

		final MenuBar menuBar = new MenuBar();

		final MenuItem fileMenuItem0, fileMenuItem1, fileMenuItem2;
		final MenuItem editMenuItem0, editMenuItem1, editMenuItem2;
		final MenuItem viewMenuItem0, viewMenuItem1;
		final CheckboxMenuItem viewMenuCheckboxItem0;
		final MenuItem machineMenuItem0, machineMenuItem1, machineMenuItem2, machineMenuItem3;
		final MenuItem inputMenuItem0, inputMenuItem1;
		final MenuItem helpMenuItem0, helpMenuItem1;
		final MenuItem defineColoursItem0, defineColoursItem1, defineColoursItem2, defineColoursItem3;

		// Top-Level Menus
		final Menu fileMenu = new Menu("File");
		final Menu editMenu = new Menu("Edit");
		final Menu viewMenu = new Menu("View");
		final Menu machineMenu = new Menu("Machine");
		final Menu inputMenu = new Menu("Input");
		final Menu helpMenu = new Menu("Help");

		// Second-Level Menus
		final Menu defineColours = new Menu("Redefine Colours");
		defineColoursItem0 = new MenuItem("State Colour");
		defineColoursItem1 = new MenuItem("State Selected Colour");
		defineColoursItem2 = new MenuItem("Transition Arrow Colour");
		defineColoursItem3 = new MenuItem("Background Colour");
		defineColours.add(defineColoursItem0);
		defineColours.add(defineColoursItem1);
		defineColours.add(defineColoursItem2);
		defineColours.add(defineColoursItem3);

		// File Menu
		fileMenuItem0 = new MenuItem("Open");
		fileMenuItem1 = new MenuItem("Save");
		fileMenuItem2 = new MenuItem("Exit");

		// Edit Menu
		editMenuItem0 = new MenuItem("Select All States");
		editMenuItem1 = new MenuItem("Delete All States");
		editMenuItem2 = new MenuItem("Invert Selection");

		// View Menu
		viewMenuItem0 = new MenuItem("Save Stage As Image");
		viewMenuItem1 = new MenuItem("Reorder States");
		viewMenuCheckboxItem0 = new CheckboxMenuItem("Action Tracer", false);
		// TODO view machine information (# states)

		// Machine Menu
		machineMenuItem0 = new MenuItem("DFA");
		machineMenuItem1 = new MenuItem("DPA");
		machineMenuItem2 = new MenuItem("Mealy");
		machineMenuItem3 = new MenuItem("Moore");

		// Input Menu
		inputMenuItem0 = new MenuItem("Step By State");
		inputMenuItem1 = new MenuItem("Fast Run");

		// Help Menu
		helpMenuItem0 = new MenuItem("Help");
		helpMenuItem1 = new MenuItem("About PFLAP");

		// Add file items to file menu
		fileMenu.add(fileMenuItem0);
		fileMenu.add(fileMenuItem1);
		fileMenu.add(fileMenuItem2);

		// Add edit items to edit menu
		editMenu.add(undo);
		editMenu.add(redo);
		editMenu.add(editMenuItem0);
		editMenu.add(editMenuItem1);
		editMenu.add(editMenuItem2);

		// Add view items to view menu
		viewMenu.add(viewMenuItem0);
		viewMenu.add(viewMenuItem1);
		viewMenu.add(defineColours);

		// Add input items to machine menu
		machineMenu.add(machineMenuItem0);
		machineMenu.add(machineMenuItem1);
		machineMenu.add(machineMenuItem2);
		machineMenu.add(machineMenuItem3);

		// Add input items to input menu
		inputMenu.add(inputMenuItem0);
		inputMenu.add(inputMenuItem1);

		// Add help items to help menu
		helpMenu.add(helpMenuItem0);
		helpMenu.add(helpMenuItem1);

		// Menu Action Listeners
		final ActionListener fileMenuListener, editMenuListener, viewMenuListener, machineMenuListener,
				inputMenuListener, helpMenuListener, defineColoursListener;
		final ItemListener tracerListener;

		fileMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Exit" :
						p.exit();
						break;
					case "Open" :
						FileDialog fg = new FileDialog(f, "Open a file", FileDialog.LOAD);
						fg.setVisible(true);
						String file = fg.getDirectory() + fg.getFile();
						PApplet.print(file);
						break;
					case "Save" : // save info rather than image
						break;
					default :
						System.err.println("Unhandled Menuitem.");
						break;
				}
			}
		};

		editMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Select All States" :
						PFLAP.selected.addAll(PFLAP.nodes);
						PFLAP.nodes.forEach(s -> s.select());
						break;
					case "Delete All States" :
						HistoryHandler.buffer(new Batch(Batch.createDeleteBatch(PFLAP.nodes)));
						break;
					case "Invert Selection" :
						if (PFLAP.selected.size() > 0) { // only if >=1 selected
							ArrayList<State> temp = new ArrayList<>(PFLAP.nodes);
							temp.removeAll(PFLAP.selected);
							PFLAP.selected.forEach(s -> s.deselect());
							PFLAP.selected.clear();
							PFLAP.selected.addAll(temp);
							PFLAP.selected.forEach(s -> s.select());
						}
						break;
					case "Undo" :
						HistoryHandler.undo();
						redo.setEnabled(true);
						if (HistoryHandler.getHistoryStateIndex() < -1) {
							undo.setEnabled(false);
						}
						break;
					case "Redo" :
						HistoryHandler.redo();
						undo.setEnabled(true);
						break;
					default :
						System.err.println("Unhandled Menuitem.");
						break;
				}
			}
		};

		viewMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Save Stage As Image" :
						FileDialog fg = new FileDialog(f, "Save", FileDialog.SAVE);
						fg.setDirectory(System.getProperty("user.home") + "\\Desktop");
						fg.setTitle("Save Frame");
						fg.setVisible(true);
						if (fg.getFile() != null) {
							String file = fg.getDirectory() + fg.getFile() + ".png";
							p.saveFrame(file);
						}
						PGraphics screenshot = p.createGraphics(p.width, p.height); // todo
																					// transparent
																					// screenshot
						break;
					case "Reorder States" :

						// TODO into grid?
						break;
					default :
						System.err.println("Unhandled Menuitem.");
						break;
				}
			}
		};
		
		machineMenuListener = new ActionListener() {
			private final void enableAll() {
				machineMenuItem0.setEnabled(true);
				machineMenuItem1.setEnabled(true);
				machineMenuItem2.setEnabled(true);
				machineMenuItem3.setEnabled(true);
			}
			@Override
			public void actionPerformed(ActionEvent event) {
				enableAll();
				switch (event.getActionCommand()) {
					case "DFA" :
						PFLAP.changeMode(PFLAP.modes.DFA);
						machineMenuItem0.setEnabled(false);
						break;
					case "DPA" :
						PFLAP.changeMode(PFLAP.modes.DPA);
						machineMenuItem1.setEnabled(false);
						break;
					case "Mealy" :
						machineMenuItem2.setEnabled(false);
						break;
					case "Moore" :
						machineMenuItem3.setEnabled(false);
						break;
					default :
						break;
				}
			}
		};

		inputMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String userInput;
				switch (event.getActionCommand()) {
					case "Step By State" :
						switch (PFLAP.mode) {
							case DFA :
								if (PFLAP.machine.getInitialState() != null) {
									userInput = JOptionPane.showInputDialog("DFA Input: ");
									PFLAP.machine.run(userInput);
									// /println();
								} else {
									Notification.addNotification(noInitialState);
									System.err.println("No Initial State Defined");
								}
								break;

							case DPA :
								if (PFLAP.machine.getInitialState() != null) {
									((DPA) PFLAP.machine).setInitialStackSymbol(
											JOptionPane.showInputDialog("Initial Stack Symbol: ").charAt(0));
									userInput = JOptionPane.showInputDialog("DPA Input: ");
									PApplet.println(PFLAP.machine.run(userInput));
								} else {
									Notification.addNotification(noInitialState);
									System.err.println("No Initial State Defined");
								}
								break;
						}
						break;
					case "Fast Run" :
						// TODO implement
						break;
					default :
						System.err.println("Unhandled Menuitem.");
						break;
				}
			}
		};

		helpMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "About PFLAP" :
						JOptionPane.showMessageDialog(f, Consts.about);
						break;
					case "Help" :
						JOptionPane.showMessageDialog(f, Consts.help);
						break;
					default :
						System.err.println("Unhandled Menuitem.");
						break;
				}
			}
		};

		defineColoursListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Color temp;
				switch (event.getActionCommand()) {
					case "State Colour" :
						temp = JColorChooser.showDialog(null, "Choose State Colour", PFLAP.stateColour);
						if (temp != null) {
							PFLAP.stateColour = temp;
						}
						break;
					case "State Selected Colour" :
						temp = JColorChooser.showDialog(null, "Choose State Selected Color", PFLAP.stateSelectedColour);
						if (temp != null) {
							PFLAP.stateSelectedColour = temp;
						}
						break;
					case "Transition Arrow Colour" :
						temp = JColorChooser.showDialog(null, "Choose Transition Arrow Colour", PFLAP.transitionColour);
						if (temp != null) {
							PFLAP.transitionColour = temp;
						}
						break;
					case "Background Colour" :
						temp = JColorChooser.showDialog(null, "Choose Background Colour", PFLAP.bgColour);
						if (temp != null) {
							HistoryHandler.buffer(new setBackgroundColor(temp));
						}
						break;
					default :
						System.err.println("Unhandled Menuitem.");
						break;
				}
			}
		};

		tracerListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				PFLAP.processing.trace.setVisible(viewMenuCheckboxItem0.getState());
			}
		};

		fileMenu.addActionListener(fileMenuListener);
		editMenu.addActionListener(editMenuListener);
		viewMenu.addActionListener(viewMenuListener);
		machineMenu.addActionListener(machineMenuListener);
		inputMenu.addActionListener(inputMenuListener);
		helpMenu.addActionListener(helpMenuListener);
		defineColours.addActionListener(defineColoursListener);

		viewMenuCheckboxItem0.addItemListener(tracerListener);
		viewMenu.add(viewMenuCheckboxItem0);

		// Adding menus to the menu bar
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(machineMenu);
		menuBar.add(inputMenu);
		menuBar.add(helpMenu);

		// Adding menubar to frame
		f.setMenuBar(menuBar);
	}

	public static void initCp5() {
		PFont traceFont = p.createFont("Comfortaa Regular", 12, true);
		PFLAP.cp5 = new ControlP5(p);
		PFLAP.processing.trace = PFLAP.cp5.addTextarea("Trace")

		// @formatter:off
			.setVisible(false)
			.setPosition(10, 670)
			.setSize(200, 100)
			.setFont(traceFont)
			.setLineHeight(14)
			.setColor(p.color(255))
			.setColorBackground(p.color(0, 200))
			.setMoveable(false)
			;
			// @formatter:on
		// cp5.addConsole(trace); TODO uncomment
	}
}
