package snooker2D;

import java.io.Serializable;

public final class Vect2D implements Serializable {
    /*
     * Based on the Vector2D in Physics-Based Game Design lectures of the University of Essex.
     */
    public final double x, y;

    public Vect2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double mag() {
        return Math.hypot(x, y);
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    public Vect2D add(Vect2D v) {
        return new Vect2D(this.x + v.x, this.y + v.y);
    }


    public Vect2D addScaled(Vect2D v, double fac) {
        return new Vect2D(this.x + v.x * fac, this.y + v.y * fac);
    }

    public Vect2D mult(double fac) {
        return new Vect2D(this.x * fac, this.y * fac);
    }

    public Vect2D rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double nx = x * cos - y * sin;
        double ny = x * sin + y * cos;
        return new Vect2D(nx, ny);
    }

    public double scalarProduct(Vect2D v) {
        return x * v.x + y * v.y;
    }

    public Vect2D normalise() {
        double len = mag();
        return new Vect2D(x / len, y / len);
    }

    public Vect2D rotate90degreesAnticlockwise() {
        return new Vect2D(-y, x);
    }
}