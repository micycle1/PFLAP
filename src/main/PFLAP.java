package main;

import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.File;
import java.awt.Frame;
import java.awt.Menu;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import controlP5.ControlP5;

import machines.DFA;
import machines.DPA;

import p5.Arrow;
import p5.Notification;
import p5.SelectionBox;
import p5.State;

import processing.awt.*;

import static main.Consts.notificationData.noInitialState;
import static main.Functions.withinRange;
import static main.Functions.withinRegion;

//@formatter:off
/**
 * State self bezier-arrows. 
 * Right-click menu on arrows 
 * DPA fully integrated with states and transitions.
 * delete transitions
 * modify transitions
 * info about machine (#states, etc)
 * undo/redo
 * save/load : stateXY; encoding of transitions per machine
 * blur behind notification
 * mutli selection creating transtion makes multiple transitions
 */
//@formatter:on

public class PFLAP extends PApplet {

	public static HashSet<Character> keysDown = new HashSet<Character>();
	public static HashSet<Integer> mouseDown = new HashSet<Integer>();

	public static ArrayList<Arrow> arrows = new ArrayList<>();
	public static ArrayList<State> nodes = new ArrayList<>();
	public static HashSet<State> selected = new HashSet<>();

	public static ControlP5 cp5;

	private static State mouseOverState, arrowTailState, arrowHeadState, dragState;
	private static Arrow drawingArrow;
	private static SelectionBox selectionBox = null;

	private PVector mouseClickXY, mouseReleasedXY, mouseCoords;

	public static boolean allowGUIInterraction = true;

	public static PApplet p;

	public static enum modes {
		DFA, DPA;
	}

	public static PImage notificationBG;
	public p5.Notification temp;

	public static modes mode = modes.DPA; // TODO change for test

	public static void main(String[] args) {
		PApplet.main(PFLAP.class);
	}

	@Override
	public void setup() {
		initCp5();
		initMenuBar();
		// notifications.get
		p = this;

		surface.setTitle(Consts.title);
		surface.setLocation(displayWidth / 2 - width / 2, displayHeight / 2 - height / 2);
		surface.setResizable(false);
		surface.setResizable(true);
		frameRate(60);
		strokeJoin(MITER);
		strokeWeight(3);
		stroke(0);
		textSize(Consts.stateFontSize);
		textAlign(CENTER, CENTER);
		rectMode(CORNER);
		ellipseMode(CENTER);
		cursor(ARROW);
	}

	@Override
	public void settings() {
		size(Consts.WIDTH, Consts.HEIGHT);
		smooth(8);
	}

	@Override
	public void draw() {
		mouseCoords = new PVector(constrain(mouseX, 0, width), constrain(mouseY, 0, height));
		background(255);

		if (drawingArrow != null) {
			stroke(0, 0, 0, 80);
			strokeWeight(2);
			drawingArrow.setHeadXY(mouseCoords);
			drawingArrow.draw();
		}
		if (selectionBox != null) {
			selectionBox.setEndPosition(mouseCoords);
			selectionBox.draw();
		}

		stroke(0);
		fill(0);
		strokeWeight(2);
		textSize(18);
		arrows.forEach(a -> a.draw());
		
		textAlign(CENTER, CENTER);
		textSize(16);
		nodes.forEach(s -> s.draw());
		
		if (dragState != null) {
			dragState.setPosition(mouseCoords);
			dragState.draw();
		}
		
		Notification.run();
	}

