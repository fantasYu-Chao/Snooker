package snooker2D.table;

import java.awt.*;

public abstract class LineOnCloth {
    protected final Color color;

    public LineOnCloth(){
        this.color = new Color(147, 174, 162);
    }

    public abstract void draw(Graphics2D g);
}
