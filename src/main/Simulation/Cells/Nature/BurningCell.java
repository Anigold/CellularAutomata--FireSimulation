package Simulation.Cells.Nature;

import java.awt.Color;
import Simulation.Cells.Cell;

/** 
 * Representing if a cell is currently burning.
 */
public class BurningCell extends Cell {
    
    public BurningCell(int x, int y) {
        setxPosition(x);
        setyPosition(y);
        setColor(new Color(255, 165, 0));
    }
   
    @Override
    public String toString() {
        return "BurningCell";
    }
    
}
