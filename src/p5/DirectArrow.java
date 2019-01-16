package p5;

import static main.Functions.angleBetween;
import static main.PFLAP.p;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.dist;
import static processing.core.PApplet.map;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

import main.Functions;
import processing.core.PApplet;
import processing.core.PVector;

public class DirectArrow extends AbstractArrow {

	private int labelRotationModifier;
	private float theta1, theta2, labelRotationOffset, textSize;
	private PVector directTail, directHead, midPoint;

	public DirectArrow(State head, State tail) {
		super(head, tail);
	}
	
	public DirectArrow(State head, State tail, char transitionSymbol, char stackPop, String stackPush) {
		super(head, tail, transitionSymbol, stackPop, stackPush);
	}

	@Override
	public void update() {
		theta2 = angleBetween(tail.getPosition(), head.getPosition());
		theta1 = (theta2 + PI) % TWO_PI;
		if (Functions.numberBetween(theta2, PApplet.HALF_PI, 1.5 * PI)) {
			labelRotationModifier = 1;
			labelRotationOffset = theta1;
		} else {
			labelRotationModifier = -1;
			labelRotationOffset = theta2;
		}
		midPoint = new PVector((head.getPosition().x + tail.getPosition().x) / 2,
				(head.getPosition().y + tail.getPosition().y) / 2);
		textSize = map(PVector.dist(tail.getPosition(), head.getPosition()), 0, 200, 10, 18);
		directTail = new PVector(tail.getPosition().x + tail.getRadius() * -0.5f * cos(theta2),
				tail.getPosition().y + tail.getRadius() * -0.5f * sin(theta2));

		directHead = new PVector(head.getPosition().x + head.getRadius() * -0.5f * cos(theta1),
				head.getPosition().y + head.getRadius() * -0.5f * sin(theta1));
//		transitionSymbolEntry.setPosition(midPoint.x - transitionSymbolEntry.getWidth() / 2, midPoint.y + 10);
		stateOptions.setPosition(midPoint.x, midPoint.y + 20);
	}

	@Override
	public void draw() {
		p.strokeWeight(2);
		p.line(directTail.x, directTail.y, directHead.x, directHead.y);
		drawArrowTip(directHead, theta1);
		p.pushMatrix();
		p.translate(directTail.x, directTail.y);
		p.rotate(labelRotationOffset);

		p.textSize(textSize);
		drawTransitionLabel(new PVector(
				labelRotationModifier * dist(directTail.x, directTail.y, directHead.x, directHead.y) / 2, 10));
		p.popMatrix();
	}

	@Override
	public boolean isMouseOver(PVector mousePos) {
		float dx = mousePos.x - midPoint.x;
		float dy = mousePos.y - midPoint.y;
		float nx = dx * cos(-theta2) - (dy * sin(-theta2));
		float ny = dy * cos(-theta2) + (dx * sin(-theta2));
		float dist = dist(tail.getPosition().x, tail.getPosition().y, head.getPosition().x, head.getPosition().y) / 2;
		boolean inside = (abs(nx) < dist * 2 / 2) && (abs(ny) < 10 / 2);
		return inside || Functions.withinRange(mousePos.x, mousePos.y, 20, midPoint.x, midPoint.y) || cp5.isMouseOver();
	}

}
