package snooker2D;

import java.awt.Graphics2D;

public abstract class Barrier {
	/* 
	 * Author: Michael Fairbank
	 */
	public abstract void draw(Graphics2D g);
	public abstract boolean isCircleCollidingBarrier(Vect2D circleCentre, double circleRadius);
	public abstract Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel);
}
