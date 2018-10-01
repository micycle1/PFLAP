package main;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import static processing.core.PApplet.pow;
import static processing.core.PApplet.sq;
import static processing.core.PApplet.sqrt;

import java.awt.Color;

/**
 * <b>Functions</b>
 * <p>Provides static functions. Mostly geometry related.</p>
 */
public final class Functions {

	/**
	 * East = 0; North = -1/2PI; West = -PI; South = -3/2PI | 1/2PI
	 * @param tail PVector Coordinate 1.
	 * @param head PVector Coordinate 2.
	 * @return float θ in radians.
	 */
	public static float angleBetween(PVector tail, PVector head) {
		float a = PApplet.atan2(tail.y - head.y, tail.x - head.x);
		if (a < 0) {
			a += PConstants.TWO_PI;
		}
		return a;
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
	public static boolean numberBetween(double n, double a1, double a2) {
		return (n >= Math.min(a1, a2) && n <= Math.max(a1, a2));
	}

	/**
	 * Determine if a point is within rectangular region.
	 * @param point PVector position to test.
	 * @param UL Corner one of region.
	 * @param BR Corner two of region (different X & Y).
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

	/**
	 * Substitutes ' ' chars with 'λ'.
	 * @param test char to test substitution
	 * @return char λ if input was ' '.
	 * @see {@link #testForLambda(String) testForLambda(String)}
	 */
	public static char testForLambda(char test) {
		if (test == ' ') {
			return Consts.lambda;
		} else {
			return test;
		}
	}

	/**
	 * Subsitutes all ' ' for λ within a String.
	 * @param test String to substitute.
	 * @return String with λ.
	 * @see {@link #testForLambda(char) testForLambda(char)}
	 */
	public static String testForLambda(String test) {
		return test.replace(' ', Consts.lambda);
	}

	/**
	 * Converts (R,G,B) values to integer representation,
	 * compatible with Processing.
	 * @param R Red Value [0-255].
	 * @param G Green Value [0-255].
	 * @param B Blue Value [0-255].
	 * @return Color int.
	 * @see {@link #color(int, int, int, float) fdssdsd}
	 */
	public static int color(int R, int G, int B) {
		return new Color(R, G, B).getRGB();
	}

	/**
	 * Converts (R,G,B,A) values to integer representation,
	 * compatible with Processing.
	 * @param R Red Value [0-255].
	 * @param G Green Value [0-255].
	 * @param B Blue Value [0-255].
	 * @param A Alpha (transparency) [0.0-1.0].
	 * @return Color int.
	 */
	public static int color(int R, int G, int B, float A) {
		return new Color(((float) R) / 255, ((float) G) / 255, ((float) B) / 255, A / 255).getRGB();
	}

	/**
	 * Returns inverted color of parameter.
	 * @param c AWT Colour Object
	 * @return Color inverse of <b>c</b> (integer representation).
	 */
	public static int invertColor(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()).getRGB();
	}

	/**
	 * Darkens the given color.
	 * @param c AWT Colour Object
	 * @param percentage Percentage amount to darken the color by, where:
	 * <p>0.0 = no change; 0.5 = half brightness; 1.0 = black.
	 * @return Darker version of <b>c</b> (integer representation).
	 */
	public static int darkenColor(Color c, float percentage) {
		if (!numberBetween(percentage, 0, 1)) {
			throw new IllegalArgumentException("Percentage must be between 0 and 1 (inclusive).");
		}
		int r = c.getRed() - (int) (c.getRed() * percentage);
		int g = c.getGreen() - (int) (c.getGreen() * percentage);
		int b = c.getBlue() - (int) (c.getBlue() * percentage);
		return new Color(r, g, b).getRGB();
	}

	private Functions() {
		throw new AssertionError();
	}
}