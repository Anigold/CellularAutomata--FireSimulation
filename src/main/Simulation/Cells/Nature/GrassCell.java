package Simulation.Cells.Nature;

import java.awt.Color;

import Simulation.Cells.Cell;

/**
 * Representing if a cell is grass.
 */
public class GrassCell extends Cell {
    
    public GrassCell(int x, int y) {
        setxPosition(x);
        setyPosition(y);
        setColor(new Color(100, 250, 0));
    }
    
    @Override
    public String toString() {
        return "GrassCell";
    }
}
