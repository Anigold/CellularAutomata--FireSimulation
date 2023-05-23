package Simulation.Graphics;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Class responsible for painting the Environment board to the screen.
 * 
 * Generalizes the Environment cell matrix from a 2D array of Cell objects
 * to just a 2D array of generic objects.
 * 
 * The board assumes the colors of the cells are determined by the specific
 * object's getColor() method.
 * 
 * So you should probably have that method in your object's class definition.
 * Or re-write this one to deal with it however you see fit.
 */
public class Board extends JPanel {

    private Object[][] board;
    private int cellSize;

    /**
     * Default constructor.
     * 
     * @param board 2D array of Objects.
     */
    public Board(Object[][] board) {
        this.board = board;
        this.setDefaults();
    }
    
    /**
     * Construct board at specific resolution.
     * 
     * @param board 2D array of Objects.
     * @param cellSize desired pixel-length of cells
     */
    public Board(Object[][] board, int cellSize) {
        this.board = board;
        this.cellSize = cellSize;
    }

    /**
     * Get the board.
     * 
     * @return this.board
     */
    public Object[][] getBoard() {
        return this.board;
    }

    /**
     * Set the board to the given newBoard.
     * 
     * @param newBoard 2D array of objects.
     */
    public void setBoard(Object[][] newBoard) {
        this.board = newBoard;
    }
    
    /**
     * Should never need to call this. Passes in the current graphics 
     * object to update with whatever protocols found in doDrawing().
     */
    @Override
    public void paintComponent(Graphics g) {
        
        try {
            super.paintComponent(g);
            doDrawing(g);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }         

    }

    /**
     * Method responsible for what gets painted to the screen.
     * 
     * Pass in the current graphics and update the colors based on
     * the new board state.
     * 
     * @param graphics
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void doDrawing(Graphics graphics) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Graphics2D g2d = (Graphics2D) graphics; 

        int x = 0, y = 0;

        try {
            
            Method cellMethod = this.board[0][0].getClass().getMethod("getColor");

            for ( Object[] cellRow : this.board ) {

                for ( Object cell : cellRow ) {
                    
                    Color cellColor = (Color) cellMethod.invoke(cell);
                    g2d.setColor(cellColor);
                    g2d.fillRect(x, y, this.cellSize, this.cellSize);
    
                    x += this.cellSize;
                }
    
                y += this.cellSize;
                x = 0;
    
            }

        } catch (NoSuchMethodException nsme) {
            throw new NoSuchMethodException("Board environment does not have contain a getCellMatrix() method.");
        } catch (IllegalAccessException iae) {
            throw new IllegalAccessException("Cannot access target method.");
        } catch (InvocationTargetException ite) {
            throw new InvocationTargetException(ite.getTargetException());
        }
        
    }

    /**
     * Defines the default settings for the board.
     * 
     * Cell size is determined by the proportional sizes of the 
     * screen and board.
     * 
     */
    private void setDefaults() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim = toolkit.getScreenSize();

        this.cellSize = (dim.width / 2) / this.board.length;
        if (this.cellSize == 1) {
            this.cellSize = 2; // Yes this is ugly, no I don't want to do all of the math yet
        }

    }

    
}
