import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Exceptions.InvalidProbabilityException;
import Simulation.Environment;
import Simulation.Graphics.Window;

/**
 * Controller class; middlemans between Environment and Graphics.
 * 
 */
public class Driver implements ActionListener, ChangeListener {
    
    public  Environment  environment;
    public  Window       window;
    
    private boolean      isRunning = true;

    private Timer        timer;

    /**
     * Send updated probability values to environment to use.
     * 
     * @param name String representation of probability
     * @param value int value of probability (from 1 - 100)
     */
    public void updateEnvironmentProbabilities(String name, int value) {
        
        try {
            switch (name) {
                case "Grass Generation Ratio":
                    this.environment.setGenerativeProbability((double) value/100);
                    return;
                case "Burnout Probablity":
                    this.environment.setBurnoutProbability( (double) value/100);
                    return;
                case "Ignition Probability":
                    this.environment.setIgnitionProbability( (double) value/100);
                    return;
                case "Resurrection Probability":
                    this.environment.setResurrectionProbability( (double) value/100);
                    return;
                case "Spontaneous Combustion Probability":
                    this.environment.setSpontaneousCombustionProbability( (double) value/100);
                    return;
                default:
                    return;
            }
        } catch (InvalidProbabilityException ipe) {
            // Program needs error handling.
            // Also shouldn't be able to reach this because sliders go from 0 - 100,
            // so values will be 0 - 1.
            System.err.println("Uh oh");
        }
        
    }
    
    /**
     * Listens for incoming events and acts accordingly.
     * 
     * 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        String command = e.getActionCommand();
        
        // If the action came from a slider. Sliders can be set even when board is paused.
        if (e.getSource().getClass() == JSlider.class) {
            
            String name = ((JSlider) e.getSource()).getName();
            int value = ((JSlider) e.getSource()).getValue();

            this.updateEnvironmentProbabilities(name, value);
            return;
        }

       
        // The board won't automatically repaint when not running.
        if (!this.isRunning) {

            if (command.equals("Pause")) {
                this.togglePause();
            } else if (command.equals("Reset")) {
                this.environment = new Environment(this.environment.getCellMatrix().length);
                this.window.board.setBoard(this.environment.getCellMatrix());
                this.window.repaint();
            }
            
            return;            
        }

        // Handle all other action commands.
        switch (command) {

            case "Pause":
                this.togglePause();
                return;
            
            case "Reset":

                // Ready a new environment
                Environment newEnv = new Environment(this.environment.getCellMatrix().length);

                // Transfer current physics
                try {
                    newEnv.setGenerativeProbability(            this.environment.getGenerativeProbability());
                    newEnv.setBurnoutProbability(               this.environment.getBurnoutProbability());
                    newEnv.setIgnitionProbability(              this.environment.getIgnitionProbability());
                    newEnv.setResurrectionProbability(          this.environment.getResurrectionProbability());
                    newEnv.setSpontaneousCombustionProbability( this.environment.getSpontaneousCombustionProbability());
                } catch (InvalidProbabilityException ipe) {
                    // TODO: handle exception
                    // Again, this should not be reachable.
                }
                
                // Load new environment
                this.environment = newEnv;

                // Paint the new board.
                this.window.board.setBoard(this.environment.getCellMatrix());
                this.window.repaint();
                return;

            case "Tick": // Tick goes the clock...

                this.environment.implementTransitions();                                 // Scan the board and implement transition protocols.
                this.window.board.setBoard(this.environment.getCellMatrix());            // Hand the new board to the painter.
                this.window.board.repaint();                                             // Paint the new matrix.  
                return;

            default:
                return;
        }




                              
        

    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        //System.out.println(e.getSource());
    }
   
    /**
     * Toggle the state of isRunning.
     */
    public void togglePause() {
        this.isRunning = !this.isRunning;
    }

    /**
     * Set isRunning to true.
     */
    public void start() {
        this.isRunning = true;
    }

    /**
     * Set isRunning to false.
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * String representation of the driver/controller.
     */
    public String toString() {
        return "Fire Simulation Driver/Controller";
    }

    public static void main(String[] args) {

        int matrixDimension = Integer.parseInt(args[0]);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() { 

                Driver driver = new Driver();                                   // Load the driver

                driver.environment = new Environment(matrixDimension);          // Load the environment.
                driver.window = new Window(driver.environment.getCellMatrix()); // Load the graphics.
                driver.timer = new Timer(55, driver);
                driver.timer.setActionCommand("Tick");
                
                driver.window.addActionListener(driver);                        // Attach listener to controller.
               

                driver.timer.start();                                           // Start the timer.
                                 
            }
        });
    }
                                             
}
