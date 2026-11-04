import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class DrawingBoard extends JPanel {

    boolean done = false;
    ArrayList<Brian> del = new ArrayList<>();
    ArrayList<Wall> walls = new ArrayList<>();
    ArrayList<Brian> oldBrians = new ArrayList<>();
    ArrayList<Brian> brians = new ArrayList<>();

    private int gridScale = 20;
    //added a discovery map to do floodfilling (saw from a tutorial and decided to use the method)
    private int[][] discoveryMap = new int[50][50];

    int step = 0;
    int max_gen;
    int brianCount;

    Timer timer;

    private void updateBrians() {
        if (brians.isEmpty()) return;

        for (Brian b : brians) {
            if (step < b.movement.length) {
                double currentDistSq = Math.pow(b.position.getX(), 2) + Math.pow(b.position.getY(), 2);

                //calculate intended next position
                int nextX = (int) (b.position.getX() + b.movement[step].getX());
                int nextY = (int) (b.position.getY() + b.movement[step].getY());

                boolean hitWall = false;

                if (currentDistSq > b.maxDistSq) {
                    b.maxDistSq = currentDistSq;
                    b.stepReachedMaxDistance = step;
                }

                //checking 3 points for collision: 25%, 50%, and 100% of the way there, to block jumping
                //I had SO MANY ISSUES WITH THIS, they would jump over the wall even when given speed values smaller
                //which I assume was caused by them having a next vector value that was opposite of the vector
                //they had when hitting the wall, causing the anti collision system to incorrectly push them back
                //on the other side of the wall instead of the original side, now I simply stop them from going
                //in it in the first place!
                for (double pull = 0.25; pull <= 1.0; pull += 0.25) {
                    int checkX = (int) (b.position.getX() + (b.movement[step].getX() * pull));
                    int checkY = (int) (b.position.getY() + (b.movement[step].getY() * pull));

                    for (Wall w : walls) {
                        if (checkX >= w.getX() && checkX <= w.getX() + w.getWidth() &&
                                checkY >= w.getY() && checkY <= w.getY() + w.getHeight()) {
                            hitWall = true;
                            break;
                        }
                    }
                    if (hitWall) break;
                }
                if (hitWall) {
                    b.hitCount++;
                } else {
                    b.position.setX(nextX);
                    b.position.setY(nextY);
                }

                //map exploration tracking
                int gX = (int)b.position.getX() / gridScale;
                int gY = (int)b.position.getY() / gridScale;
                if (gX >= 0 && gX < discoveryMap.length && gY >= 0 && gY < discoveryMap[0].length) {
                    discoveryMap[gX][gY]++;
                    b.uniqueCellsVisited.add(gX + "," + gY);
                }

                b.finalPosition = new Vector2D((int) b.position.getX(), (int) b.position.getY());
            }
        }

        //checking if the whole generation has finished their steps-1 to mark it as done just to make sure the
        //evolve thread doesn't screw anything up by messing with my Brians early!
        if (step >= brians.get(0).stepCount - 1) {
            done = true;
        } else {
            step++;
        }
        repaint();
    }

    public DrawingBoard(ArrayList<Wall> walls, ArrayList<Brian> brians, int generations) {
        this.brianCount = brians.size();
        this.max_gen = generations;
        this.walls = walls;
        this.brians = brians;
        timer = new Timer(10, e -> updateBrians());
        timer.start();
        //I'm using a thread to constantly do the Evolve process only when a generation is done!
        new Thread(this::Evolve).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < discoveryMap.length; i++) {
            for (int j = 0; j < discoveryMap[0].length; j++) {
                if (discoveryMap[i][j] > 0) {
                    int alpha = Math.min((int)(discoveryMap[i][j] * 0.1), 180);
                    g2d.setColor(new Color(0, 255, 255, alpha));
                    //decided on a cyan color for the flood to show how densely a spot is filled
                    //the less filled a spot, the more important the Brian/s that filled it is/are!

                    //I hate how I can't align this completely to the actual grid but its good enough I swear!
                    g2d.fillRect((i * gridScale), (j * gridScale), gridScale, gridScale);
                }
            }
        }

        g2d.setColor(Color.BLACK);
        for (Wall w : walls) g2d.fillRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());

        g2d.setColor(Color.RED);
        for (Brian b : brians) g2d.fillOval((int) (b.position.getX() - 4), (int) (b.position.getY() - 4), 8, 8);
    }

    public void resetBrians(ArrayList<Brian> brians) {
        timer.stop();
        for (int i = 0; i < discoveryMap.length; i++) {
            for (int j = 0; j < discoveryMap[0].length; j++) {
                discoveryMap[i][j] *= 0.9; //90% memory retention, makes sure brians end up finding a spot they keep visiting boring
            }
        }
        this.brians = brians;
        del.clear();
        oldBrians.clear();
        done = false;
        step = 0;
        repaint();
        timer.start();

        // ^ cleaning up the info from the last generation for the new one!
    }

    public void Evolve() {
        int gen = 0;
        while (gen < max_gen) {
            while (!done) { try { Thread.sleep(1); } catch (Exception e) {} }
            oldBrians.addAll(brians);
            evalFitness(oldBrians);
//            for (Brian b : oldBrians){
//                System.out.println(b.fitness);
//            }
            ArrayList<Brian> nextGen = newGen(oldBrians);
            resetBrians(nextGen);
            gen++;
        }
    }

    private ArrayList<Brian> newGen(ArrayList<Brian> oldBrians) {
        //sort by fitness descending (best ones first)
        oldBrians.sort(Comparator.<Brian>comparingDouble(b -> b.fitness).reversed());

        ArrayList<Brian> nextGeneration = new ArrayList<>();
        Random r = new Random();

        ArrayList<Brian> matingPool = new ArrayList<>();
        for (int i = 0; i < oldBrians.size(); i++) {
            matingPool.add(oldBrians.get(i));
        }

        for (int i = 0; i < brianCount; i++) {
            //two random parents are put up against the others tournament style
            //since the contenders are random, 2 (almost always at least) different parents are chosen
            Brian parentA = selectTournament(matingPool, r);
            Brian parentB = selectTournament(matingPool, r);
            Brian parentC = selectTournament(matingPool, r);

            Vector2D[] childGenes = new Vector2D[brians.get(0).stepCount];

            //replaced previous methods of coin flipping per move with a 2 point crossover
            int moveCount = 0;
            for (int j = 0; j < brians.get(0).stepCount; j++) {
                Vector2D chosenMove = new Vector2D(0,0);
                if(moveCount <= 333){
                    chosenMove = parentA.movement[j];
                } else if (moveCount > 333 && moveCount <= 666) {
                    chosenMove = parentB.movement[j];
                } else if (moveCount > 666 && moveCount <= 1000){
                    chosenMove = parentC.movement[j];
                }

                int newX = (int) chosenMove.getX();
                int newY = (int) chosenMove.getY();

                //mutation (20% chance per move)
                if (r.nextInt(100) <= 20) {
                    newX += r.nextInt(-3, 4);
                    newY += r.nextInt(-3, 4);
                }

                newX = clamp(newX,-5,5);
                newY = clamp(newY,-5,5);

                childGenes[j] = new Vector2D(newX, newY);
                moveCount++;
            }

            nextGeneration.add(new Brian(childGenes));
        }

        return nextGeneration;
    }

    private void evalFitness(ArrayList<Brian> brians) {
        for (Brian b : brians) {
            // Rewards and penalties
            double score = 0;

            // Penalty for loitering (detecting tiny or zero-movement steps)
            double loiterPenalty = 0;
            for (int i = 1; i < b.movement.length; i++) {
                double movCalcX = b.movement[i-1].getX() + b.movement[i].getX();
                if(movCalcX >= -3 || movCalcX <= 3){
                    loiterPenalty += 2;
                }
                double movCalcY = b.movement[i-1].getY() + b.movement[i].getY();
                if(movCalcY >= -3 || movCalcY <= 3){
                    loiterPenalty += 2;
                }
            }

            for (String cell : b.uniqueCellsVisited) {
                String[] p = cell.split(",");
                int x = Integer.parseInt(p[0]);
                int y = Integer.parseInt(p[1]);
                // Higher reward for cells that are freshly visited (toned down slightly so it doesn't override distance)
                if (discoveryMap[x][y] < 10) {
                    score += 100.0;
                } else {
                    score += (Math.log10(discoveryMap[x][y] + 1));
                }
            }

            // Just grabbing the total distance
            double totalDistance = Math.sqrt(Math.pow(b.position.getX(), 2) + Math.pow(b.position.getY(), 2));

            // Progress gauge: heavily weighted net distance to force them outward instead of wiggling locally
            double progressBonus = totalDistance * 5.0;

            // Fixed syntax error for collision penalty
            double collisionPenalty = b.hitCount * 2.0;

            // Velocity bonus: distance divided by time
            double velocityBonus = totalDistance / (b.stepReachedMaxDistance + 1);

            // Combine all components cleanly
            b.fitness = Math.max(0, score + progressBonus + (velocityBonus * 20.0) - collisionPenalty - loiterPenalty);

            // Softened scaling (changed from Math.pow(..., 2) to prevent premature convergence and wipeouts (hopefully?))
            b.fitness = Math.pow(b.fitness, 1.2);

            b.uniqueCellsVisited.clear();
        }
    }

    private Brian selectTournament(ArrayList<Brian> pool, Random r) {
        int split = getWeightedMove();

        // Pick 3 random Brians from the mating pool
        Brian best = null;
        for (int i = 0; i < 3; i++) {
            Brian contender = pool.get(r.nextInt(pool.size()));
            if (best == null || contender.fitness > best.fitness) {
                best = contender;
            }
        }
        return best;
    }

    public int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }
    public boolean isSimFinished() { return done; }

    public int getWeightedMove() {
        double r = Math.random() * 140;
        if (r < 70) return 4;
        if (r < 110) return 3;
        if (r < 130) return 2;
        return 1;
    }
}
