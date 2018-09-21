package main;

import static main.Consts.notificationData.noInitialState;
import static main.PFLAP.p;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
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

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import commands.Batch;
import commands.changeMode;
import commands.setBackgroundColor;

import controlP5.ControlP5;
import machines.DPA;

import p5.Notification;
import p5.State;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PFont;

final class InitUI {

	private InitUI() {
		throw new AssertionError();
	}

	private static Frame getFrame() {
		Frame frame = null;
		try {
			Field f = ((PSurfaceAWT) p.getSurface()).getClass().getDeclaredField("frame");
			f.setAccessible(true);
			frame = (Frame) (f.get(((PSurfaceAWT) p.getSurface())));
		} catch (Exception e) {
		}
		return frame;
	}

	protected static final MenuItem undo = new MenuItem("Undo"), redo = new MenuItem("Redo");

	protected static void initMenuBar() {

		Frame f = getFrame();

		undo.setEnabled(false);
		redo.setEnabled(false);

		f.setMinimumSize(new Dimension(Consts.miniumWidth, Consts.minimumHeight));

		f.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent resized) {
				Notification.stageResized();
				Step.stageResized();
			}
		});

		final MenuBar menuBar = new MenuBar();

		final MenuItem fileMenuItem0, fileMenuItem1, fileMenuItem2;
		final MenuItem editMenuItem0, editMenuItem1, editMenuItem2;
		final MenuItem viewMenuItem0, viewMenuItem1, viewMenuItem2;
		final CheckboxMenuItem viewMenuCheckboxItem0, viewMenuCheckboxItem1;
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
		fileMenuItem0.setEnabled(false);
		fileMenuItem1 = new MenuItem("Save");
		fileMenuItem1.setEnabled(false);
		fileMenuItem2 = new MenuItem("Exit");

		// Edit Menu
		editMenuItem0 = new MenuItem("Select All States");
		editMenuItem1 = new MenuItem("Delete All States");
		editMenuItem2 = new MenuItem("Invert Selection");

		// View Menu
		viewMenuItem0 = new MenuItem("Save Stage As Image");
		viewMenuItem1 = new MenuItem("Reorder States");
		viewMenuItem1.setEnabled(false);
		viewMenuItem2 = new MenuItem("Machine Information");
		viewMenuCheckboxItem0 = new CheckboxMenuItem("Action Tracer", false);
		viewMenuCheckboxItem1 = new CheckboxMenuItem("History List GUI", false);

		// Machine Menu
		machineMenuItem0 = new MenuItem("DFA");
		machineMenuItem0.setEnabled(false); // DFA is initial mode, change if
											// load in mode at boot
		machineMenuItem1 = new MenuItem("DPA");
		machineMenuItem2 = new MenuItem("Mealy");
		machineMenuItem2.setEnabled(false);
		machineMenuItem3 = new MenuItem("Moore");
		machineMenuItem3.setEnabled(false);

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
		viewMenu.add(viewMenuItem2);
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
		final ItemListener tracerListener, historyGUIListener;

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
							String directory = fg.getDirectory() + fg.getFile() + ".png";
							p.saveFrame(directory);
							Notification.addNotification("Screenshot", "Image saved to " + directory);
						}

						break;
					case "Reorder States" : //TODO
						break;
					case "Machine Information" :
						String info = "Transitions: " + PFLAP.arrows.size() + "\r\n" + "States: " + PFLAP.nodes.size()
								+ "\r\n" + "Type: " + PFLAP.mode;
						JOptionPane.showMessageDialog(f, info, "Machine Info", JOptionPane.INFORMATION_MESSAGE);
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
						HistoryHandler.buffer(new changeMode(PFLAP.modes.DFA));
						machineMenuItem0.setEnabled(false);
						break;
					case "DPA" :
						HistoryHandler.buffer(new changeMode(PFLAP.modes.DPA));
						machineMenuItem1.setEnabled(false);
						break;
					case "Mealy" :
						HistoryHandler.buffer(new changeMode(PFLAP.modes.MEALY));
						machineMenuItem2.setEnabled(false);
						break;
					case "Moore" :
						HistoryHandler.buffer(new changeMode(PFLAP.modes.MOORE));
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
				if (PFLAP.machine.getInitialState() != null) {
					String userInput;
					do {
						userInput = JOptionPane.showInputDialog("Machine Input: ");
						if (userInput == null) {
							return;
						}
						if (userInput.contains(" ")) {
							Notification.addNotification("Invalid Input", "Input cannot contain ' ' characters.");
						}
					} while (userInput.contains(" "));
					switch (event.getActionCommand()) {
						case "Step By State" :
							Step.endStep();
							switch (PFLAP.mode) {
								case DFA :
									Step.beginStep(userInput);
									break;
								case DPA :
									String stackSymbol;
									do {
										stackSymbol = JOptionPane.showInputDialog("Initial Stack Symbol: ");
										if (stackSymbol == null) {
											return;
										}
										if (stackSymbol.length() == 1) {
											((DPA) PFLAP.machine).setInitialStackSymbol(
													Functions.testForLambda(stackSymbol.charAt(0)));
//											PFLAP.machine.run(userInput);
											Step.beginStep(userInput);
										} else {
											Notification.addNotification("Invalid Stack Symbol",
													"Initial Stack Symbol must be single character.");
										}
									} while (stackSymbol.length() != 1);
									break;
								case MEALY :
									break;
								case MOORE :
									break;
								default :
									break;
							}
							break;
						case "Fast Run" :
							switch (PFLAP.mode) {
								case DFA :
									PFLAP.machine.run(userInput);
									break;
								case DPA :
									String stackSymbol;
									do {
										stackSymbol = JOptionPane.showInputDialog("Initial Stack Symbol: ");
										if (stackSymbol == null) {
											return;
										}
										if (stackSymbol.length() == 1) {
											((DPA) PFLAP.machine).setInitialStackSymbol(
													Functions.testForLambda(stackSymbol.charAt(0)));
											PFLAP.machine.run(userInput);
										} else {
											Notification.addNotification("Invalid Stack Symbol",
													"Initial Stack Symbol must be single character.");
										}
									} while (stackSymbol.length() != 1);
									break;
								case MEALY :
									break;
								case MOORE :
									break;
								default :
									break;
							}
							break;
						default :
							System.err.println("Unhandled Menuitem.");
							break;
					}
				} else {
					Notification.addNotification(noInitialState);
				}

			}
		};

		helpMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "About PFLAP" :
						JOptionPane.showMessageDialog(f, Consts.about, "About", JOptionPane.INFORMATION_MESSAGE);
						break;
					case "Help" :
						JOptionPane.showMessageDialog(f, Consts.helpPFLAP, "PFLAP: Help",
								JOptionPane.INFORMATION_MESSAGE);
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
				PFLAP.PApplet.trace.setVisible(viewMenuCheckboxItem0.getState());
			}
		};
		viewMenuCheckboxItem0.addItemListener(tracerListener);

		historyGUIListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				HistoryList.toggleVisible();
			}
		};
		viewMenuCheckboxItem1.addItemListener(historyGUIListener);

		fileMenu.addActionListener(fileMenuListener);
		editMenu.addActionListener(editMenuListener);
		viewMenu.addActionListener(viewMenuListener);
		machineMenu.addActionListener(machineMenuListener);
		inputMenu.addActionListener(inputMenuListener);
		helpMenu.addActionListener(helpMenuListener);
		defineColours.addActionListener(defineColoursListener);
		viewMenu.add(viewMenuCheckboxItem0);
		viewMenu.add(viewMenuCheckboxItem1);

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

	protected static void initCp5() {
		PFont traceFont = p.createFont("Comfortaa Regular", 12, true);
		PFLAP.cp5 = new ControlP5(p);
		// @formatter:off
		PFLAP.PApplet.trace = PFLAP.cp5.addTextarea("Trace")
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
		 PFLAP.cp5.addConsole(PFLAP.PApplet.trace);
	}
}