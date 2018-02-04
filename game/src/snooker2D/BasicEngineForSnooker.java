package snooker2D;

import snooker2D.balls.Billiard;
import snooker2D.colors.Brown;
import snooker2D.colors.Pink;
import snooker2D.table.Barrier;
import snooker2D.table.Barrier_Curve;
import snooker2D.table.Barrier_StraightLine;

import java.awt.Dimension;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class BasicEngineForSnooker {
    /*
     * Author: Chao
     */
    // Frame dimension
    public static final int SCREEN_HEIGHT = 405;
    public static final int SCREEN_WIDTH = 800;
    public static final Dimension FRAME_SIZE = new Dimension(
            SCREEN_WIDTH, SCREEN_HEIGHT);

    // World definition
    public static final double WORLD_WIDTH = 3.57; // (m)
    public static final double WORLD_HEIGHT = WORLD_WIDTH * SCREEN_HEIGHT / SCREEN_WIDTH;

    public static final double E = 0.70; // coefficient of restitution
    public static final double U = 10; // rolling friction

    // Update configuration
    public static final int DELAY = 20; // sleep time between frames 10 (ms)
    public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH = 1;
    public static final double DELTA_T = DELAY / 10000.0 / NUM_EULER_UPDATES_PER_SCREEN_REFRESH; // 20000.0

    // public variables for determination
    public int score1, score2, lastIn, objectBall, redBallNum, colorBallNum, winner, redBallAllPotted;
    public boolean calibration = false, determineService = false, colorBallAllPotted = false, endGame = false;
    public boolean areBilliardsStatic = true, currentPlayer = true, player1 = true;
    public Barrier_StraightLine ray = new Barrier_StraightLine(0, 0, 0, 0, Color.WHITE);
    public List<Billiard> pottedInThisRound, collisionList;
    public Billiard lastPotted, validBallLast;

    // Frame world transformation
    public static int WorldXtoScreenX(double worldX) {
        return (int) (worldX / WORLD_WIDTH * SCREEN_WIDTH);
    }

    public static int WorldYtoScreenY(double worldY) {
        return (int) (SCREEN_HEIGHT - (worldY / WORLD_HEIGHT * SCREEN_HEIGHT));
    }

    public List<Barrier> barriers;
    public List<Billiard> billiards;
    public List<Billiard> pockets;

    public BasicEngineForSnooker() {
        // Layout
        double tableWidth = WORLD_WIDTH;
        double pocketSize = 0.024 * tableWidth;
        double cushionDepth = 0.35 * pocketSize; // (0.35 * pocketSize) for recommendation
        double curveRadius = pocketSize * cushionDepth / (pocketSize - 2 * cushionDepth);
        double cushionLength = WORLD_WIDTH / 2 - 2 * cushionDepth - pocketSize / 2 -
                2 * (curveRadius - cushionDepth);
        double tableHeight = cushionLength + 2 * (curveRadius - cushionDepth) + 4 * cushionDepth;
        double serviceLine = cushionDepth + 0.2 * WORLD_WIDTH;
        double serviceRadius = 0.082 * WORLD_WIDTH;

        barriers = new ArrayList<Barrier>();
        createCushion(barriers, tableHeight / 2, tableHeight / 2, 0, curveRadius, cushionLength);
        createCushion(barriers, tableHeight / 2, tableHeight / 2, Math.PI / 2, curveRadius, cushionLength);
        createCushion(barriers, tableHeight / 2, tableHeight / 2, Math.PI * 3 / 2, curveRadius, cushionLength);
        createCushion(barriers, tableWidth - tableHeight / 2, tableHeight / 2, Math.PI / 2, curveRadius, cushionLength);
        createCushion(barriers, tableWidth - tableHeight / 2, tableHeight / 2, Math.PI, curveRadius, cushionLength);
        createCushion(barriers, tableWidth - tableHeight / 2, tableHeight / 2, Math.PI * 3 / 2, curveRadius, cushionLength);

        pockets = new ArrayList<Billiard>();
        pockets.add(new Billiard(Color.BLACK, cushionDepth / 2, cushionDepth / 2, 1, 1, 0.01 * WORLD_WIDTH / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, cushionDepth / 2, WORLD_HEIGHT - cushionDepth / 2, 1, 1, 0.01 * WORLD_WIDTH / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, WORLD_WIDTH / 2, WORLD_HEIGHT, 1, 1, 0.01 * WORLD_WIDTH / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, WORLD_WIDTH / 2, 0, 1, 1, 0.01 * WORLD_WIDTH / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, WORLD_WIDTH - cushionDepth / 2, cushionDepth / 2, 1, 1, 0.01 * WORLD_WIDTH / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, WORLD_WIDTH - cushionDepth / 2, WORLD_HEIGHT - cushionDepth / 2, 1, 1, 0.01 * WORLD_WIDTH / 2, 0, false, 0));

        pockets.add(new Billiard(Color.BLACK, cushionDepth / 2, cushionDepth / 2, 1, 1, pocketSize / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, cushionDepth / 2, WORLD_HEIGHT - cushionDepth / 2, 1, 1, pocketSize / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, WORLD_WIDTH / 2, WORLD_HEIGHT, 1, 1, pocketSize / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, WORLD_WIDTH / 2, 0, 1, 1, pocketSize / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, WORLD_WIDTH - cushionDepth / 2, cushionDepth / 2, 1, 1, pocketSize / 2, 0, false, 0));
        pockets.add(new Billiard(Color.BLACK, WORLD_WIDTH - cushionDepth / 2, WORLD_HEIGHT - cushionDepth / 2, 1, 1, pocketSize / 2, 0, false, 0));
        // Billiards
        Vect2D yellowPos = new Vect2D(serviceLine, WORLD_HEIGHT / 2 - serviceRadius);
        Vect2D greenPos = new Vect2D(serviceLine, WORLD_HEIGHT / 2 + serviceRadius);
        Vect2D brownPos = new Vect2D(serviceLine, WORLD_HEIGHT / 2);
        Vect2D bluePos = new Vect2D(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        Vect2D pinkPos = new Vect2D(WORLD_WIDTH * 3 / 4 - cushionDepth / 2, WORLD_HEIGHT / 2);
        Vect2D blackPos = new Vect2D(WORLD_WIDTH - cushionDepth - 0.09 * WORLD_WIDTH, WORLD_HEIGHT / 2);
        double ballWeight = .15;
        double ballSize = 0.015 * WORLD_WIDTH;

        billiards = new ArrayList<Billiard>();
        // Colored billiards and cue-ball
        billiards.add(new Billiard(Color.WHITE, serviceLine - 0.01, WORLD_HEIGHT / 2 - 0.18, 0, 0, ballSize / 2, ballWeight, true, 0));
        billiards.add(new Billiard(Color.YELLOW, yellowPos, 0, 0, ballSize / 2, ballWeight, true, 2));
        billiards.add(new Billiard(Color.GREEN, greenPos, 0, 0, ballSize / 2, ballWeight, true, 3));
        billiards.add(new Billiard(new Brown(), brownPos, 0, 0, ballSize / 2, ballWeight, true, 4)); // brown ball
        billiards.add(new Billiard(Color.BLUE, bluePos, 0, 0, ballSize / 2, ballWeight, true, 5));
        billiards.add(new Billiard(new Pink(), pinkPos, 0, 0, ballSize / 2, ballWeight, true, 6)); // pink ball
        billiards.add(new Billiard(Color.BLACK, blackPos, 0, 0, ballSize / 2, ballWeight, true, 7));

        // Red billiards starts from no.8 end with no.22
        for (int i = 1; i <= 5; i++) {
            for (int j = 0; j < i; j++) {
                double rx = pinkPos.x + 1.1 * ballSize + (i - 1) * ballSize * 0.87;
                double ry = WORLD_HEIGHT / 2 + (2 * j + 1 - i) * ballSize / 2;
                billiards.add(new Billiard(Color.RED, rx, ry, 0, 0, ballSize / 2, ballWeight, true, 1));
            }
        }
    }

    private void createCushion(List<Barrier> barriers, double centrex, double centrey, double orientation, double curveRadius, double cushionLength) {
        Color col = Color.WHITE;
        Vect2D p1 = new Vect2D(-cushionLength / 2 - 2 * curveRadius, cushionLength / 2);
        Vect2D p2 = new Vect2D(-cushionLength / 2 - curveRadius, cushionLength / 2);
        Vect2D p3 = new Vect2D(-cushionLength / 2 - curveRadius, -cushionLength / 2);
        Vect2D p4 = new Vect2D(-cushionLength / 2 - 2 * curveRadius, -cushionLength / 2);

        // For increasing the smoothness of cushions
        if (orientation >= Math.PI * 3 / 2 || orientation == 0) {
            p2 = p2.add(new Vect2D(.003, 0));
            p3 = p3.add(new Vect2D(.003, 0));
        }

        p1 = p1.rotate(orientation);
        p2 = p2.rotate(orientation);
        p3 = p3.rotate(orientation);
        p4 = p4.rotate(orientation);
        barriers.add(new Barrier_Curve(centrex + p1.x, centrey + p1.y, col, curveRadius, -5 + orientation * 180 / Math.PI, 50));
        barriers.add(new Barrier_Curve(centrex + p4.x, centrey + p4.y, col, curveRadius, 5 + orientation * 180 / Math.PI, -50));
        barriers.add(new Barrier_StraightLine(centrex + p2.x, centrey + p2.y, centrex + p3.x, centrey + p3.y, col));
    }

    public static void main(String[] arg) throws Exception {
        final BasicEngineForSnooker game = new BasicEngineForSnooker();
        final BasicView view = new BasicView(game);
        JEasyFrame frame = new JEasyFrame(view, "Basic Engine");
        frame.addKeyListener(new BasicKeyListener());
        game.startThread(view);
    }

    private void startThread(final BasicView view) throws InterruptedException {
        final BasicEngineForSnooker game = this;
        // Initialization
        pottedInThisRound = new ArrayList<Billiard>();
        collisionList = new ArrayList<Billiard>();
        // validBallLast = billiards.get(2);
        objectBall = 1;

        while (true) {
            // Update for the every time step specified.
            game.update();
            view.repaint();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
            }
        }
    }

    // Update and executing the rules
    public void update() {
        redBallNum = 0;
        colorBallNum = 0;
        areBilliardsStatic = true;
        // Collision
        for (Billiard ball : billiards) {
            // snooker2D.table.Barrier-ball collision
            for (Barrier b : barriers) {
                if (b.isCircleCollidingBarrier(ball.getPos(), ball.getRadius())) {
                    Vect2D vel2 = b.calculateVelocityAfterACollision(ball.getPos(), ball.getVel());
                    ball.setVel(vel2);
                }
            }
            // Ball-ball collision
            for (Billiard ball2 : billiards) {
                areBilliardsStatic = areBilliardsStatic && (ball.getVel().mag() < 0.01 ? true : false);
                if (ball2.collidesWith(ball)) {
                    if (ball2.value == 0) {
                        collisionList.add(ball);

                    }
                    Billiard.implementElasticCollision(ball, ball2, 0.95);
                }
            }
            // Reach the pockets
            for (Billiard pocket : pockets) {
                if (ball.collidesWith(pocket)) {
                    pottedInThisRound.add(ball);
                    ball.setCollisionDetect(false);
                    ball.setVel(new Vect2D(0, 0));
                    ball.setPos(new Vect2D(3.7, 1.7));
                    lastPotted = ball;
                }
            }
            if (ball.value == 1 && ball.isCollisionDetect() == true)
                redBallNum += 1;
            if (ball.value > 1 && ball.isCollisionDetect() == true)
                colorBallNum += 1;
            ball.update();
        }

        Billiard cueball = billiards.get(0);
        if (areBilliardsStatic) {
            // Determine the player, score and rearrange the billiards
            if (determineService == true && endGame == false) {
                // System.out.println(redBallNum);
                // First collision
                int fistCollisionValue = 0;
                if (collisionList.isEmpty()) {
                    currentPlayer = (currentPlayer == player1) ? false : true;
                    scoring(currentPlayer, 4);
                    if (redBallNum != 0)
                        objectBall = 1;
                    if (redBallNum == 0 && objectBall == 2)
                        redBallAllPotted = 2;
                    if (billiards.get(0).isCollisionDetect() == false)
                        billiards.get(0).setToOrigin();
                } else {
                    fistCollisionValue = collisionList.get(0).value;
                    // System.out.println(fistCollisionValue);

                    // Red ball(s) on table
                    if (redBallAllPotted < 2) {
                        // No ball in pocket
                        if (pottedInThisRound.isEmpty()) {
                            lastIn = 0;
                            if (!((objectBall == fistCollisionValue) || ((fistCollisionValue > objectBall) & (objectBall != 1)))) {
                                scoring(currentPlayer, -Math.max(fistCollisionValue, 4));
                            }
                            currentPlayer = (currentPlayer == player1) ? false : true;
                            objectBall = 1;
                        } else {
                            int thisIn = pottedInThisRound.indexOf(lastPotted) + 1;
                            // One ball in pocket
                            if (thisIn == 1) {
                                if (lastPotted.value == 0) {
                                    currentPlayer = (currentPlayer == player1) ? false : true;
                                    scoring(currentPlayer, Math.max(4, fistCollisionValue));
                                    lastPotted.setToOrigin();
                                    objectBall = 1;
                                } else if (lastPotted.value > 1 && (objectBall > 1) && (lastPotted.value == fistCollisionValue)) {
                                    //&& (validBallLast.value == 1)) {
                                    scoring(currentPlayer, lastPotted.value);
                                    // validBallLast = lastPotted;
                                    objectBall = 1;
                                    lastIn = 1;
                                    // Reset
                                    lastPotted.setToOrigin();
                                } else if (lastPotted.value == 1 && (objectBall == 1) && (fistCollisionValue == 1)) {
                                    //(validBallLast.value > 1 || lastIn == 0)) {
                                    scoring(currentPlayer, 1);
                                    // validBallLast = lastPotted;
                                    objectBall = 2;
                                    lastIn = 1;
                                } else {
                                    currentPlayer = (currentPlayer == player1) ? false : true;
                                    if (lastPotted.value == 1) {
                                        scoring(currentPlayer, 4);
                                    } else {
                                        scoring(currentPlayer, lastPotted.value);
                                    }
                                    if (lastPotted.value > 1)
                                        lastPotted.setToOrigin();
                                    objectBall = 1;
                                    lastIn = 0;
                                }
                            }

                            // More than one in pocket
                            else if (thisIn > 1) {
                                int temp = 1;
                                for (Billiard b : pottedInThisRound) {
                                    temp = temp * b.value;
                                }
                                // All potted are red balls
                                if (objectBall == 1 && temp == 1) {
                                    // Valid
                                    scoring(currentPlayer, thisIn);
                                    // validBallLast = lastPotted;
                                    lastIn = 1;
                                    objectBall = 2;
                                }
                                // Invalid
                                else {
                                    int temp2 = 1;
                                    // Reset the colored billiards
                                    for (Billiard b : pottedInThisRound) {
                                        if (b.value > 1) {
                                            b.setToOrigin();
                                            objectBall = 1;
                                        }
                                        if (b.value == 1 && redBallNum == 0) {
                                            // The last red ball potted so the opponent should start from the yellow
                                            objectBall = 2;
                                        }
                                        if (b.value == 0) {
                                            b.setToOrigin();
                                            temp2 = 4;
                                            objectBall = 1;
                                        }
                                    }
                                    for (Billiard b : pottedInThisRound) {
                                        temp2 = ((temp2 > b.value) ? temp2 : b.value);
                                    }
                                    currentPlayer = (currentPlayer == player1) ? false : true;
                                    scoring(currentPlayer, temp2);
                                }

                            }

                        }

                        if (redBallNum == 0) {
                            redBallAllPotted = redBallAllPotted + 1;
                            objectBall = 2;
                        }
                    }
                    // All red balls are potted
                    else if (redBallAllPotted == 2 && colorBallAllPotted == false) {

                        // No ball in pocket
                        if (pottedInThisRound.isEmpty()) {
                            if (objectBall != fistCollisionValue) {
                                scoring(currentPlayer, -Math.max(objectBall, 4));
                            }
                            currentPlayer = (currentPlayer == player1) ? false : true;
                        } else {
                            int thisIn = pottedInThisRound.indexOf(lastPotted) + 1;
                            // One ball in pocket
                            if (thisIn == 1) {
                                if (lastPotted.value == 0) {
                                    currentPlayer = (currentPlayer == player1) ? false : true;
                                    scoring(currentPlayer, Math.max(4, fistCollisionValue));
                                    lastPotted.setToOrigin();
                                }
                                // Right ball, scoring
                                else if ((lastPotted.value == objectBall) && (fistCollisionValue == lastPotted.value)) {
                                    scoring(currentPlayer, objectBall);
                                    objectBall += 1;
                                    lastIn = 1;
                                    colorBallNum += -1;
                                } else {
                                    currentPlayer = (currentPlayer == player1) ? false : true;
                                    scoring(currentPlayer, lastPotted.value);
                                    lastPotted.setToOrigin();
                                    lastIn = 0;
                                }
                            }

                            // More than one in pocket
                            else if (thisIn > 1) {
                                // Invalid
                                for (Billiard b : pottedInThisRound)
                                    b.setToOrigin();

                                int temp = objectBall;
                                for (Billiard b : pottedInThisRound) {
                                    temp = ((temp > b.value) ? temp : b.value);
                                }
                                currentPlayer = (currentPlayer == player1) ? false : true;
                                scoring(currentPlayer, temp);
                            }
                        }
                        if (colorBallNum == 0)
                            colorBallAllPotted = true;
                        // System.out.println(colorBallNum);
                    }
                    // Game is end
                    else {
                        endGame = true;
                    }


                }
                pottedInThisRound = new ArrayList<Billiard>();
                collisionList = new ArrayList<Billiard>();
                determineService = false;
            }
            // Set the winner
            else if (endGame == true) {
                winner = (score1 > score2) ? 1 : 2;
            }

            // Create calibration line
            if (calibration == false) {
                Vect2D temp = new Vect2D(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
                temp = temp.addScaled(cueball.getPos(), -1).mult(0.3);
                ray = createCalibrationLine(cueball.getPos(), temp);
                calibration = true;
            }

            if (BasicKeyListener.isLeftKeyPressed()) {
                barriers.remove(18);
                Vect2D temp = ray.transformToVector().rotate(Math.PI / 180.0 / 3);
                ray = createCalibrationLine(cueball.getPos(), temp);
            }
            if (BasicKeyListener.isRightKeyPressed()) {
                barriers.remove(18);
                Vect2D temp = ray.transformToVector().rotate(-Math.PI / 180.0 / 3);
                ray = createCalibrationLine(cueball.getPos(), temp);
            }
            if (BasicKeyListener.isUpKeyPressed()) {
                barriers.remove(18);
                Vect2D temp = ray.transformToVector();
                temp = temp.add(temp.normalise().mult(0.01));
                ray = createCalibrationLine(cueball.getPos(), temp);
            }
            if (BasicKeyListener.isDownKeyPressed()) {
                barriers.remove(18);
                Vect2D temp = ray.transformToVector();
                temp = temp.add(temp.normalise().mult(-0.01));
                ray = createCalibrationLine(cueball.getPos(), temp);
            }
            if (BasicKeyListener.isStrikeKeyPressed()) {
                strikeCueball(cueball, ray);
                determineService = true;
            }
        }
    }

    public Barrier_StraightLine createCalibrationLine(Vect2D startPos, Vect2D rel) {
        Vect2D endPos = startPos.add(rel);
        Barrier_StraightLine r = new Barrier_StraightLine(startPos.x, startPos.y,
                endPos.x, endPos.y, Color.WHITE, 0, false);
        barriers.add(18, r);
        return r;
    }

    public void scoring(boolean current, int scoring) {
        if ((scoring > 0) && (current == true)) {
            score1 = score1 + scoring;
        } else if ((scoring < 0) && (current == false)) {
            score1 = score1 - scoring;
        } else if ((scoring < 0) && (current == true)) {
            score2 = score2 - scoring;
        } else if ((scoring > 0) && (current == false)) {
            score2 = score2 + scoring;
        }
    }

    public void strikeCueball(Billiard cueball, Barrier_StraightLine ray) {
        cueball.setVel(ray.transformToVector().mult(30));
        calibration = false;
        barriers.remove(18);
    }
}
