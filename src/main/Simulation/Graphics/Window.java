package Simulation.Graphics;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import Simulation.Graphics.Board;
import Simulation.Graphics.DriverControls;

/**
 * Container class for all of the graphics. 
 * 
 * Responsible for layout and positional sizing.
 */
public class Window extends JPanel implements ActionListener, ChangeListener {

    protected EventListenerList listenerList = new EventListenerList();
    
    private static final long serialVersionUID = 1L;    
    private JFrame window = new JFrame();

    GridLayout layout = new GridLayout(1, 2, 5, 5);

    private HashMap<String, Object> config = new HashMap<String, Object>();
    
    public Board board;
    public DriverControls controls;

    public Window(Object[][] board) {

        // Load configuration
        this.loadDefaultConfig();                                           // Load the default config.

        // Implement all settings
        Integer size  = (Integer) this.config.get("board_dimension");
        window.add(this);                                                   // add "this" JPanel to the JFrame
        window.setTitle(this.config.get("title").toString());           // give it a title bar
        window.setSize(size, size);                                         // how big is the window?
        window.setLocationRelativeTo(null);                               // place window in the middle of the screen, not relative to any other GUI object
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);              // What happens when you close the window?
        //window.setResizable(false);

        // Set the content panel
        this.setSize(500, 500);
        this.setLayout(this.layout);

        // Initialize and add the board to the content panel.
        this.board = new Board(board);                                      // Initialize the board.
        this.add(this.board);                                               // Add the board to the window.

        this.controls = new DriverControls();                               // Initialize the controls.
        this.controls.addActionListener(this);
        

        this.add(this.controls);                                            // Add the controls to the window.
        
        window.setVisible(true);                                          // Show window.

    }

    public Window(HashMap<String, Object> config) {
        
        this.loadDefaultConfig();                               // Load the default config.

        for (String setting : config.keySet()) {                // Update all of the given settings.

            if (this.config.containsKey(setting)) {

                // Might need for dynamic type casting.
                // Type settingDataType = this.config.get(setting).getClass();
                this.config.put(setting, config.get(setting));
            
            }
        }

        // Implement all settings.
        Integer size  = (Integer) this.config.get("board_dimension");
        window.add(this);                                         // add "this" JPanel to the JFrame
        window.setTitle(this.config.get("title").toString());                                   // give it a title bar
        window.setSize(size, size);                               // how big is the window?
        window.setLocationRelativeTo(null);                     // place window in the middle of the screen, not relative to any other GUI object
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // What happens when you close the window?
        window.setVisible(true);  
    }

    public void loadDefaultConfig() {
        this.config.put("board_dimension", 1500);
        this.config.put("title", "Default Title");
        this.config.put("cell_height", 10);
        this.config.put("cell_width", 10);
    } 

    /**
     * Return the config hashmap for 'this' Board.
     * 
     * Full list of settings:
     * 
     * board_dimension: the length of one side of the board.
     * title: the display title for the board window.
     * 
     * ETC...TODO
     * 
     * @return
     */
    public HashMap<String, Object> getConfig() {
        return this.config;
    }

    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }

    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }

    protected void fireActionPerformed(ActionEvent e) {
        
        Object[] listeners = this.getListeners(ActionListener.class);
     
        for (int i = 0; i < listeners.length; ++i) {
            
            ((ActionListener) listeners[i]).actionPerformed(e);
            
        }
        
    }

    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.fireActionPerformed(e);
    }

    protected void fireChangeOccured(ChangeEvent e) {
        
        Object[] listeners = this.getListeners(ActionListener.class);
        
        for (int i = 0; i < listeners.length; ++i) {
            
            ((ChangeListener) listeners[i]).stateChanged(e);
                
        }
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        this.fireChangeOccured(e);
    }
}
