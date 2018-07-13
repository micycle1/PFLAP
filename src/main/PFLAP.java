package main;

import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.FileDialog;
//import java.io.File;
import java.awt.Frame;
import java.awt.Menu;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.JColorChooser;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import controlP5.ControlP5;
import controlP5.Textarea;
import machines.DFA;
import machines.DPA;

import p5.Arrow;
import p5.Notification;
import p5.SelectionBox;
import p5.State;

import processing.awt.*;

import static main.Consts.notificationHeight;
import static main.Consts.notificationWidth;
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
 * state resize / transition thickness
 * PGraphics.begindraw for screenshot transparency
 * DFA: if adding transition w/ same head & tail, merge into existing
 * pimage for arrow transitions
 * make machine non-static of type generic
 */
//@formatter:on

public class PFLAP extends PApplet {

	public static HashSet<Character> keysDown = new HashSet<Character>();
	public static HashSet<Integer> mouseDown = new HashSet<Integer>();

	public static ArrayList<Arrow> arrows = new ArrayList<>();
	public static ArrayList<State> nodes = new ArrayList<>();
	public static HashSet<State> selected = new HashSet<>();

	public static ControlP5 cp5;
	private static Textarea trace;

	private static State mouseOverState, arrowTailState, arrowHeadState, dragState;
	private static Arrow drawingArrow, mouseOverTransition;
	private static SelectionBox selectionBox = null;

	private PVector mouseClickXY, mouseReleasedXY, mouseCoords;
	private static PFont comfortaaRegular, comfortaaBold, traceFont;

	public static boolean allowGUIInterraction = true;

	public static PApplet p;

	public static enum modes {
		DFA, DPA;
	}

	public static modes mode = modes.DFA; // TODO change for test

	public static Color stateColour = new Color(255, 220, 0), stateSelectedColour = new Color(0, 35, 255),
			transitionColour = new Color(0, 0, 0), bgColour = new Color(255, 255, 255);

	public static void main(String[] args) {
		PApplet.main(PFLAP.class);
	}

	@Override
	public void setup() {
		p = this;
		surface.setTitle(Consts.title);
		surface.setLocation(displayWidth / 2 - width / 2, displayHeight / 2 - height / 2);
		surface.setResizable(false);
		surface.setResizable(true);
		comfortaaRegular = createFont("Comfortaa Regular", 24, true);
		traceFont = createFont("Comfortaa Regular", 12, true);
		comfortaaBold = createFont("Comfortaa Bold", Consts.stateFontSize, true);
		textFont(comfortaaBold);
		frameRate(60);
		strokeJoin(MITER);
		strokeWeight(3);
		stroke(0);
		textSize(Consts.stateFontSize);
		textAlign(CENTER, CENTER);
		rectMode(CORNER);
		ellipseMode(CENTER);
		cursor(ARROW);
		colorMode(RGB);
		initCp5();
		initMenuBar();
	}

	@Override
	public void settings() {
		size(Consts.WIDTH, Consts.HEIGHT);
		smooth(4);
	}

	@Override
	public void draw() {
		mouseCoords = new PVector(constrain(mouseX, 0, width), constrain(mouseY, 0, height));
		background(bgColour.getRGB());

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

		fill(0);
		strokeWeight(2);
		stroke(transitionColour.getRGB());
		textSize(18);
		textFont(comfortaaRegular);
		arrows.forEach(a -> a.draw());

		textSize(Consts.stateFontSize);
		textFont(comfortaaBold);
		stroke(0);
		strokeWeight(2);
		nodes.forEach(s -> s.draw());

		if (dragState != null) {
			dragState.setPosition(mouseCoords);
			dragState.draw();
		}

		Notification.run();
	}

