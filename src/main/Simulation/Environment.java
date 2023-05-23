package Simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import Simulation.Cells.Cell;
import Simulation.Cells.Nature.BurningCell;
import Simulation.Cells.Nature.StoneCell;
import Simulation.Cells.Nature.GrassCell;
import Simulation.Cells.Nature.BurntCell;

import Exceptions.InvalidCellPositionException;
import Exceptions.InvalidProbabilityException;

// GUI apps should be added to the JRE event queue
import java.awt.EventQueue;

/**
 * <p>Class to define the physics and structure of the burn simulation.</p>
 * 
 * <p>The board is a square 2D array of cell objects. </p>
 * 
 * <p>Points of initial ignition are chosen upon creation.</p>
 * 
 * <p>The physics of the simulation are leveled against 4 stochastic metrics:</p>
 * 
 * <ol>
 * <li>generativeProbability:             the liklihood of a cell to generate as a burning cell.</li>
 * <li>ignitionProbability:               the liklihood of a burning cell to transition to a burning cell.</li>
 * <li>burnoutProbability:                the liklihood of a burning cell to transition to a burnt cell.</li>
 * <li>resurrectionProbability:           the liklihood of a burnt cell to transition to a burning cell.</li>
 * <li>spontaneousCombustionProbability:  the liklihood of a grass cell to transition to a burning cell.</li>
 * </ol>
 * 
 * <p>Once intial conditions are set, succesive board states are dependant upon the current board state
 * and chance.</p>
 */
public class Environment {
 
    private Cell[][] cellMatrix;            
    private Cell[][] transitionMatrix;

    private int[][] pointsOfArson;          

    private ArrayList<Cell> updatedCells = new ArrayList<Cell>();

    private double generativeProbability;
    private double burnoutProbability;
    private double ignitionProbability;
    private double resurrectionProbability;
    private double spontaneousCombustionProbability;

    /**
     * <p>Construction requires setting a length of one side of the board.</p>
     * 
     * <p>The matrix will be initialized to size, the default pointOfArson and 
     * the default physics will be set.</p>
     * 
     * <p>Lastly, the board will be populated with cells.</p>
     * 
     * @param size integer length of one side of the matrix
     */
    public Environment(int size) {
        
        // Initialize the board.
        this.cellMatrix         = new Cell[size][size];
        this.transitionMatrix   = new Cell[size][size];

        // Generate positions of initial burning cells.
        pointsOfArson = new int[1][2];
        //pointsOfArson[0] = new int[] {size/2, size/2};

        // Initialize physics 
        this.generativeProbability            = 0.50; // % Grass vs Stone
        this.burnoutProbability               = 0.50; 
        this.ignitionProbability              = 0.50;
        this.resurrectionProbability          = 0.50;
        this.spontaneousCombustionProbability = 0.50;

        // Load cells onto board.
        this.loadCells();

    }

    /** 
     *  +=====================+
     *  | Getters and Setters |
     *  +=====================+
    */

    /**
     * Get the ratio of GrassCells to StoneCells
    */
    public double getGenerativeProbability() {
        return generativeProbability;
    }

    /**
     * Set the ratio of GrassCells to StoneCells
     * 
     * @param probability double between 0 and 1
     * @throws InvalidProbabilityException when value is not between 0 and 1
    */
    public void setGenerativeProbability(double probability) throws InvalidProbabilityException {
        if (probability < 0 || probability > 1) {
            throw new InvalidProbabilityException("Probabilties must be between 0 and 100.");
        }
        this.generativeProbability = probability;
        this.loadCells();
    }

    /**
     * Get the probability of a BurningCell transitioning into a BurntCell.
     */
    public double getBurnoutProbability() {
        return burnoutProbability;
    }
       
    /**
     * Set the probability of a BurningCell transitioning into a BurntCell.
     * 
     * @param probability double between 0 and 1
     * @throws InvalidProbabilityException when value is not between 0 and 1
     */
    public void setBurnoutProbability(double probability) throws InvalidProbabilityException {
        if (probability < 0 || probability > 1) {
            throw new InvalidProbabilityException("Probabilties must be between 0 and 100.");
        }
        this.burnoutProbability = probability;
    }

