package main;

import static processing.core.PApplet.*;
import processing.core.PVector;

public class Functions {
	
	public static float angleBetween(PVector tail, PVector head) {
		return -atan2(tail.x - head.x, tail.y - head.y) - (PI / 2);
	}
	
	public static boolean withinRange(float x, float y, float diameter, float x2, float y2) {
		return (sqrt(sq(y - y2) + sq(x - x2)) < diameter / 2);
	}
	
	public static boolean numberBetween(float n, float a1, float a2) {
		return (n >= min(a1, a2) && n <= max(a1, a2));
	}

}
