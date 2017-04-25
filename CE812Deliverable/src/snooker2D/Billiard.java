package snooker2D;

import static snooker2D.BasicEngineForSnooker.U;
import static snooker2D.BasicEngineForSnooker.DELTA_T;

import java.awt.Color;
import java.awt.Graphics2D;

public class Billiard {
	/* 
	 * Author: Chao
	 */
	public Color color;
	public int value;
	private Vect2D pos, vel;
	private final Vect2D origin;
	private final double mass;
	private final double radius;	
	private boolean collisionDetect;

	private final int radiusForScreen;
	
	public Billiard(Color col, double sx, double sy, double vx, double vy, double radius, double mass, boolean collisionDetect, int value) {
		this.color = col;
		setPos(new Vect2D(sx, sy));
		origin = new Vect2D(sx, sy);
		setVel(new Vect2D(vx, vy));
		this.radius = radius;
		this.mass = mass;
		this.radiusForScreen = BasicEngineForSnooker.WorldXtoScreenX(radius);
		this.collisionDetect = collisionDetect;
		this.value = value;
	}
	
	public Billiard(Color col, Vect2D pos, double vx, double vy, double radius, double mass, boolean collisionDetect, int value) {
		this.color = col;
		setPos(pos);
		origin = pos;
		setVel(new Vect2D(vx, vy));
		this.radius = radius;
		this.mass = mass;
		this.radiusForScreen = BasicEngineForSnooker.WorldXtoScreenX(radius);
		this.collisionDetect = collisionDetect;
		this.value = value;
	}

	public void update() {
		// Use improved Euler method
		if (getVel().mag() > 0.01) {
			Vect2D acc = getVel().normalise().mult(-U);
			Vect2D vel2 = getVel().addScaled(acc, DELTA_T);
			Vect2D velAv = vel2.add(getVel()).mult(0.5);
			
			Vect2D acc2 = velAv.normalise().mult(-U);
			Vect2D accAv = acc2.add(acc).mult(0.5);
			
			setPos(getPos().addScaled(velAv, DELTA_T));
			setVel(getVel().addScaled(accAv, DELTA_T));
		}
		// Normal method
		/*setPos(getPos().addScaled(getVel(), DELTA_T));
		setVel(getVel().addScaled(getVel().normalise().mult(-U), DELTA_T));*/
	}

	public void draw(Graphics2D g) {
		int x = BasicEngineForSnooker.WorldXtoScreenX(getPos().x);
		int y = BasicEngineForSnooker.WorldYtoScreenY(getPos().y);
		g.setColor(color);
		g.fillOval(x - radiusForScreen, y - radiusForScreen, 2 * radiusForScreen, 2 * radiusForScreen);
	}
	
	public boolean collidesWith(Billiard ball2) {
		if (collisionDetect == true) {
			Vect2D vecFrom1to2 = ball2.getPos().addScaled(getPos(), -1);
			boolean movingTowardsEachOther = ball2.getVel().addScaled(getVel(), -1).scalarProduct(vecFrom1to2)<0;
			return vecFrom1to2.mag() < getRadius() + ball2.getRadius() && movingTowardsEachOther;
		} else 
			return false;
	}

	public static void implementElasticCollision(Billiard b1, Billiard b2, double e) {
		Vect2D normal = b2.getPos().addScaled(b1.getPos(), -1).normalise();
		double Jb = (e + 1) * (b1.getVel().scalarProduct(normal) - b2.getVel().scalarProduct(normal)) / ( 1 / b1.mass + 1 / b2.mass);
		b2.vel = b2.getVel().addScaled(normal.mult(Jb / b2.mass), 1);
		b1.vel = b1.getVel().addScaled(normal.mult(Jb / b1.mass), -1);
	}
	
	public void setToOrigin() {
		setPos(this.origin);
		setCollisionDetect(true);
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public Vect2D getPos() {
		return pos;
	}

	public void setPos(Vect2D pos) {
		this.pos = pos;
	}

	public Vect2D getVel() {
		return vel;
	}

	public void setVel(Vect2D vel) {
		this.vel = vel;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public boolean isCollisionDetect() {
		return collisionDetect;
	}

	public void setCollisionDetect(boolean collisionDetect) {
		this.collisionDetect = collisionDetect;
	}
}