	public void initMenuBar() {

		Frame f = getFrame();
		f.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				Notification.positionTarget = new PVector(p.width - notificationWidth, p.height - notificationHeight);
			}
		});

		MenuBar menuBar = new MenuBar();

		MenuItem fileMenuItem0, fileMenuItem1, fileMenuItem2;
		MenuItem editMenuItem0, editMenuItem1, editMenuItem2;
		MenuItem viewMenuItem0, viewMenuItem1;
		CheckboxMenuItem viewMenuCheckboxItem0;
		MenuItem inputMenuItem0, inputMenuItem1;
		MenuItem helpMenuItem0, helpMenuItem1;
		MenuItem defineColoursItem0, defineColoursItem1, defineColoursItem2, defineColoursItem3;

		// Top-Level Menus
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");
		Menu viewMenu = new Menu("View");
		Menu inputMenu = new Menu("Input");
		Menu helpMenu = new Menu("Help");

		// Second-Level Menus
		Menu defineColours = new Menu("Redefine Colours");
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
		//TODO view machine information (# states)

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
		viewMenu.add(defineColours);

		// Add input items to input menu
		inputMenu.add(inputMenuItem0);
		inputMenu.add(inputMenuItem1);

		// Add help items to help menu
		helpMenu.add(helpMenuItem0);
		helpMenu.add(helpMenuItem1);

		// Menu Action Listeners
		ActionListener fileMenuListener, editMenuListener, viewMenuListener, inputMenuListener, helpMenuListener,
				defineColoursListener;
		ItemListener tracerListener;

		fileMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Exit" :
						exit();
						break;
					case "Open" :
						FileDialog fg = new FileDialog(frame, "Open a file", FileDialog.LOAD);
						fg.setVisible(true);
						String file = fg.getDirectory() + fg.getFile();
						print(file);
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
						FileDialog fg = new FileDialog(frame, "Save", FileDialog.SAVE);
						fg.setDirectory(System.getProperty("user.home") + "\\Desktop");
						fg.setTitle("Save Frame");
						fg.setVisible(true);
						if (fg.getFile() != null) {
							String file = fg.getDirectory() + fg.getFile() + ".png";
							saveFrame(file);
						}
						break;
					case "Reorder States" :
						// TODO
						break;
					default :
						System.err.println("Unhandled Menuitem.");
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
									DFA.run(userInput);
									// /println();
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
						temp = JColorChooser.showDialog(null, "Choose State Colour", stateColour);
						if (temp != null) {
							stateColour = temp;
						}
						break;
					case "State Selected Colour" :
						temp = JColorChooser.showDialog(null, "Choose State Selected Color", stateSelectedColour);
						if (temp != null) {
							stateSelectedColour = temp;
						}
						break;
					case "Transition Arrow Colour" :
						temp = JColorChooser.showDialog(null, "Choose Transition Arrow Colour", transitionColour);
						if (temp != null) {
							transitionColour = temp;
						}
						break;
					case "Background Colour" :
						temp = JColorChooser.showDialog(null, "Choose Background Colour", bgColour);
						if (temp != null) {
							bgColour = temp;
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
				trace.setVisible(viewMenuCheckboxItem0.getState());
			}
		};

		fileMenu.addActionListener(fileMenuListener);
		editMenu.addActionListener(editMenuListener);
		viewMenu.addActionListener(viewMenuListener);
		inputMenu.addActionListener(inputMenuListener);
		helpMenu.addActionListener(helpMenuListener);
		defineColours.addActionListener(defineColoursListener);

		viewMenuCheckboxItem0.addItemListener(tracerListener);
		viewMenu.add(viewMenuCheckboxItem0);

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
		trace = cp5.addTextarea("Trace")
		// @formatter:off
				.setVisible(false)
				.setPosition(10, 670)
				.setSize(200, 100)
				//.setFont(createFont("", 12))
				.setFont(traceFont)
				.setLineHeight(14)
				.setColor(color(255))
				.setColorBackground(color(0, 200))
				.setMoveable(false)
				;
		// @formatter:on

		cp5.addConsole(trace);
		System.out.println("Tracer: Traces machine transitions during operation.");
	}

	public void pushTrace(String entry) {
		trace.append(entry + "\n\r");
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

	public void transitionMouseOver() {
		for (Arrow a : arrows) {
			if (a.isMouseOver(mouseClickXY)) {
				mouseOverTransition = a;
				return;
			}
		}
		mouseOverTransition = null;
	}

	public static void deleteState(State s) {
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
				transitionMouseOver();
				if (mouseOverState == null && mouseOverTransition == null) {
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
					if (mouseOverState != null) {
						if (!mouseOverState.isMouseOver()) {
							cursor(HAND);
							dragState = mouseOverState;
							nodes.remove(dragState);
							selected.add(dragState);
							dragState.select();
						}
					} else {
						if (mouseOverTransition != null) {
							// clicked on transition GUI
						}
					}
				}
				break;

			case RIGHT :
				nodeMouseOver();
				transitionMouseOver();
				if (!(mouseOverState == null) && allowGUIInterraction && mouseOverTransition == null) {
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
						} else {
							transitionMouseOver();
							if (mouseOverTransition != null) {
								mouseOverTransition.showUI(); // TODO
							}
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

	public void exit() {
		/**
		 * Finish-up
		 */
		super.exit();
	}
}