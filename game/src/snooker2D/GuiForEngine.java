package snooker2D;

import snooker2D.balls.Billiard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GuiForEngine {
    /*
     * Author: Chao
     */
    private static JButton jButton_newRound;
    private static JTextField jTextfields_p1;
    private static JTextField jTextfields_p2;
    private static JTextField jTextfields_hint;
    private static Thread theThread;

    public static void main(String[] args) throws Exception {
        BasicEngineForSnooker game = new BasicEngineForSnooker();
        final BasicView view = new BasicView(game);
        // Main panel
        JComponent mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(view, BorderLayout.CENTER);
        // Side panel
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new FlowLayout());
        jButton_newRound = new JButton("New Game");
        sidePanel.add(jButton_newRound);
        mainPanel.add(sidePanel, BorderLayout.WEST);

        JComponent topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Player1"));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        jTextfields_p1 = new JTextField();
        topPanel.add(jTextfields_p1);
        jTextfields_p1.setColumns(3);

        topPanel.add(new JLabel("Player2"));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        jTextfields_p2 = new JTextField();
        topPanel.add(jTextfields_p2);
        jTextfields_p2.setColumns(3);

        topPanel.add(new JLabel("Hint"));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        jTextfields_hint = new JTextField();
        topPanel.add(jTextfields_hint);
        jTextfields_hint.setColumns(20);

        JEasyFrame frame = new JEasyFrame(mainPanel, "Basic Engine");
        view.addKeyListener(new BasicKeyListener());

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == jButton_newRound) {
                    try {
                        final BasicEngineForSnooker game2 = new BasicEngineForSnooker();
                        view.updateGame(game2);
                        view.requestFocus();
                        startThread(game2, view); // start a new thread for the new game object:
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };

        jButton_newRound.addActionListener(listener);
    }

    private static void startThread(final BasicEngineForSnooker game, final BasicView view) throws InterruptedException {
        Runnable r = new Runnable() {
            public void run() {
                // Initialization
                game.pottedInThisRound = new ArrayList<Billiard>();
                game.collisionList = new ArrayList<Billiard>();
                game.objectBall = 1;

                while (theThread == Thread.currentThread()) {
                    for (int i = 0; i < BasicEngineForSnooker.NUM_EULER_UPDATES_PER_SCREEN_REFRESH; i++) {
                        game.update();
                        jTextfields_p1.setText(String.valueOf(game.score1));
                        jTextfields_p2.setText(String.valueOf(game.score2));
                        if (game.endGame == true) {
                            jTextfields_hint.setText("The Winner is: Player" + game.winner);
                        } else {
                            String ball = (game.objectBall) == 1 ? "Red-ball" : "Color-ball";
                            String player = (game.currentPlayer == true) ? "Player1" : "Player2";
                            jTextfields_hint.setText(player + "'s service. Object ball: " + ball);
                        }
                    }
                    view.repaint();
                    try {
                        Thread.sleep(BasicEngineForSnooker.DELAY);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        theThread = new Thread(r);
        theThread.start();
    }

}
