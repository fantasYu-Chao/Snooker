package snooker2D;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BasicKeyListener extends KeyAdapter {
    /*
     * Author: Chao
     */
    private static boolean RightKeyPressed, LeftKeyPressed, UpKeyPressed, DownKeyPressed, StrikeKeyPressed;

    public static boolean isRightKeyPressed() {
        return RightKeyPressed;
    }

    public static boolean isLeftKeyPressed() {
        return LeftKeyPressed;
    }

    public static boolean isUpKeyPressed() {
        return UpKeyPressed;
    }

    public static boolean isDownKeyPressed() {
        return DownKeyPressed;
    }

    public static boolean isStrikeKeyPressed() {
        return StrikeKeyPressed;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                UpKeyPressed = true;
                break;
            case KeyEvent.VK_LEFT:
                LeftKeyPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                RightKeyPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                DownKeyPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                StrikeKeyPressed = true;
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                UpKeyPressed = false;
                break;
            case KeyEvent.VK_LEFT:
                LeftKeyPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                RightKeyPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                DownKeyPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                StrikeKeyPressed = false;
                break;
        }
    }
}
