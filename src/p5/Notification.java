package p5;

import static main.Consts.notificationHeight;
import static main.Consts.notificationLifetime;
import static main.Consts.notificationLifetimeFast;
import static main.Consts.notificationLifetimeVeryFast;
import static main.Consts.notificationTextPadding;
import static main.Consts.notificationWidth;
import static main.PFLAP.p;

import com.google.common.graph.*;

import java.util.HashSet;
import java.util.LinkedList;

import main.Consts;
import main.Functions;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Handles both drawing and logic of notifications.
 */
public class Notification {

	private static final LinkedList<Notification> notifications = new LinkedList<>();
	private static final PImage background;
	private static PVector positionTarget;

	private final PVector position = new PVector(p.width - notificationWidth, p.height);
	private final String title, message;
	private int lifetime, startTime, alpha = 255;

	static {
		positionTarget = new PVector(p.width - notificationWidth, p.height - notificationHeight);
		background = p.createImage(notificationWidth, notificationHeight, PApplet.ARGB);
		for (int i = 0; i < background.pixels.length; i++) {
			float a = PApplet.map(i, 0, background.pixels.length, 255, 0);
			background.pixels[i] = Functions.colorToRGB(10, 100, 100, a);
		}
	}

	/**
	 * Private method; creates GUI notifications internally.
	 * <p>Call the static method {@link #addNotification(String, String) addNotification} to create notifications.
	 * @param title
	 * @param message
	 */
	private Notification(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public static void clear() {
		notifications.clear();
	}

	public static void addNotification(Consts.notificationData data) {
		notifications.add(new Notification(data.title(), data.message()));
	}
	
	public static void addNotification(String title, String message) {
		notifications.add(new Notification(title, message));
	}
	
	public static void addStateInfoNotification(State s) {
		int state = main.PFLAP.PApplet.view.getIDByState(s);
		int degree = model.Model.transitionGraph.degree(state);
		int inDegree = model.Model.transitionGraph.inDegree(state);
		int outDegree = model.Model.transitionGraph.outDegree(state);
		HashSet<Integer> reachable = new HashSet<Integer>(
				Graphs.reachableNodes(model.Model.transitionGraph.asGraph(), state));
		reachable.remove(state);
		notifications.add(new Notification("State Information",
				"State ID: " + s.getID() + "\n" + "Degree: " + degree + "\n" + "In-Degree: " + inDegree + "\n"
						+ "Out-Degree: " + outDegree + "\n" + "Reachable Nodes: " + reachable.size()));
	}

	/**
	 * Call this within PApplet's draw() loop.
	 */
	public static void run() {
		if (!(notifications.isEmpty())) {
			notifications.getFirst().draw();
		}
	}

	public static void stageResized() {
		positionTarget = new PVector(p.width - notificationWidth, p.height - notificationHeight);
	}

	private void draw() {
		if (startTime == 0) {
			p.tint(255, 255);
			startTime = p.frameCount;
			if (notifications.size() > 2) {
				if (notifications.size() < 6) {
					lifetime = notificationLifetimeFast;
				} else {
					lifetime = notificationLifetimeVeryFast;
				}
			} else {
				lifetime = notificationLifetime;
			}
		}
		if (position.y > positionTarget.y) {
			position.y -= 10;
		}
		if ((p.frameCount - startTime) >= lifetime) {
			p.tint(255, alpha);
			alpha -= 10;
			if (alpha < 0) {
				notifications.removeFirst();
			}
		}
		p.fill(255, 255, 255, alpha);
		p.textAlign(PApplet.LEFT, PApplet.TOP);
		p.image(background, position.x, position.y);
		p.textSize(14);
		// @formatter:off
		p.text("Notification: " + title,
				position.x + notificationTextPadding,
				position.y + notificationTextPadding,
				notificationWidth - notificationTextPadding, 
				notificationHeight - notificationTextPadding);
		p.textSize(12);
		p.textLeading(14);
		p.fill(0, 0, 0, alpha);
		p.text(message,
				position.x + notificationTextPadding,
				position.y + notificationTextPadding + 32,
				notificationWidth - notificationTextPadding, 
				notificationHeight - notificationTextPadding);
		 // @formatter:on
		p.textAlign(PApplet.CENTER, PApplet.CENTER);
		p.noTint();
	}
}