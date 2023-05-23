package Simulation.Cells;
import java.awt.Color;
import java.util.HashMap;

/**
 * Abstract class for a cell.
 * 
 * Maintains knowledge about its color and (x,y) position.
 * 
 */
public abstract class Cell {

    private int xPosition;
    private int yPosition;

    private Color rgb; 

    public boolean changed = false;
    
    public void setColor(Color color) {
        this.rgb = color;
    }

    public Color getColor() {
        return rgb;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * Decided this was too coupled to the Environment.

    public boolean getChangedStatus() {
        return this.changed;
    }

    public void markChanged() {
        this.changed = true;
    }

    public void markNotChanged() {
        this.changed = false;
    }

    public void toggleChangeStatus() {
        this.changed = !(this.changed);
    }
    */
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + xPosition;
        result = prime * result + yPosition;
        result = prime * result + ((rgb == null) ? 0 : rgb.hashCode());
        return result;
    }
    
}