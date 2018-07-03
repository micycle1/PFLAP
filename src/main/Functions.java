package main;

import static processing.core.PApplet.*;
import processing.core.PVector;

/**
 * Provides static geometry functions.
 */
public final class Functions {
	
	/**
	 * East = 0; North = -1/2PI; West = -PI; South = -3/2PI | 1/2PI
	 * @param tail PVector Coordinate 1.
	 * @param head PVector Coordinate 2.
	 * @return float θ in radians.
	 */
	public static float angleBetween(PVector tail, PVector head) {
		float theta = -atan2(tail.x - head.x, tail.y - head.y) - (PI / 2);
		return theta;
	}
	
	/**
	 * Determine whether a point is within a radial region around another.
	 * @param x X Position of region center.
	 * @param y Y Position of region center.
	 * @param diameter Diameter of region.
	 * @param x2 X Position of point to test.
	 * @param y2 Y Position of point to test.
	 * @return True/False
	 */
	public static boolean withinRange(float x, float y, float diameter, float x2, float y2) {
		return (sqrt(sq(y - y2) + sq(x - x2)) < diameter / 2);
	}
	
	/**
	 * @param n Number to test
	 * @param a1 Range Low
	 * @param a2 Range High
	 * @return True if Low >= n >= High 
	 */
	public static boolean numberBetween(float n, float a1, float a2) {
		return (n >= min(a1, a2) && n <= max(a1, a2));
	}
	
	private Functions() {
		throw new AssertionError();
	}

}
