package p5;

import static main.Consts.transitionBezierCurve;
import static main.Functions.angleBetween;
import static main.PFLAP.p;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.map;
import static processing.core.PApplet.sin;

import java.util.ArrayList;

import main.Functions;
import model.LogicalTransition;
import processing.core.PConstants;
import processing.core.PVector;

public class BezierArrow extends AbstractArrow {

	private float textSize, arrowTipAngle, theta2;
	private PVector bezierCPoint, bezierApex, arrowTip, midPoint;
	
	public BezierArrow(State head, State tail, ArrayList<LogicalTransition> t) {
		super(head, tail, t);
	}

	@Override
	public void update() {
		theta2 = angleBetween(tail.getPosition(), head.getPosition());
		midPoint = new PVector((head.getPosition().x + tail.getPosition().x) / 2,
				(head.getPosition().y + tail.getPosition().y) / 2);
		textSize = map(PVector.dist(tail.getPosition(), head.getPosition()), 0, 200, 10, 16);
		bezierCPoint = new PVector(midPoint.x + (sin(-theta2) * (transitionBezierCurve * 2 * -1)),
				midPoint.y + (cos(-theta2) * (transitionBezierCurve * 2 * -1)));
		bezierApex = new PVector(midPoint.x - (bezierCPoint.x - midPoint.x),
				midPoint.y - (bezierCPoint.y - midPoint.y));
		arrowTipAngle = angleBetween(head.getPosition(), bezierApex);
		arrowTip = new PVector(head.getPosition().x + (head.getRadius() + 3) * -0.5f * cos(arrowTipAngle),
				head.getPosition().y + (head.getRadius() + 3) * -0.5f * sin(arrowTipAngle));
//		transitionSymbolEntry.setPosition(bezierApex.x - transitionSymbolEntry.getWidth() / 2, bezierApex.y + 10);
		stateOptions.setPosition(bezierApex.x, bezierApex.y + 10);
	}

	@Override
	public void draw() {
		p.noFill();
		p.curve(bezierCPoint.x, bezierCPoint.y, tail.getPosition().x, tail.getPosition().y, head.getPosition().x,
				head.getPosition().y, bezierCPoint.x, bezierCPoint.y);
		drawArrowTip(arrowTip, arrowTipAngle);
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.textSize(textSize);
		drawTransitionLabel(bezierApex);
	}

	@Override
	public boolean isMouseOver(PVector mousePos) {
		return Functions.withinRange(bezierApex.x, bezierApex.y, textSize * 2, mousePos.x, mousePos.y)
				|| cp5.isMouseOver();
	}

}
