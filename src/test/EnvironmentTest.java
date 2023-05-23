package test;

import Simulation.Environment;
import Simulation.Cells.Cell;
import Simulation.Cells.Nature.BurningCell;
import Simulation.Cells.Nature.BurntCell;
import Simulation.Cells.Nature.GrassCell;
import Simulation.Cells.Nature.StoneCell;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.management.remote.TargetedNotification;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.swing.tree.TreeNode;

import org.junit.Test;

import Exceptions.InvalidProbabilityException;


public class EnvironmentTest {
        
    Environment environment;

    public void setup() {
        environment = null;
    }

    @Test
    public void constructorTest() {

        setup();

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        
        for (int i = 0; i < 10; ++i) {

            int size = rand.nextInt(500);

            // Ensure cellMatrix is initialized with proper size
            environment = new Environment(size);
            assertTrue(environment.getCellMatrix().length                   == size);
            assertTrue(environment.getCellMatrix()[0].length                == size);

            // Ensure probabilites are all 50%
            assertTrue(environment.getBurnoutProbability()                  == .5);
            assertTrue(environment.getGenerativeProbability()               == .5);
            assertTrue(environment.getIgnitionProbability()                 == .5);
            assertTrue(environment.getResurrectionProbability()             == .5);
            assertTrue(environment.getSpontaneousCombustionProbability()    == .5);

            // Ensure board has been initialized
            Cell[][] board = environment.getCellMatrix();
            for (Cell[] cellRow : board) {
                for (Cell cell : cellRow) {
                    assertTrue(cell != null);
                }
            }

        }

        


        
    }

    @Test
    public void setProbabilitiesTest() {

        setup();
        environment = new Environment(500);
        HashMap<String, Double> probabilites = environment.getProbabilities();

        for (String probability : probabilites.keySet()) {

            double tooHighValue = 46.1;
            Exception probabilityTooHigh = assertThrows( InvalidProbabilityException.class, () -> {
                environment.setProbability(probability, tooHighValue);
            });
            assertTrue( probabilityTooHigh.getClass().equals(InvalidProbabilityException.class));
            
            double tooLowValue = -.49;
            Exception probabilityTooLow = assertThrows( InvalidProbabilityException.class, () -> {
                environment.setProbability(probability, tooLowValue);
            });
            assertTrue( probabilityTooLow.getClass().equals(InvalidProbabilityException.class));
            
            double justRightValue = .64;
            try {
                environment.setProbability(probability, justRightValue);
                assertTrue(environment.getProbability(probability) == justRightValue);
            } catch (Exception e) {
                fail();
            }
        }
    }

    @Test
    public void neighborhoodTest() {

        setup();
        environment = new Environment(500);

        Cell[][] cellMatrix = environment.getCellMatrix();
        Cell cell;
        Cell[] neighborhood;

        // Correct positions
        cell = cellMatrix[50][50];
        neighborhood = environment.getVanNeumannNeighborhood(cell, 2);

        int[][] positions = {
            { 0, -2},
            {-1, -1},
            { 0, -1},
            { 1, -1},
            {-2,  0},
            {-1,  0},
            { 0,  0},
            { 1,  0},
            { 2,  0},
            {-1,  1},
            { 0,  1},
            { 1,  1},
            { 0,  2},
        };

        for (int i = 0; i < neighborhood.length; ++i) {

            int relativeX = neighborhood[i].getxPosition() - cell.getxPosition();
            int relativeY = neighborhood[i].getyPosition() - cell.getyPosition();

            int expectedX = positions[i][0];
            int expectedY = positions[i][1];

            assertTrue(relativeX == expectedX);
            assertTrue(relativeY == expectedY);
        }
        
        // All neighbors
        cell = cellMatrix[30][30];
        neighborhood = environment.getVanNeumannNeighborhood(cell, 2);

        for (Cell neighbor : neighborhood) {
            assertTrue(neighbor != null);
        }

        // Null edges
        cell = cellMatrix[0][0];
        neighborhood = environment.getVanNeumannNeighborhood(cell, 2);

        int nonNulls = 0;
        for (Cell neighbor : neighborhood) {
            if (neighbor == null) {
                continue;
            }
            ++nonNulls;
        }
        assertTrue(nonNulls == 6);

    }