    /**
     * Get the probability of a BurningCell transitioning a GrassCell into a BurningCell.
     */
    public double getIgnitionProbability() {
        return ignitionProbability;
    }

    /**
     * Set the probability of a BurningCell transitioning a GrassCell into a BurningCell.
     * 
     * @param probability double between 0 and 1
     * @throws InvalidProbabilityException when value is not between 0 and 1
     */
    public void setIgnitionProbability(double probability) throws InvalidProbabilityException {
        if (probability < 0 || probability > 1) {
            throw new InvalidProbabilityException("Probabilties must be between 0 and 100.");
        }
        this.ignitionProbability = probability;
    }

    /**
     * Get the probability of a BurntCell transitioning into a GrassCell.
     */
    public double getResurrectionProbability() {
        return resurrectionProbability;
    }

    /**
     * Set the probability of a BurntCell transitioning into a GrassCell.
     * 
     * @param probability double between 0 and 1
     * @throws InvalidProbabilityException when value is not between 0 and 1
     */
    public void setResurrectionProbability(double probability) throws InvalidProbabilityException {
        if (probability < 0 || probability > 1) {
            throw new InvalidProbabilityException("Probabilties must be between 0 and 100.");
        }
        
        this.resurrectionProbability = probability;
    }

    /**
     * Set the probability of a GrassCell transitioning into a BurningCell.
     * 
     * @param probability double between 0 and 1
     * @throws InvalidProbabilityException when value is not between 0 and 1
    */
    public double getSpontaneousCombustionProbability() {
        return spontaneousCombustionProbability;
    }

    /**
     * Set the probability of a GrassCell transitioning into a BurningCell.
     * 
     * @param probability double between 0 and 1
     * @throws InvalidProbabilityException when value is not between 0 and 1
    */
    public void setSpontaneousCombustionProbability(double probability) throws InvalidProbabilityException {
        if (probability < 0 || probability > 1) {
            throw new InvalidProbabilityException("Probabilties must be between 0 and 100.");
        }
        this.spontaneousCombustionProbability = probability;
        
    }
    
    /**
     * Get the name and values of all probabilities.
     * 
     * @return HashMap<String, Double> = { name : value }
    */
    public HashMap<String, Double> getProbabilities() {
        HashMap<String, Double> probabilities = new HashMap<String, Double>();

        probabilities.put("Burnout",        this.burnoutProbability                 );
        probabilities.put("Resurrection",   this.resurrectionProbability            );
        probabilities.put("Ignition",       this.ignitionProbability                );
        probabilities.put("Generation",     this.generativeProbability              );
        probabilities.put("Combustion",     this.spontaneousCombustionProbability   );

        return probabilities;
    }
    
    /**
     * Set the value of a probability based on name.
     * 
     * To be used in conjunction with this.getProbabilities().
     * 
     * @param name String representation of the probability to be changed
     * @param value Double value of the probability, must be between 0 and 1
     * @throws InvalidProbabilityException when value is not between 0 and 1
    */
    public void setProbability(String name, double value) throws InvalidProbabilityException {
        try{
            switch (name) {
                case "Burnout":
                    this.setBurnoutProbability(value);
                    break;
                case "Resurrection":
                    this.setResurrectionProbability(value);
                    break;
                case "Ignition":
                    this.setIgnitionProbability(value);
                    break;
                case "Generation":
                    this.setGenerativeProbability(value);
                    break;
                case "Combustion":
                    this.setSpontaneousCombustionProbability(value);
                    break;
            }
        } catch (InvalidProbabilityException ipe) {
            throw new InvalidProbabilityException(ipe.getMessage());
        }
    }

    /**
     * Get the value of a probability based on name.
     * 
     * To be used in conjunction with this.getProbabilities()
     * 
     * @param name String representation of the probability
     * @return double value of the probablity, will be between 0 and 1
     */
    public double getProbability(String name) {
        return getProbabilities().get(name);
    }

