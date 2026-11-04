import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Brian {

    public int stepCount = 1000;
    public double maxDistSq;
    Set<String> uniqueCellsVisited = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());
    public int stepReachedMaxDistance = 0;
    private int slotSize = 50;
    public double fitness = 0;
    public Vector2D position;
    public Vector2D finalPosition = new Vector2D(1,1);
    public int hitCount;
    public Vector2D[] movement = new Vector2D[stepCount];

    public Brian(){
        for(int i = 0; i < movement.length; i++){
            Random rand = new Random();
            this.movement[i] = new Vector2D(new Random().nextInt(-5, 6), new Random().nextInt(-5, 6));

        }
        this.position = new Vector2D(10,10);
    }
    public Brian( Vector2D[] movement){
        System.arraycopy(movement, 0, this.movement, 0, movement.length);
        this.position = new Vector2D(10,10);
    }
}