    @Test
    public void transitionTest() {

        setup();

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        // StoneCell never changes
        for (int i = 0; i < 11; ++i) {

            environment = new Environment(500);
            createAbsolutes();
            
            int randomX = rand.nextInt(450);
            int randomY = rand.nextInt(450);

            // Stone cell never changes
            StoneCell stoneCell = new StoneCell(randomX, randomY);                          // Create a new StoneCell
            environment.setCell(randomX, randomY, stoneCell);                               // Place it on the matrix

            environment.transitionProtocol(environment.getCell(randomX, randomY));          // Run the transition protocol

            Cell[][] transionMatrix = environment.getTransitionMatrix();                    // Grab the cell from the transitionMatrix

            assertTrue(transionMatrix[randomY][randomX].getClass() == StoneCell.class);     // Ensure it hasn't changed
        }
       
        // BurningCell burns out
        for (int i = 0; i < 11; ++i) {

            environment = new Environment(500);
            createAbsolutes();

            int randomX = rand.nextInt(450);
            int randomY = rand.nextInt(450);

            BurningCell burningCell = new BurningCell(randomX, randomY);                    // Create a new BurningCell
            environment.setCell(randomX, randomY, burningCell);                             // Place it on the matrix

            environment.transitionProtocol(environment.getCell(randomX, randomY));          // Run the transition protocol

            Cell[][] transionMatrix = environment.getTransitionMatrix();                    // Grab the cell from the transitionMatrix

            assertTrue(transionMatrix[randomY][randomX].getClass() == BurntCell.class);     // Ensure it has transitioned to BurntCell
        }

        // BurningCell catches GrassCell neighbor
        for (int i = 0; i < 11; ++i) {

            environment = new Environment(500);

            try {
                environment.setIgnitionProbability(1);                          // Ensure that one of the cells will catch on fire
                environment.setSpontaneousCombustionProbability(0);             // Ensure none of the cell will combust of their own accord
                environment.setBurnoutProbability(0);                           // Ensure the BurningCell won't burn out before setting a neighbor on fire
            } catch (Exception e) {
                fail("TESTING ERROR: The creator of this test screwed up.");
            }
            
            int randomX = rand.nextInt(450);
            int randomY = rand.nextInt(450);

            BurningCell burningCell = new BurningCell(randomX, randomY);                    // Create a new BurningCell
            environment.setCell(randomX, randomY, burningCell);                             // Place it on the matrix

            Cell[] neighbors = environment.getVanNeumannNeighborhood(burningCell, 2);     // Get its neighboring cells
            for (int j = 0; j < neighbors.length; ++j) {                                    // Turn them all to GrassCells

                if (neighbors[j] == null) {
                    continue;
                }
                int xPos = neighbors[j].getxPosition();
                int yPos = neighbors[j].getyPosition();

                if (xPos == randomX && yPos == randomY) {
                    continue;
                }

                GrassCell grassCell = new GrassCell(xPos, yPos);
                environment.setCell(xPos, yPos, grassCell);
            }                            

            environment.implementTransitions();                                             // Implement the transition.
             
            neighbors = environment.getVanNeumannNeighborhood(burningCell, 2);            // Check the neighborhood after transitions.

            int numberOfBurning = 0;
            for (Cell cell : neighbors) {

                if (cell == null) {
                    continue;
                }

                if (cell.getClass() == BurningCell.class) {
                    numberOfBurning++;
                }
            }

            assertTrue(numberOfBurning == 2);                                               // Ensure exactly 2 cells are Burning, the original and one other.
                                                  
        }

        // BurningCell doesn't catch StoneCell
        for (int i = 0; i < 11; ++i) {

            environment = new Environment(500);
            
            try {
                environment.setIgnitionProbability(1);                          // Ensure that one of the cells will catch on fire
                environment.setSpontaneousCombustionProbability(0);             // Ensure none of the cell will combust of their own accord
                environment.setBurnoutProbability(0);                           // Ensure the BurningCell won't burn out before setting a neighbor on fire
                environment.setResurrectionProbability(0);
            } catch (Exception e) {
                fail("TESTING ERROR: The creator of this test screwed up.");
            }
            
            int randomX = rand.nextInt(450);
            int randomY = rand.nextInt(450);

            BurningCell burningCell = new BurningCell(randomX, randomY);                    // Create a new BurningCell
            environment.setCell(randomX, randomY, burningCell);                             // Place it on the matrix

            Cell[] neighbors = environment.getVanNeumannNeighborhood(burningCell, 2);     // Get its neighboring cells
            for (int j = 0; j < neighbors.length; ++j) {                                    // Turn them all to StoneCells

                if (neighbors[j] == null) {
                    continue;
                }
                int xPos = neighbors[j].getxPosition();
                int yPos = neighbors[j].getyPosition();

                if (xPos == randomX && yPos == randomY) {
                    continue;
                }

                StoneCell stoneCell = new StoneCell(xPos, yPos);
                environment.setCell(xPos, yPos, stoneCell);
                
            }                            
           
            environment.implementTransitions();
            neighbors = environment.getVanNeumannNeighborhood(burningCell, 2);

            for (Cell cell : neighbors) {

                if (cell == null) {
                    continue;
                }

                if (cell.getxPosition() == randomX && cell.getyPosition() == randomY) {
                    continue;
                }

                assertTrue(cell.getClass() == StoneCell.class);
            }
            
        }

        // Grass cell spontaneously combusts into BurntCell
        /**
         * This was deprecated, testing is obsolete.
         
        for (int i = 0; i < 11; ++i) {
            environment = new Environment(500);
            
            try {
                environment.setIgnitionProbability(0);                          // Ensure that no cells will ignite.
                environment.setSpontaneousCombustionProbability(1);             // Ensure the cell will combust of its own accord
                environment.setBurnoutProbability(0);                           
                environment.setResurrectionProbability(0);
                environment.setGenerativeProbability(.50);
            } catch (Exception e) {
                fail("TESTING ERROR: The creator of this test screwed up.");
            }
            
            int randomX = rand.nextInt(450);
            int randomY = rand.nextInt(450);

            GrassCell grassCell = new GrassCell(randomX, randomY);                          // Create a new BurningCell
            environment.setCell(randomX, randomY, grassCell);                               // Place it on the matrix
            System.out.println(environment.getCell(randomX, randomY));
            environment.implementTransitions();

            System.out.println(environment.getCell(randomX, randomY).getClass());
            assertTrue(environment.getCell(randomX, randomY).getClass() == BurningCell.class);
        }
   
        // BurntCell resurrects into GrassCell
        for (int i = 0; i < 11; ++i) {
            environment = new Environment(500);
            
            try {
                environment.setIgnitionProbability(0);                          // Ensure that no cells will ignite.
                environment.setSpontaneousCombustionProbability(0);             // Ensure the cell will combust of its own accord
                environment.setBurnoutProbability(0);                           
                environment.setResurrectionProbability(1);
                environment.setGenerativeProbability(.50);
            } catch (Exception e) {
                fail("TESTING ERROR: The creator of this test screwed up.");
            }
            
            int randomX = rand.nextInt(450);
            int randomY = rand.nextInt(450);

            BurntCell burntCell = new BurntCell(randomX, randomY);                          // Create a new BurntCell
            environment.setCell(randomX, randomY, burntCell);                               // Place it on the matrix
            
            environment.implementTransitions();                                             // Implement transitions

            assertTrue(environment.getCell(randomX, randomY).getClass() == GrassCell.class);
        }
        */
    }


    private void createAbsolutes() {
        HashMap<String, Double> probabilites = this.environment.getProbabilities();
        for (String probablity : probabilites.keySet()) {
            try {
                this.environment.setProbability(probablity, 1);
            } catch (Exception e) {
                fail("Testing failure");
            }
        }
    }
}