    /**
     * Get the "live" board.
     * 
     * @return 2D array of cell objects
     */
    public Cell[][] getCellMatrix() {
        return this.cellMatrix;
    }
    
    /**
     * Get the transitionMatrix.
     * 
     * @return 2D array of Cell objects
     */
    public Cell[][] getTransitionMatrix() {
        return this.transitionMatrix;
    }
   
    /**
     * Grabs cell located at the given (x, y) position in cellMatrix.
     * 
     * @param x int of the x position
     * @param y int of the y position
     * @return Cell object
     * @throws InvalidCellPositionException if the given position is invalid 
     */
    public Cell getCell(int x, int y) {

        if (cellExists(x, y)) {
            return this.cellMatrix[y][x];
        }
        return null; // This shouldn't ever be reached.

    }

    /**
     * Set cell at position (x,y) on cellMatrix to the given Cell object.
     * 
     * @param x int value of x-position
     * @param y int value of y-position
     * @param newCell Cell object to be placed in cellMatrix
    */
    public void setCell(int x, int y, Cell newCell) {
        if (cellExists(x, y)) {
            this.cellMatrix[y][x] = newCell;
            this.transitionMatrix[y][x] = newCell;
        }
    }

    public void setCell(Cell newCell) {
        
        int xPos = newCell.getxPosition();
        int yPos = newCell.getyPosition();

        if (cellExists(xPos, yPos)) {
            this.cellMatrix[yPos][xPos] = newCell;
            this.transitionMatrix[yPos][xPos] = newCell;
        }
    }
    /**
     * Checks to make sure the given (x, y) position is valid.
     * 
     * Assumes all positions in the Environment are a Cell.
     * 
     * @param x int of the x position
     * @param y int of the y position
     * @return true if the position is within the bounds of the board
     * @throws InvalidCellPositionException if the position is outside the bounds of the board.
     */
    public boolean cellExists(int x, int y){
        return x <= this.cellMatrix.length 
            && y <= this.cellMatrix.length;
    }

    /**+=========+
     * | Helpers |
     * +=========+
    */

    /**
     * Loads the cellMatrix and transitionMatrix with Cells.
     * 
     * Assigns either GrassCell or StoneCell based on the generativeProbability.
     */
    private void loadCells() {
        
        for (int i = 0; i < this.cellMatrix.length; ++i) {
            
            Cell[] cellRow = this.cellMatrix[i];

            for (int j = 0; j < cellRow.length; ++j) {

                Cell cell = (Math.random() < this.generativeProbability) ? new GrassCell(j, i) : new StoneCell(j, i); 
                
                this.cellMatrix[i][j] = cell;
                this.transitionMatrix[i][j] = cell;
                
            }
        }    
    }

