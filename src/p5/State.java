package p5;

import java.util.ArrayList;

import main.Consts;
import processing.core.PApplet;
import processing.core.PVector;

public class State {

	public static PApplet p;
	public int ID;
	private PVector position;
	private ArrayList<Arrow> arrowHeads = new ArrayList<>(); //TODO
	private ArrayList<Arrow> arrowTails = new ArrayList<>(); //TODO
	public boolean selected = false; //TODO (deletion)
	
	
	//[transition symbol, next node, current string]

	public State(PVector XY, int ID) {
		position = XY;
		this.ID = ID;
	}

	public void draw() {
		if (!selected) {
		p.fill(255, 220, 0);
		}
		else {
			p.fill(0,35,255);
		}
		p.ellipse(position.x, position.y, Consts.stateRadius, Consts.stateRadius);
		if (!selected) {
			p.fill(0);
		}
		else {
			p.fill(255);
		}
		p.text("q"+ID, position.x, position.y);

	}

	public void setPosition(PVector position) {
		this.position = position;
	}

	public PVector getPosition() {
		return position;
	}
	
	public void addArrowHead(Arrow a) {
		
	}
	
	public void addArrowTail(Arrow a) {
		
	}
}