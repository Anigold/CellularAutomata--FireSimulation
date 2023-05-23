package Simulation.Graphics;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.util.EventListener;
import java.util.HashMap;
import java.util.jar.JarEntry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.synth.SynthScrollBarUI;

/**
 * I'm not ready to make this code look pretty yet.
 * 
 * Note to self:
 * 
 * This class hosts the controls and listens for inputs, it then sends out the input without
 * changing it. 
 * 
 * The use of factories for each control type would make this infinitely better to work with.
 * 
 * The use of screen size will save a lot of heachache is positioning.
 */
public class DriverControls extends JPanel implements ActionListener, ChangeListener {
    
    protected EventListenerList listenerList = new EventListenerList();
    
    
    public JPanel        mainPanel;
    public SpringLayout  layout;

    public JPanel        buttonContainer;
    public JToggleButton pauseButton;
    public JButton       resetButton;

    public JPanel        probabilityContainer;

    private HashMap<String, JSlider> sliderMap = new HashMap<String, JSlider>();
    private HashMap<String, JSlider> buttonMap = new HashMap<String, JSlider>();

    public JPanel        cellContainer;

    
    
    public DriverControls() {

        this.mainPanel  = new JPanel(new GridLayout(3,4, 5, 5));

        mainPanel.setSize(500, 1000);

        buildButtonContainer();
        createProbabilitySliders();
        generateProbabilityPanel();


        this.mainPanel.add(this.buttonContainer);
        this.mainPanel.add(this.probabilityContainer);

        this.add(this.mainPanel);
    }

    
    public void buildButtonContainer() {
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim = toolkit.getScreenSize();

        this.buttonContainer = new JPanel();

        this.pauseButton = new JToggleButton();
        this.resetButton = new JButton();

        this.pauseButton.setPreferredSize(new Dimension(dim.width / 20, dim.height / 25));
        this.resetButton.setPreferredSize(new Dimension(dim.width / 20, dim.height / 25));

        this.pauseButton.setText("Pause");
        this.resetButton.setText("Reset");
        
        this.pauseButton.setActionCommand("Pause");
        this.pauseButton.addActionListener(this);
        this.resetButton.setActionCommand("Reset");
        this.resetButton.addActionListener(this);

        this.buttonContainer.add(this.pauseButton); 
        this.buttonContainer.add(this.resetButton);  

    }

    public void addSlider(int min, int max, String name) {

        JSlider slider = new JSlider(min, max);

        slider.setName(name);
        slider.addChangeListener(this);

        this.sliderMap.put(name, slider);
        
    }

    public HashMap<String, JSlider> getSliderMap() {
        return this.sliderMap;
    }

    public void generateProbabilityPanel() {

        
        HashMap<String, JSlider> sliders = this.getSliderMap();

        int size = sliders.size();
        this.probabilityContainer = new JPanel(new GridLayout(size, 2, 10, 5));
        for (String slider : sliders.keySet()) {
            this.probabilityContainer.add(new JLabel(slider));
            this.probabilityContainer.add(sliders.get(slider));
        }

    }

    public void createProbabilitySliders() {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim   = toolkit.getScreenSize();

        String[] sliderLabels = {
            "Grass Generation Ratio",
            "Burnout Probablity",
            "Ignition Probability",
            "Resurrection Probability",
            "Spontaneous Combustion Probability"
        };

        this.probabilityContainer           = new JPanel(new GridLayout(5, 2, 5, 10));
        //SpringLayout layout                 = new SpringLayout();

        //this.probabilityContainer.setLayout(layout);
        for (String label : sliderLabels) {
            this.addSlider(0, 100, label);
        }
        
    }

    protected void fireChangeOccured(ChangeEvent e) {
        
        Object[] listeners = this.getListeners(ActionListener.class);
    
        for (int i = 0; i < listeners.length; ++i) {
            ((ChangeListener) listeners[i]).stateChanged(e);
        }
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

    @Override
    public String toString() {
        return "Fire Simulation Controls";
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        Object source = e.getSource();

        if (source.getClass() == JSlider.class) {

            String name = ((JSlider) source).getName();
            int value = ((JSlider) source).getValue();
            ActionEvent action = new ActionEvent(source, value, name);

            this.actionPerformed(action);
        }
      
       
    }
}