    /**
     * <p> Returns the von Neumann neighborhood of given radius, r. </p>
     * 
     * <p> Positions in the array correspond to the respective neighborhood
     * based on the following top-down model (in this case r = 2): </p>
     * <p><blockquote><pre>       
     * Ex:    
     *            +----+           
     *            | 01 |           
     *       +----+----+----+      
     *       | 02 | 03 | 04 |      
     *  +----+----+----+----+----+ 
     *  | 05 | 06 | 07 | 08 | 09 | 
     *  +----+----+----+----+----+ 
     *       | 10 | 11 | 12 |      
     *       +----+----+----+      
     *            | 13 |           
     *            +----+           
     * </pre></blockquote><p>
     * <p>Where (in this case) 07 is the supplied cell. 
     * @param centerCell the Cell at the center of the neighborhood
     * @param r integer value of the radius of the neighborhood
     * @return array of Cell instances
     */
    public Cell[] getVanNeumannNeighborhood(Cell centerCell, int r) {

        Cell[] neighborhood = new Cell[13];

        // Center of neighborhood
        int xPos    = centerCell.getxPosition();
        int yPos    = centerCell.getyPosition();

        // Since we are in the 4th quadrant, downward is increasing value.
        int yStart  = Math.max(yPos - r, 0);                              // Ensure we don't move past the top border.
        int yEnd    = Math.min(yPos + r + 1, this.cellMatrix.length);       // Ensure we don't move past the bottom border.

        // Horizontal movement is maintained increasing from left to right.
        int xStart  = Math.max(xPos - r, 0);                              // Ensure we don't move past the left border.
        int xEnd    = Math.min(xPos + r + 1, this.cellMatrix[0].length);    // Ensure we don't move past the right border.


        int neighborCount = 0;
        for (int j = yStart; j < yEnd; ++j) {                               // Starting at the top of the neighborhood.

            for (int i = xStart; i < xEnd; ++i) {                           // Move along x-axis.

                int distance = Math.abs(yPos - j) + Math.abs(xPos - i);     // Determine relative distance from center point.

                if (distance <= r) {                                        // If that distance falls within the given radius
                    neighborhood[neighborCount] = getCell(i, j);            // Add the cell to the neighborhood.
                    neighborCount++;                                        // Move the pointer to the next neighbor slot.           
                }
            }
        }
        return neighborhood;                                                   // Return neighborhood.
    }

    /**
     * <p>Defines the transitions for each kind of cell.</p>
     * 
     * <p>Note:
     * <p>I opted for this setup rather than handing the transition protocols to each cell because I wanted the cells
     * to be decoupled from one another. I'd also maintain that this saves on considerable computational space
     * as there can possibly be 100,000+ cells at any one time, and thus just as many copies of the transitions.</p>
     * 
     * @param currentCell the Cell to be transitioned
     */
    public void transitionProtocol(Cell currentCell) {

        int xPos = currentCell.getxPosition();
        int yPos = currentCell.getyPosition();

        Cell[] neighbors;
        switch (currentCell.toString()) {
            
            case "BurningCell":
                
                // Roll to see if the BurningCell will transition to a BurntCell
                if (burnoutRoll()) {
                    this.transitionMatrix[yPos][xPos] = new BurntCell(xPos, yPos);
                    return;
                }

                // Get the neighborhood and roll to see if we should traverse the array
                // forwards or backwards.
                neighbors = getVanNeumannNeighborhood(currentCell, 2);
                boolean forward = Math.random() <= .50;

                // TODO: Generalize this, complete and utter DRY failure...
                if (forward) {
                    for (int i = 0; i < neighbors.length; ++i) {

                        // We only break here because we know the neighborhood will group
                        // the null values together at the end of the array.
                        if (neighbors[i] == null) {
                            break;
                        }
                        // If the neighbor is not a GrassCell, skip over it.
                        if (neighbors[i].toString() != "GrassCell") {
                            continue;
                        }
                        
                        // Roll to see if the GrassCell neighbor will transition to a BurningCell.
                        if (ignitionRoll()) {

                            int neighborX = neighbors[i].getxPosition();
                            int neighborY = neighbors[i].getyPosition();
                            
                            this.transitionMatrix[neighborY][neighborX] = new BurningCell(neighborX, neighborY);
                            break; // We only allow one neighbor to catch per check.
                        }
                    }
                } else {
                    for (int i = neighbors.length-1; i > 0; --i) {

                        // We only break here because we know the neighborhood will group
                        // the null values together at the end of the array.
                        if (neighbors[i] == null) {
                            break;
                        }
                        // If the neighbor is not a GrassCell, skip over it.
                        if (neighbors[i].toString() != "GrassCell") {
                            continue;
                        }

                        int neighborX = neighbors[i].getxPosition();
                        int neighborY = neighbors[i].getyPosition();

                        // Roll to see if the GrassCell neighbor will transition to a BurningCell.
                        if (ignitionRoll()) {
                            this.transitionMatrix[neighborY][neighborX] = new BurningCell(neighborX, neighborY);
                            break; // We only allow one neighbor to catch per check.
                        }
                    }
                }

                // Roll to see if the BurningCell will transition to a BurntCell
                if (burnoutRoll()) {
                    this.transitionMatrix[yPos][xPos] = new BurntCell(xPos, yPos);
                    return;
                }

                this.transitionMatrix[yPos][xPos] = new BurningCell(xPos, yPos); // If it made it here, then it stays a BurningCell.
                return;
            
            case "GrassCell":
                
                // If the cell has already changed this cycle, then we move on.
                if (this.cellMatrix[yPos][xPos].getClass() != this.transitionMatrix[yPos][xPos].getClass()) {
                    return;
                }
                
                // Roll to see if it spontaneously combusts.
                /** 
                 * DISABLED IN EXCHANGE FOR RANDOM SINGLE FIRES
                if (combustionRoll()) {
                    this.transitionMatrix[yPos][xPos] = new BurningCell(xPos, yPos);
                    return;
                } 
                */

                this.transitionMatrix[yPos][xPos] = new GrassCell(xPos, yPos);
                return;
            
            case "BurntCell":

                // Roll to see if the BurntCell transitions to a GrassCell.
                if (resurrectionRoll()) {
                    this.transitionMatrix[yPos][xPos] = new GrassCell(xPos, yPos);
                    return;
                } else {
                    this.transitionMatrix[yPos][xPos] = new BurntCell(xPos, yPos);
                    return;
                }

            case "StoneCell":

                // The StoneCell should never change.
                this.transitionMatrix[yPos][xPos] = new StoneCell(xPos, yPos);

            default:
                // This should never be reached.
                return;
        }
    }

