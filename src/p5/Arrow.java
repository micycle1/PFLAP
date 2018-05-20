package p5;

import main.Consts;
import main.PFLAP;
import processing.core.PApplet;
import processing.core.PVector;

public class Arrow {

	public static PApplet p;
	public State tail, head; //ARROW POINTS TO TAIL/HEAD????
	private PVector tailXY, headXY;
	private float rotationOffset;

	public Arrow(PVector startXY, State tail) {
		this.tail = tail;
		tailXY = startXY;
		headXY = tailXY;
	}

	public Arrow(PVector startXY, PVector endXY, State tail) {
		tailXY = startXY;
		headXY = endXY;
		update();
	}

	public void update() {
		rotationOffset = PFLAP.angleBetween(tailXY, headXY);
	}
	
	public void reset() {
		float theta1 = PFLAP.angleBetween(head.getPosition(), tail.getPosition());
		PVector newHeadXy = new PVector(
				head.getPosition().x + Consts.stateRadius * 0.5f * PApplet.cos(theta1),
				head.getPosition().y + Consts.stateRadius * 0.5f * PApplet.sin(theta1));
		this.setHeadXY(newHeadXy);

		float theta2 = PFLAP.angleBetween(tail.getPosition(), tail.getPosition());
		PVector newTailXy = new PVector(
				tail.getPosition().x + Consts.stateRadius * 0.5f * PApplet.cos(theta2),
				tail.getPosition().y + Consts.stateRadius * 0.5f * PApplet.sin(theta2));
		this.setTailXY(newTailXy);
	}

	public void draw() {
		p.line(tailXY.x, tailXY.y, headXY.x, headXY.y);
		p.noFill();
		p.pushMatrix();
		p.translate(headXY.x, headXY.y);
		p.rotate(rotationOffset);
		p.beginShape();
		p.vertex(-10, -7);
		p.vertex(0, 0);
		p.vertex(-10, 7);
		p.endShape();
		p.popMatrix();
	}

	public PVector getTailXY() {
		return tailXY;
	}

	public void setTailXY(PVector tailXY) {
		this.tailXY = tailXY;
		update();
	}

	public void setHeadXY(PVector headXY) {
		this.headXY = headXY;
		update();
	}
}