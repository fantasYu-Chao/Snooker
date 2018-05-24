package snooker2D;

import snooker2D.balls.Billiard;
import snooker2D.colors.GameColors;
import snooker2D.table.BalkLine;
import snooker2D.table.Barrier;
import snooker2D.table.SemiCycle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class BasicView extends JComponent {
    /*
     * Author: Michael Fairbank
     */

    public static final Color BG_COLOR = GameColors.cloth(); // background colour

    private BasicEngineForSnooker game;

    public BasicView(BasicEngineForSnooker game) {
        this.game = game;
    }

    @Override
    public void paintComponent(Graphics g0) {
        BasicEngineForSnooker game;
        synchronized (this) {
            game = this.game;
        }
        Graphics2D g = (Graphics2D) g0;
        g.setColor(BG_COLOR); // paint the background
        g.fillRect(0, 0, getWidth(), getHeight());
        BalkLine balkLine = new BalkLine();
        balkLine.draw(g);

        SemiCycle semiCycle = new SemiCycle();
        semiCycle.draw(g);

        for (Billiard b : game.billiards)
            b.draw(g);
        for (Barrier b : game.barriers)
            b.draw(g);
        for (Billiard p : game.pockets)
            p.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return BasicEngineForSnooker.FRAME_SIZE;
    }

    public synchronized void updateGame(BasicEngineForSnooker game) {
        this.game = game;
    }
}