    /**
     * 
     * NEEDS FURTHER IMPROVEMENT UNTIL VIABLE FOR USE.
     * 
     * Currently hindered by needing to check if a cell has been 
     * changed already. 
     * 
     * The original method only requires a hashmap lookup, O(1).
     * This method could theoretically require a O(2*cellMatrix.length) lookup time. 
     * 
     * The space saved doesn't compensate the slower rate of lookup.
     * 
     * @param currentCell
     */
    public void newTransitionProtocol(Cell currentCell) {
        int xPos = currentCell.getxPosition();
        int yPos = currentCell.getyPosition();

        Cell[] neighbors;
        switch (currentCell.toString()) {

            case "BurningCell":
                
                // Roll to see if the BurningCell will transition to a BurntCell
                if (burnoutRoll()) {
                    this.updatedCells.add(new BurntCell(xPos, yPos));
                    return;
                }

                // Get the neighborhood and roll to see if we should traverse the array
                // forwards or backwards.
                neighbors = getVanNeumannNeighborhood(currentCell, 2);
                boolean forward = Math.random() <= .50;

                // TODO: Generalize this, complete and utter DRY failure...
                if (forward) {
                    for (int i = 0; i < neighbors.length; ++i) {

                        // We only break here because we know the neighborhood will group
                        // the null values together at the end of the array.
                        if (neighbors[i] == null) {
                            break;
                        }
                        // If the neighbor is not a GrassCell, skip over it.
                        if (neighbors[i].toString() != "GrassCell") {
                            continue;
                        }
                        
                        // Roll to see if the GrassCell neighbor will transition to a BurningCell.
                        if (ignitionRoll()) {

                            int neighborX = neighbors[i].getxPosition();
                            int neighborY = neighbors[i].getyPosition();
                            
                            this.updatedCells.add(new BurningCell(neighborX, neighborY));
                            
                            break; // We only allow one neighbor to catch per check.
                        }
                    }
                } else {
                    for (int i = neighbors.length-1; i > 0; --i) {

                        // We only break here because we know the neighborhood will group
                        // the null values together at the end of the array.
                        if (neighbors[i] == null) {
                            break;
                        }
                        // If the neighbor is not a GrassCell, skip over it.
                        if (neighbors[i].toString() != "GrassCell") {
                            continue;
                        }

                        int neighborX = neighbors[i].getxPosition();
                        int neighborY = neighbors[i].getyPosition();

                        // Roll to see if the GrassCell neighbor will transition to a BurningCell.
                        if (ignitionRoll()) {
                            this.updatedCells.add(new BurningCell(neighborX, neighborY));
                            break; // We only allow one neighbor to catch per check.
                        }
                    }
                }

                return;
            
            case "GrassCell":
                
                
                
                // Roll to see if it spontaneously combusts.
                /** 
                 * DISABLED IN EXCHANGE FOR RANDOM SINGLE FIRES
                if (combustionRoll()) {
                    this.transitionMatrix[yPos][xPos] = new BurningCell(xPos, yPos);
                    return;
                } 
                */
                return;
            
            case "BurntCell":

                // Roll to see if the BurntCell transitions to a GrassCell.
                if (resurrectionRoll()) {
                    this.updatedCells.add(new GrassCell(xPos, yPos));
                }

                return;

            case "StoneCell":

                // The StoneCell should never change.
                return;

            default:
                // This should never be reached.
                return;
        }
    }
  
