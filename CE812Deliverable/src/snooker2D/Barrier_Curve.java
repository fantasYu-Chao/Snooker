package snooker2D;

import java.awt.Color;
import java.awt.Graphics2D;

public class Barrier_Curve extends Barrier {
	/* Author: Michael Fairbank
	 * Adapted by: Chao
	 */
	private final Vect2D centreCoordinates;
	private final Color color;
	private final double radius;
	private final int radiusInScreenCoordinates;
	private final double startAngle;
	private final double deltaAngle;
	private final boolean normalPointsInwards;
	private final double barrierDepth;
	
	public Barrier_Curve(double centrex, double centrey, Color col, double radius, double startAngle, double deltaAngle) {
		this(centrex, centrey, col, radius, startAngle, deltaAngle, false, 1);
	}
	
	public Barrier_Curve(double centrex, double centrey, Color col, double radius, double startAngle, double deltaAngle, boolean normalPointsInwards, double barrierDepth){
		centreCoordinates = new Vect2D(centrex, centrey);
		this.color = col;
		this.radius = radius;
		this.radiusInScreenCoordinates = BasicEngineForSnooker.WorldXtoScreenX(radius);
		this.startAngle = startAngle;
		this.deltaAngle = deltaAngle;
		this.normalPointsInwards = normalPointsInwards;
		this.barrierDepth = barrierDepth;
	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicEngineForSnooker.WorldXtoScreenX(centreCoordinates.x);
		int y1 = BasicEngineForSnooker.WorldYtoScreenY(centreCoordinates.y);
		g.setColor(color);
		// Give dimensions of a rectangle (x, y, width, height) that contains the ellipse
		g.drawArc(x1 - radiusInScreenCoordinates, y1 - radiusInScreenCoordinates, 
				radiusInScreenCoordinates * 2, radiusInScreenCoordinates * 2, (int)startAngle, (int)deltaAngle);
	}
	
	@Override
	public boolean isCircleCollidingBarrier(Vect2D circleCentre, double circleRadius) {
		Vect2D vector = circleCentre.addScaled(centreCoordinates, -1);
		double ang = vector.angle(); // relies on Math.atan2 function
		ang = ang * 180 / Math.PI; //convert from radians to degrees
		ang = (ang + 360) % 360;	// remove any negative angles to avoid confusion
		boolean withinAngleRange = false;
		if (deltaAngle < 0 && 
				((ang >= startAngle + deltaAngle && ang <= startAngle) || (ang >= startAngle + deltaAngle + 360 && ang <= startAngle + 360)))
			{withinAngleRange = true;}
		if (deltaAngle >= 0 && 
				((ang <= startAngle + deltaAngle && ang >= startAngle) || (ang <= startAngle + deltaAngle + 360 && ang >= startAngle + 360)))
			{withinAngleRange = true;}
		double distToCentreOfBarrierArc = vector.mag();
		boolean withinDistanceRange = (normalPointsInwards && distToCentreOfBarrierArc + circleRadius >= this.radius && distToCentreOfBarrierArc - circleRadius <= this.radius + barrierDepth) 
				|| (!normalPointsInwards && distToCentreOfBarrierArc - circleRadius <= this.radius && distToCentreOfBarrierArc + circleRadius >= this.radius - barrierDepth);
		return withinDistanceRange && withinAngleRange;
	}
	
	@Override
	public Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel) {
		Vect2D normal = pos.addScaled(centreCoordinates, -1).normalise();
		if (normalPointsInwards) normal=normal.mult(-1);
		Vect2D tangent=normal.rotate90degreesAnticlockwise().normalise();
		double vParallel=vel.scalarProduct(tangent);
		double vNormal=vel.scalarProduct(normal);
		if (vNormal<0) // assumes normal points AWAY from barrierPoint... 
			vNormal=-vNormal;
		Vect2D result=tangent.mult(vParallel);
		result=result.addScaled(normal, vNormal);
		return result;
	}
}
