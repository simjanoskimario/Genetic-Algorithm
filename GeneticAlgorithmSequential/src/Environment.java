import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Environment extends JFrame {

    int brianCount;
    int slotSize = 50;
    int size = 10;
    MazeGenerator maze = new MazeGenerator(size,size);
    DrawingBoard drawingBoard;
    ArrayList<Wall> walls = new ArrayList<>();
    ArrayList<Brian> brians = new ArrayList<Brian>();

    public Environment(int brianCount, int generation_count){
        this.brianCount = brianCount;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Evolution - Environment");

        for(int r = 0; r < size+1; r++){
            for(int c = 0; c < size; c++){
                if(maze.getHorizontalWalls()[r][c]) {
                    walls.add(new Wall(c*slotSize, r*slotSize, slotSize + 7, 7));
                }
            }
        }

        for(int r = 0; r < size; r++){
            for(int c = 0; c < size+1; c++){
                if(maze.getVerticalWalls()[r][c]) {
                    walls.add(new Wall(c*slotSize, r*slotSize, 7, slotSize));
                }
            }
        }

        for (int i = 0; i < brianCount; i++) {
            brians.add(new Brian());
        }

        drawingBoard = new DrawingBoard(walls, brians, generation_count);

        this.setSize(size * slotSize + 20, size * slotSize + 40);
        this.add(drawingBoard);
        this.setVisible(true);
    }


}
