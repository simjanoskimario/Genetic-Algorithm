# Genetic-Algorithm
A simple genetic algorithm. A number of little dudes called "Brians" are tasked to navigate a maze, the algorithm selects for better Brians and strikes a good balance (or at least tries to) between exploration and reaching the opposite side of the maze. (The project name says "Sequential", but it is not, a different thread handles evolution.)

# Evolution Environment
<p>
  The Program launches a simple java swing window that draws a maze with black walls, the Brians are dots that use their own Vector2 class to move. After time runs out, all Brians are evaluated based off their final position, where they land on a flood-fill matrix, how close they are to the bottom right, and heavily penalized for sitting in one spot, making extremely precise micro movements and of course, if they aren't close to the goal. Every generation after the first has their moves smoothed, and the genes carry over through tournament selection and splitting the paths 3 different parents take into equal segments that get fitted into each Brian.
</p>
<p align="center">
  <img src="./assets/InitialGenerations.gif" alt="Initial Generation" /><br>
  This is what the initial generations look like, at the < 10 generation mark.
</p>
<p align="center">
  <img src="./assets/EvolvedFurther.gif" alt="Evolved Further" /><br>
  This is what the Brians look like after hundreds of generations have passed.
</p>
<p>
  The initial values the program starts with are 1000 Brians, 1000 generations. Each Brian has 1000 steps each, the amount of times they hit a wall is counted for fitness, and their maximum distance away from the starting point is also preserved within each Brian.
</p>
