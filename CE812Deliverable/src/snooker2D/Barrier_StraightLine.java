package snooker2D;

import static snooker2D.BasicEngineForSnooker.E;

import java.awt.Color;
import java.awt.Graphics2D;


public class Barrier_StraightLine extends Barrier{
	/* Author: Michael Fairbank
	 * Adapted by: Chao
	 */
	private Vect2D startPos, endPos, unitTangent, unitNormal;
	private final Color color;
	private double barrierDepth;
	private boolean collisionDetect;
	
	public Barrier_StraightLine(double startx, double starty, double endx, double endy, Color col) {
		this(startx, starty, endx, endy, col, 0, true);
	}
	
	public Barrier_StraightLine(double startx, double starty, double endx, double endy, Color col, double barrierDepth, boolean collisionDetect) {
		startPos = new Vect2D(startx, starty);
		endPos = new Vect2D(endx, endy);
		this.color = col;
		unitTangent = endPos.addScaled(startPos, -1).normalise();
		unitNormal = unitTangent.rotate90degreesAnticlockwise();
		this.collisionDetect = collisionDetect;
	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicEngineForSnooker.WorldXtoScreenX(startPos.x);
		int y1 = BasicEngineForSnooker.WorldYtoScreenY(startPos.y);
		int x2 = BasicEngineForSnooker.WorldXtoScreenX(endPos.x);
		int y2 = BasicEngineForSnooker.WorldYtoScreenY(endPos.y);
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}
	
	@Override
	public boolean isCircleCollidingBarrier(Vect2D circleCentre, double circleRadius) {
		if (collisionDetect == true) {
			Vect2D vector = circleCentre.addScaled(this.startPos, -1);
			double distOnCorrectSideOfBarrierToCentre = vector.scalarProduct(this.unitNormal);
			double distAlongBarrier = vector.scalarProduct(this.unitTangent);
			
			return distOnCorrectSideOfBarrierToCentre<=circleRadius && (this.barrierDepth == 0 || distOnCorrectSideOfBarrierToCentre>=-(barrierDepth+circleRadius))
					&& distAlongBarrier>=0 && distAlongBarrier <= this.endPos.addScaled(startPos, -1).mag();
		}
		return false;
	}

	@Override
	public Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel) {
		double vParallel = vel.scalarProduct(this.unitTangent);
		double vNormal = vel.scalarProduct(this.unitNormal);
		if (vNormal < 0) // assumes normal points AWAY from wall... 
			vNormal = -vNormal * E;
		Vect2D result = this.unitTangent.mult(vParallel);
		result = result.addScaled(this.unitNormal, vNormal);
		return result;
	}
	
	public Vect2D transformToVector() {
		return (endPos.addScaled(startPos, -1));
	}

	public Vect2D getStartPos() {
		return startPos;
	}

	public void setStartPos(Vect2D startPos) {
		this.startPos = startPos;
	}

	public Vect2D getEndPos() {
		return endPos;
	}

	public void setEndPos(Vect2D endPos) {
		this.endPos = endPos;
	}
}
