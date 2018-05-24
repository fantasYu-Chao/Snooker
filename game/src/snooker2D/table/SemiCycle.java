package snooker2D.table;

import snooker2D.Vect2D;

import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

public class SemiCycle extends LineOnCloth{
    private Vect2D center;

    public SemiCycle(){
        super();
        this.center = new Vect2D(100.0, 135.0);
    }

    @Override
    public void draw(Graphics2D g) {
        Arc2D.Double semicycle = new Arc2D.Double(this.center.x, this.center.y, 133.0, 133.0, 90.0, 180.0, Arc2D.OPEN);
        g.draw(semicycle);
    }
}
