package snooker2D.table;

import snooker2D.Vect2D;

import java.awt.*;


public class BalkLine extends LineOnCloth{
    private Vect2D startPos, endPos;

    public BalkLine(){
        super();
        this.startPos = new Vect2D(166.0, 8.0);
        this.endPos = new Vect2D(166.0, 398.0);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(this.color);
        g.drawLine((int)this.startPos.x, (int)this.startPos.y, (int)this.endPos.x, (int)this.endPos.y);
    }

}
