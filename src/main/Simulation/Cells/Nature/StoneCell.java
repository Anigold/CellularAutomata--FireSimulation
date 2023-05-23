package Simulation.Cells.Nature;

import java.awt.Color;

import Simulation.Cells.Cell;

/**
 * Representing if a cell is stone.
 */
public class StoneCell extends Cell {

    public StoneCell(int x, int y) {
        setxPosition(x);
        setyPosition(y);
        setColor(new Color(169, 169, 169));
    }

    @Override
    public String toString() {
        return "StoneCell";
    }

}
