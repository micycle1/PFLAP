package main;

import processing.core.PVector;
import static processing.core.PApplet.atan2;
import static processing.core.PApplet.PI;
import static processing.core.PApplet.pow;
import static processing.core.PApplet.sq;
import static processing.core.PApplet.sqrt;
import static processing.core.PApplet.max;
import static processing.core.PApplet.min;

import java.awt.Color;



/**
 * Provides static geometry functions.
 */
public final class Functions {

	/**
	 * East = 0; North = -1/2PI; West = -PI; South = -3/2PI | 1/2PI
	 * 
	 * @param tail
	 *            PVector Coordinate 1.
	 * @param head
	 *            PVector Coordinate 2.
	 * @return float θ in radians.
	 */
	public static float angleBetween(PVector tail, PVector head) {
		float theta = -atan2(tail.x - head.x, tail.y - head.y) - (PI / 2);
		return theta;
	}

	/**
	 * Determine whether a point is within a radial region around another.
	 * 
	 * @param x
	 *            X Position of region center.
	 * @param y
	 *            Y Position of region center.
	 * @param diameter
	 *            Diameter of region.
	 * @param x2
	 *            X Position of point to test.
	 * @param y2
	 *            Y Position of point to test.
	 * @return True/False
	 */
	public static boolean withinRange(float x, float y, float diameter, float x2, float y2) {
		return (sqrt(sq(y - y2) + sq(x - x2)) < diameter / 2);
	}

	/**
	 * @param n
	 *            Number to test
	 * @param a1
	 *            Range Low
	 * @param a2
	 *            Range High
	 * @return True if Low >= n >= High
	 */
	public static boolean numberBetween(float n, float a1, float a2) {
		return (n >= min(a1, a2) && n <= max(a1, a2));
	}

	/**
	 * Determine if a point is within rectangular region.
	 * 
	 * @param point
	 *            PVector position to test.
	 * @param UL
	 *            Corner one of region.
	 * @param BR
	 *            Corner two of region (different X & Y).
	 * @return True if point contained in region.
	 */
	public static boolean withinRegion(PVector point, PVector UL, PVector BR) {
		return (point.x >= UL.x && point.y >= UL.y) && (point.x <= BR.x && point.y <= BR.y) // SE
				|| (point.x >= BR.x && point.y >= BR.y) && (point.x <= UL.x && point.y <= UL.y) // NW
				|| (point.x <= UL.x && point.x >= BR.x) && (point.y >= UL.y && point.y <= BR.y) // SW
				|| (point.x <= BR.x && point.x >= UL.x) && (point.y >= BR.y && point.y <= UL.y); // NE
	}
	
	/**
	 * Calculates luminosity contrast.
	 * The returned value should be bigger than 5 for best readability.
	 * @param a AWT Colour Object
	 * @param b AWT Colour Object
	 * @return 21.0 >=Double >= 1.0
	 */
	public static double lumdiff(Color a, Color b) {
		double L1 = 0.2126 * pow(a.getRed() / 255f, 2.2f) + 0.7152 * pow(a.getGreen() / 255f, 2.2f)
				+ 0.0722 * pow(a.getBlue() / 255f, 2.2f);

		double L2 = 0.2126 * pow(b.getRed() / 255f, 2.2f) + 0.7152 * pow(b.getGreen() / 255f, 2.2f)
				+ 0.0722 * pow(b.getBlue() / 255f, 2.2f);

		if (L1 > L2) {
			return (L1 + 0.05) / (L2 + 0.05);
		} else {
			return (L2 + 0.05) / (L1 + 0.05);
		}
	}

	private Functions() {
		throw new AssertionError();
	}
}