package p5;

import static main.Consts.selfTransitionLength;
import static main.Functions.angleBetween;
import static main.PFLAP.p;

import static processing.core.PApplet.cos;
import static processing.core.PApplet.radians;
import static processing.core.PApplet.sin;

import main.Functions;
import main.PFLAP;
import processing.core.PVector;

public class SelfArrow extends AbstractArrow {

	private PVector selfBezierCP1, selfBezierCP2, selfBezierTranslate, selfBezierTextLoc;
	private final float selfTransitionAngle;
	private float selfBezierAngle, textSize;

	public SelfArrow(State s) {
		super(s, s);
		selfTransitionAngle = radians((PFLAP.arrows.size() * 50));
		update();
	}

	@Override
	public void update() {
		textSize = 24;
		
		selfBezierCP1 = new PVector(head.getPosition().x + selfTransitionLength * sin(selfTransitionAngle),
				head.getPosition().y + selfTransitionLength * cos(selfTransitionAngle));
		selfBezierCP2 = new PVector(
				head.getPosition().x + selfTransitionLength * sin(selfTransitionAngle + radians(45)),
				head.getPosition().y + selfTransitionLength * cos(selfTransitionAngle + radians(45)));
		selfBezierTranslate = new PVector(head.getPosition().x + head.getRadius() / 2 * sin(selfTransitionAngle),
				head.getPosition().y + head.getRadius() / 2 * cos(selfTransitionAngle));
		selfBezierAngle = angleBetween(head.getPosition(), selfBezierCP1) - 0.3f;
		selfBezierTextLoc = new PVector(
				p.bezierPoint(head.getPosition().x, selfBezierCP1.x, selfBezierCP2.x, head.getPosition().x, 0.5f)
						+ 15 * sin(selfTransitionAngle),
				p.bezierPoint(head.getPosition().y, selfBezierCP1.y, selfBezierCP2.y, head.getPosition().y, 0.5f)
						+ 15 * cos(selfTransitionAngle));
		transitionSymbolEntry.setPosition(selfBezierTextLoc.x - transitionSymbolEntry.getWidth() / 2,
				selfBezierTextLoc.y + 10);
		stateOptions.setPosition(selfBezierTextLoc.x, selfBezierTextLoc.y + 10);
	}

	@Override
	public void draw() {
		p.strokeWeight(2);
		p.noFill();
		p.bezier(head.getPosition().x, head.getPosition().y, selfBezierCP1.x, selfBezierCP1.y, selfBezierCP2.x,
				selfBezierCP2.y, head.getPosition().x, head.getPosition().y);
		drawArrowTip(selfBezierTranslate, selfBezierAngle);
		drawTransitionLabel(selfBezierTextLoc);
	}

	@Override
	public boolean isMouseOver(PVector mousePos) {
		return Functions.withinRange(selfBezierTextLoc.x, selfBezierTextLoc.y, textSize * 2, mousePos.x, mousePos.y)
				|| cp5.isMouseOver();
	}
}
