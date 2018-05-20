package p5;

import garciadelcastillo.dashedlines.*;
import processing.core.PApplet;
import processing.core.PVector;
import main.Consts;

public class SelectionBox {

	DashedLines d;
	static PApplet p;
	int dist = 0, width, height;
	public PVector startPosition, endPosition;

	public SelectionBox(PApplet p, PVector position) {
		SelectionBox.p = p;
		this.startPosition = position;
		endPosition = startPosition;
		d = new DashedLines(p);
		d.pattern(6);
	}

	public void draw() {
		p.noFill();
		p.stroke(0);
		p.fill(200, 200, 255, 50);
		d.rect(startPosition.x, startPosition.y, endPosition.x - startPosition.x, endPosition.y - startPosition.y);
		d.offset(dist);
		dist += 1;
		p.fill(80, 100);
		p.noStroke();
		p.ellipse(startPosition.x, startPosition.y, 2 * Consts.SBNodeRadius, 2 * Consts.SBNodeRadius);
		p.ellipse(endPosition.x, endPosition.y, 2 * Consts.SBNodeRadius, 2 * Consts.SBNodeRadius);
	}

	public void setEndPosition(PVector endPosition) {
		this.endPosition = endPosition;
	}
}