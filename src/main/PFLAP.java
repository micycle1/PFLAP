package main;

import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.awt.Menu;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;

import DFA.Machine;
import GUI.Notification;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import controlP5.ControlP5;
import p5.Arrow;
import p5.SelectionBox;
import p5.State;
import processing.awt.*;

/**
 * TODO:
 * State self bezier-arrows
 * Right-click menu on arrows
 */
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

	public static boolean allowNewArrow = true;

	public static void main(String[] args) {
		PApplet.main(PFLAP.class);
	}

	@Override

	public void setup() {
		initCp5();
		initMenuBar();
		State.p = this; // Static PApplet for State objects
		Arrow.p = this; // Static PApplet for Arrow objects
		surface.setTitle("PFLAP: Processing Formal Languages and Automata Package");
		surface.setLocation(displayWidth / 2 - width / 2, displayHeight / 2 - height / 2);
		surface.setResizable(false);
		frameRate(60);
		strokeJoin(MITER);
		strokeWeight(3);
		stroke(0);
		textSize(Consts.stateFontSize);
		textAlign(CENTER, CENTER);
		rectMode(CORNER);
		ellipseMode(CENTER);
		cursor(ARROW);
		Notification.notifi("test"); //TODO remove

	}

	@Override
	public void settings() {
		size(Consts.WIDTH, Consts.HEIGHT);
		smooth(8);
	}

	@Override
	public void draw() {
		mouseCoords = new PVector(constrain(mouseX, 0, width), constrain(mouseY, 0, height));
		// textAlign(LEFT, TOP);
		background(255);
		fill(0);

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
		arrows.forEach(a -> a.draw());

		if (mouseDown.contains(CENTER)) {
			PVector offset = new PVector(mouseX - mouseClickXY.x, mouseY - mouseClickXY.y);
			for (State s : selected) {
				s.setPosition(new PVector(offset.x + s.getSelectedPosition().x, offset.y + s.getSelectedPosition().y));
			}
		}

		nodes.forEach(s -> s.draw());

		if (dragState != null) {
			dragState.setPosition(mouseCoords);
			dragState.draw();
		}

	}

	public void initMenuBar() {
		// Declarations
		Frame f = getFrame();
		MenuBar menuBar = new MenuBar();
		MenuItem fileMenuItem0, fileMenuItem1, fileMenuItem2, editMenuItem0, editMenuItem1, editMenuItem2,
				inputMenuItem0, helpMenuItem0, helpMenuItem1;

		// Top-Level Menus
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");
		Menu inputMenu = new Menu("Input");
		Menu helpMenu = new Menu("Help");

		// File Menu
		fileMenuItem0 = new MenuItem("Exit");
		fileMenuItem1 = new MenuItem("Open");
		fileMenuItem2 = new MenuItem("Save");

		// Edit Menu
		editMenuItem0 = new MenuItem("Delete All States");
		// + deleted selected
		editMenuItem1 = new MenuItem("Select All States");
		editMenuItem2 = new MenuItem("Invert Selection");

		// Input Menu
		inputMenuItem0 = new MenuItem("Step By State");

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

		// Add input items to input menu
		inputMenu.add(inputMenuItem0);

		// Add help items to help menu
		helpMenu.add(helpMenuItem0);
		helpMenu.add(helpMenuItem1);

		// Menu Action Listeners
		ActionListener fileMenuListener, editMenuListener, inputMenuListener, helpMenuListener;

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
						break;
					default :
						break;
				}

			}
		};

		editMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Delete All States" :
						nodes.forEach(s -> s.kill());
						nodes.clear();
						break;
					case "Select All States" :
						selected.addAll(nodes);
						nodes.forEach(s -> s.select());
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
						break;
				}
			}
		};

		inputMenuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (event.getActionCommand()) {
					case "Step By State" :
						String userInput = JOptionPane.showInputDialog("DFA Input: ");
						if (Machine.getInitialState() != null) {
							println(Machine.run(userInput));
						}
						else {
							//notification TODO
							System.err.println("No Initial State Defined");
						}
						break;
					default :
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
						break;
				}
			}
		};

		fileMenu.addActionListener(fileMenuListener);
		editMenu.addActionListener(editMenuListener);
		inputMenu.addActionListener(inputMenuListener);
		helpMenu.addActionListener(helpMenuListener);

		// Adding menus to the menu bar
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
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

	private static boolean withinRange(float x, float y, float diameter, float x2, float y2) {
		return (sqrt(sq(y - y2) + sq(x - x2)) < diameter / 2);
	}

	public static float angleBetween(PVector tail, PVector head) {
		return -atan2(tail.x - head.x, tail.y - head.y) - (PI / 2);
	}

	public static boolean numberBetween(float n, float a1, float a2) {
		return (n >= min(a1, a2) && n <= max(a1, a2));
	}

	private static boolean withinSelection(State s) {
		PVector sXY = s.getPosition();
		PVector bSP = selectionBox.startPosition;
		PVector bEP = selectionBox.endPosition;
		return sXY.x >= bSP.x && sXY.y >= bSP.y && sXY.x <= bEP.x && sXY.y <= bEP.y;
	}

	public void nodeMouseOver() {
		for (State s : nodes) {
			if (withinRange(s.getPosition().x, s.getPosition().y, Consts.stateRadius, mouseX, mouseY)
					|| s.cp5.isMouseOver()) {
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
				Machine.debug();
			default :
				break;
		}
		keysDown.remove(key.getKey());
	}

	@Override
	public void mousePressed(MouseEvent m) {
		if (cp5.isMouseOver()) {
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
					if (!mouseOverState.cp5.isMouseOver()) {
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
				if (!(mouseOverState == null) && allowNewArrow) {
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
		if (cp5.isMouseOver()) {
			return;
		}

		if (selectionBox != null) {
			selected.forEach(s -> s.deselect());
			selected.clear();
			for (State s : nodes) {
				if (withinSelection(s)) {
					s.select();
					selected.add(s);
				}
			}
			selectionBox = null;
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
				if (!(mouseClickXY.equals(mouseReleasedXY))) {
					nodeMouseOver();
					arrowHeadState = mouseOverState;
					if (arrowTailState != arrowHeadState && (arrowHeadState != null) && drawingArrow != null) {
						drawingArrow.tail = arrowTailState;
						drawingArrow.head = arrowHeadState;
						drawingArrow.update();
						arrowTailState.addArrowTail(drawingArrow);
						arrowHeadState.addArrowHead(drawingArrow);
						arrows.add(drawingArrow);
					}
					drawingArrow = null;
					if (arrowHeadState == null) {
						allowNewArrow = true;
					}
				} else {
					drawingArrow = null;
					if (mouseOverState != null) {
						selected.add(mouseOverState);
						mouseOverState.select();
						mouseOverState.showUI();
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
		if (mouseDown.contains(RIGHT) && selectionBox == null && drawingArrow == null) {
			selectionBox = new SelectionBox(this, mouseCoords);
		}
	}
}