	public void initMenuBar() {
		Frame f = getFrame();
		MenuBar menuBar = new MenuBar();

		MenuItem fileMenuItem0, fileMenuItem1, fileMenuItem2;
		MenuItem editMenuItem0, editMenuItem1, editMenuItem2;
		MenuItem viewMenuItem0, viewMenuItem1;
		MenuItem inputMenuItem0, inputMenuItem1;
		MenuItem helpMenuItem0, helpMenuItem1;

		// Top-Level Menus
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");
		Menu viewMenu = new Menu("View");
		Menu inputMenu = new Menu("Input");
		Menu helpMenu = new Menu("Help");

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
		editMenu.add(editMenuItem0);
		editMenu.add(editMenuItem1);
		editMenu.add(editMenuItem2);

		// Add view items to view menu
		viewMenu.add(viewMenuItem0);
		viewMenu.add(viewMenuItem1);

		// Add input items to input menu
		inputMenu.add(inputMenuItem0);
		inputMenu.add(inputMenuItem1);

		// Add help items to help menu
		helpMenu.add(helpMenuItem0);
		helpMenu.add(helpMenuItem1);

		// Menu Action Listeners
		ActionListener fileMenuListener, editMenuListener, viewMenuListener, inputMenuListener, helpMenuListener;

		fileMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Exit" :
						exit();
						break;
					case "Open" :
						// FileDialog fg = new FileDialog(frame, "Open a file");
						// fg.setVisible(true);
						// String file = fg.getDirectory() + fg.getFile();

						// selectInput("Select a file to process:",
						// "fileSelected");
						// public void fileSelected(File selection) {
						// print("SDW2");
						// }
						break;
					case "Save" :
						break;
					default :
						System.err.println("Unhandled menuitem.");
						break;
				}
			}
		};

		editMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Select All States" :
						selected.addAll(nodes);
						nodes.forEach(s -> s.select());
						break;
					case "Delete All States" :
						nodes.forEach(s -> s.kill());
						nodes.clear();
						break;
					case "Invert Selection" :
						ArrayList<State> temp = new ArrayList<>(nodes);
						temp.removeAll(selected);
						selected.forEach(s -> s.deselect());
						selected.clear();
						selected.addAll(temp);
						selected.forEach(s -> s.select());
						break;
					default :
						System.err.println("Unhandled menuitem.");
						break;
				}
			}
		};

		viewMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Save Stage As Image" :
						// TODO
						break;
					case "Reorder States" :
						// TODO
						break;
					default :
						System.err.println("Unhandled menuitem.");
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
						switch (mode) {
							case DFA :
								if (DFA.getInitialState() != null) {
									userInput = JOptionPane.showInputDialog("DFA Input: ");
									println(DFA.run(userInput));
								} else {
									Notification.addNotification(noInitialState);
									System.err.println("No Initial State Defined");
								}
								break;

							case DPA :
								if (DPA.getInitialState() != null) {
									DPA.setInitialStackSymbol(
											JOptionPane.showInputDialog("Initial Stack Symbol: ").charAt(0));
									userInput = JOptionPane.showInputDialog("DPA Input: ");
									println(DPA.run(userInput));
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
						JOptionPane.showMessageDialog(frame, Consts.about);
						break;
					case "Help" :
						JOptionPane.showMessageDialog(frame, Consts.help);
						break;
					default :
						System.err.println("Unhandled menuitem.");
						break;
				}
			}
		};

		fileMenu.addActionListener(fileMenuListener);
		editMenu.addActionListener(editMenuListener);
		viewMenu.addActionListener(viewMenuListener);
		inputMenu.addActionListener(inputMenuListener);
		helpMenu.addActionListener(helpMenuListener);

		// Adding menus to the menu bar
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(inputMenu);
		menuBar.add(helpMenu);

		// Adding menubar to frame
		f.setMenuBar(menuBar);
	}

	private Frame getFrame() {
		Frame frame = null;
		try {
			Field f = ((PSurfaceAWT) surface).getClass().getDeclaredField("frame");
			f.setAccessible(true);
			frame = (Frame) (f.get(((PSurfaceAWT) surface)));
		} catch (Exception e) {
			println(e);
		}
		return frame;
	}

	private void initCp5() {
		cp5 = new ControlP5(this);
	}

	public void nodeMouseOver() {
		for (State s : nodes) {
			if (withinRange(s.getPosition().x, s.getPosition().y, Consts.stateRadius, mouseX, mouseY)
					|| s.isMouseOver()) {
				mouseOverState = s;
				return;
			}
		}
		mouseOverState = null;
	}

	public static void deleteState(State s) {
		// call state functions to delete arrows
		s.kill();
		nodes.remove(s);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keysDown.add(e.getKey());
	}

	@Override
	public void keyReleased(KeyEvent key) {
		switch (key.getKeyCode()) {
			case 127 : // 127 == delete key
				selected.forEach(s -> deleteState(s));
				selected.clear();
				break;
			case 32 : // TODO remove (temp)
				if (mode == modes.DFA) {
					DFA.debug();
				} else {
					DPA.debug();
				}
			default :
				break;
		}
		keysDown.remove(key.getKey());
	}

	@Override
	public void mousePressed(MouseEvent m) {
		if (cp5.isMouseOver() || !allowGUIInterraction) {
			return;
		}

		mouseDown.add(m.getButton());
		mouseClickXY = mouseCoords.copy();

		switch (m.getButton()) {
			case LEFT :
				nodeMouseOver();
				if (mouseOverState == null) {
					if (!(selected.isEmpty())) {
						selected.forEach(s -> s.deselect());
						selected.clear();
					} else {
						if (selectionBox == null) {
							cursor(HAND);
							dragState = new State(mouseClickXY, nodes.size());
						}
					}

				} else {
					if (!mouseOverState.isMouseOver()) {
						cursor(HAND);
						dragState = mouseOverState;
						nodes.remove(dragState);
						selected.add(dragState);
						dragState.select();
					}
				}
				break;

			case RIGHT :
				nodeMouseOver();
				if (!(mouseOverState == null) && allowGUIInterraction) {
					arrowTailState = mouseOverState;
					drawingArrow = new Arrow(mouseClickXY, arrowTailState);
					cursor(CROSS);
				}
				break;

			default :
				break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent m) {
		cursor(ARROW);
		mouseReleasedXY = mouseCoords.copy();
		if (cp5.isMouseOver() || !allowGUIInterraction) {
			return;
		}

		switch (m.getButton()) {
			case LEFT :
				// selected.forEach(s -> {
				// if (!(s.UIOpen())) {
				// println("asd");
				// s.deselect();
				// selected.remove(s); //TODO
				// }
				// });

				nodeMouseOver();
				if (dragState != null) {
					selected.remove(dragState);
					dragState.deselect();
					nodes.add(dragState);
					dragState = null;
				}
				break;

			case RIGHT :
				if (selectionBox != null) {
					selected.forEach(s -> s.deselect());
					selected.clear();
					for (State s : nodes) {
						if (withinRegion(s.getPosition(), selectionBox.startPosition, selectionBox.endPosition)) {
							s.select();
							selected.add(s);
						}
					}
					selectionBox = null;
				} else {
					if (!(mouseClickXY.equals(mouseReleasedXY))) {
						nodeMouseOver();
						arrowHeadState = mouseOverState;
						if (arrowTailState != arrowHeadState && (arrowHeadState != null) && drawingArrow != null) {
							// TODO change logic for self transition
							allowGUIInterraction = false;
							drawingArrow.setTail(arrowTailState);
							drawingArrow.setHead(arrowHeadState);
							drawingArrow.update();
							arrowTailState.addArrowTail(drawingArrow);
							arrowHeadState.addArrowHead(drawingArrow);
							arrows.add(drawingArrow);
						}
						drawingArrow = null;
						if (arrowHeadState == null) {
							allowGUIInterraction = true;
						}
					} else {
						drawingArrow = null;
						if (mouseOverState != null) {
							selected.add(mouseOverState);
							mouseOverState.select();
							mouseOverState.showUI();
						}
					}
				}
				break;

			case CENTER :
				selected.forEach(s -> s.select());
				break;

			default :
				break;
		}
		mouseDown.remove(m.getButton());
	}

	public void mouseDragged(MouseEvent m) {
		switch (m.getButton()) {
			case LEFT : 
				break;
			case RIGHT :
				if (selectionBox == null && drawingArrow == null && allowGUIInterraction) {
					selectionBox = new SelectionBox(mouseCoords);
				}
				break;
			case CENTER :
				PVector offset = new PVector(mouseX - mouseClickXY.x, mouseY - mouseClickXY.y);
				for (State s : selected) {
					s.setPosition(new PVector(constrain(offset.x + s.getSelectedPosition().x, 0, width),
							constrain(offset.y + s.getSelectedPosition().y, 0, height)));
				}
				break;
			default :
				break;
		}
	}
}