    /**
     * Update the cellMatrix based on the transition protocols.
     */
    public void implementTransitions() {

        boolean somethingBurning = false;

        for (Cell[] cellRow : this.cellMatrix) {

            for (Cell cell : cellRow) {

                if (cell.toString() == "BurningCell") {
                    somethingBurning = true;
                }

                // If the cell was already changed this sweep, skip over it.
                /**
                 * Removed functionality
                if (cell.getChangedStatus() == true) {
                    cell.toggleChangeStatus();
                    continue;
                }
                */
                this.transitionProtocol(cell);

            }
        }

        this.cellMatrix = this.transitionMatrix;

        /**
        for (Cell cell : updatedCells) {
            this.setCell(cell);
        }
         */
     
        if (!somethingBurning && combustionRoll()) {
            
            int randomX = (int) (Math.random() * this.cellMatrix.length); // Some random x-value within the board boundaries.
            int randomY = (int) (Math.random() * this.cellMatrix.length); // Some random y-value within the board boundaries.

            this.cellMatrix[randomY][randomX] = new BurningCell(randomX, randomY);
        }

    }

    /**
     * Transition random GrassCell to BurningCell.
     */
    public void setFire() {

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        int randomX = rand.nextInt(this.cellMatrix.length);
        int randomY = rand.nextInt(this.cellMatrix.length);

        if (this.getCell(randomX, randomY).getClass() == GrassCell.class) {
            this.setCell(randomX, randomY, new BurningCell(randomX, randomY));
            return;
        }

        this.setFire();
    }

    /**
     * Transition the cell at position (x,y) to BurningCell.
     * 
     * @param x
     * @param y
     */
    public void setFire(int x, int y) {
  
        if (this.cellExists(x, y)) {
            this.setCell(x, y, new BurningCell(x, y));
        }

    }


    /**
     * +===================+
     * | Probability Rolls |
     * +===================+
     */

    /**
     * Roll against the burnoutProbability.
     * 
     * @return boolean
     */
    public boolean burnoutRoll() {
        return Math.random() < this.burnoutProbability;
    }

    /**
     * Roll against the ignitionProbability.
     * 
     * @return boolean
     */
    public boolean ignitionRoll() {
        boolean roll = Math.random() < this.ignitionProbability;
        return roll;
    }

    /**
     * Roll against the combustionProbability.
     * 
     * @return boolean
     */
    public boolean combustionRoll() {

        // To keep it rare, it has to roll 2 times in a row.
        for (int i = 0; i < 5; ++i) {
            if (Math.random() > this.spontaneousCombustionProbability) {
                return false;
            }
        }
        return true;
    }

    /**
     * Roll against the resurrectionProbability.
     * 
     * @return boolean
     */
    public boolean resurrectionRoll() {
        return Math.random() < this.resurrectionProbability;
    }

    /**
     * Roll against the generativeProbability.
     * 
     * @return boolean
     */
    public boolean generationRoll() {
        return Math.random() < this.generativeProbability;
    }
}
