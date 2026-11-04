import java.util.*;

public class MazeGenerator {

    private final int rows, cols;
    private final boolean[][] visited;
    private final boolean[][] verticalWalls;
    private final boolean[][] horizontalWalls;
    private final Random random = new Random();

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        visited = new boolean[rows][cols];

        //grid
        verticalWalls = new boolean[rows][cols + 1];
        horizontalWalls = new boolean[rows + 1][cols];

        //walls
        for (boolean[] row : verticalWalls) Arrays.fill(row, true);
        for (boolean[] row : horizontalWalls) Arrays.fill(row, true);

        generateMaze(0, 0);
    }

    private void generateMaze(int r, int c) {
        visited[r][c] = true;

        int[] directions = {0, 1, 2, 3};
        shuffleArray(directions);

        for (int dir : directions) {
            int nr = r, nc = c;

            switch (dir) {
                case 0 -> nr = r - 1; //up
                case 1 -> nc = c + 1; //right
                case 2 -> nr = r + 1; //down
                case 3 -> nc = c - 1; //left
            }

            if (isValid(nr, nc) && !visited[nr][nc]) {
                if (dir == 0) horizontalWalls[r][c] = false;
                if (dir == 1) verticalWalls[r][c + 1] = false;
                if (dir == 2) horizontalWalls[r + 1][c] = false;
                if (dir == 3) verticalWalls[r][c] = false;

                generateMaze(nr, nc);
            }
        }
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && c >= 0 && r < rows && c < cols;
    }

    private void shuffleArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int swap = random.nextInt(array.length);
            int tmp = array[i];
            array[i] = array[swap];
            array[swap] = tmp;
        }
    }

    public boolean[][] getVerticalWalls() { return verticalWalls; }
    public boolean[][] getHorizontalWalls() { return horizontalWalls; }
}
