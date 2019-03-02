package p5;

import static main.Consts.SBNodeRadius;
import static main.PFLAP.p;

import garciadelcastillo.dashedlines.DashedLines;
import processing.core.PVector;

public final class SelectionBox {

	private DashedLines d;
	private int dist = 0;
	public final PVector startPosition;
	private PVector endPosition;

	public SelectionBox(PVector position) {
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
		p.ellipse(startPosition.x, startPosition.y, 2 * SBNodeRadius, 2 * SBNodeRadius);
		p.ellipse(endPosition.x, endPosition.y, 2 * SBNodeRadius, 2 * SBNodeRadius);
	}
	
	public void setEndPosition(PVector endPosition) {
		this.endPosition = endPosition;
	}
}