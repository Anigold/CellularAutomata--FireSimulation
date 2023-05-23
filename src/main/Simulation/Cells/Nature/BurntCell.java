package Simulation.Cells.Nature;

import java.awt.Color;

import Simulation.Cells.Cell;

/**
 * Representing if a cell has burnt out.
 */
public class BurntCell extends Cell {
    
    public BurntCell(int x, int y) {
        setxPosition(x);
        setyPosition(y);
        setColor(new Color(0, 0, 0));
    }

    @Override
    public String toString() {
        return "BurntCell";
    }
}
