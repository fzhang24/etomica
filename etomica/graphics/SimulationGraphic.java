//This class includes a main method to demonstrate its use
package etomica.graphics;
import etomica.*;

//Java2 imports
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.Iterator;

import etomica.utility.LinkedList;
import etomica.utility.Iterator;

/**
 * The main class that organizes the elements of a molecular simulation.
 * Holds a single space object that is referenced in
 * many places to obtain spatial elements such as vectors and boundaries.  Also
 * holds an object that specifies the unit system used to default all I/O.  A single
 * instance of Simulation is held as a static field, and which forms the default
 * Simulation class needed in the constructor of all simulation elements.
 *
 * @author David Kofke
 */
public class SimulationGraphic extends Simulation {
    
    public String getVersion() {return "SimulationGraphic:01.11.20;"+Simulation.VERSION;}
    
    
    private SimulationPanel simulationPanel;
    
    public SimulationGraphic() {
        this(new Space2D());
    }
    
    /**
     * Constructor requires specification of the space used by the simulation
     */
    public SimulationGraphic(Space s) {
        super(s);
        elementLists.put(Display.class, new LinkedList());
        elementLists.put(Device.class, new LinkedList());
        elementCoordinator = new MediatorGraphic(this);
    }//end of constructor
    
    /**
     * @return the <code>nth</code> instantiated display (indexing from zero)
     */
    public final Display display(int n) {return (Display)displayList().get(n);}
    /**
     * @return the <code>nth</code> instantiated device (indexing from zero)
     */
    public final Device device(int n) {return (Device)deviceList().get(n);}
    
    public final LinkedList displayList() {return (LinkedList)elementLists.get(Display.class);}
    public final LinkedList deviceList() {return (LinkedList)elementLists.get(Device.class);}
                  
     
    /**
     * Method invoked in the constructor of a Display object to list it with the simulation
     */
 /*   public void unregister(Display d) {
        if(!displayList.contains(d)) return;
        displayList.remove(d);
        allElements.remove(d);
        integrator(0).removeIntervalListener(d);
        for (int i = 0; i < getComponentCount(); i++) {
	        if (getComponent(i).getName() == "displayPanel"){
	            javax.swing.JTabbedPane tp = ((javax.swing.JTabbedPane)getComponent(i));
	            tp.remove(tp.getSelectedComponent());
	            tp.repaint();
	            elementCoordinator.completed = false;
	            
	        }
	    }
    }
    
    /**
     * Method invoked in the constructor of a Device object to list it with the simulation
     */
/*    public void unregister(Device d) {
        if(!deviceList.contains(d)) return;
        deviceList.remove(d);
        allElements.remove(d);
        for (int i = 0; i < getComponentCount(); i++) {
	        if (Simulation.instance.getComponent(i).getName() == "devicePanel"){
                javax.swing.JPanel dp = ((javax.swing.JPanel)getComponent(i));
                dp.remove(dp.getComponent(1));
                dp.repaint();
                elementCoordinator.completed = false;
            }
        }
    }
 */             
                
    /**
     * A visual display of the simulation via a JPanel.
     * This may become more important if Simulation itself is revised to not extend JPanel.
     */
     public SimulationPanel panel() {
        if(simulationPanel == null) simulationPanel = new SimulationPanel(this);
        return simulationPanel;
     }
     
    public void makeAndDisplayFrame() {SimulationGraphic.makeAndDisplayFrame(this);}
    
    public static final void makeAndDisplayFrame(Simulation sim) {
        makeAndDisplayFrame((SimulationGraphic)sim);
    }
    public static final void makeAndDisplayFrame(SimulationGraphic sim) {
        javax.swing.JFrame f = new javax.swing.JFrame();
        f.setSize(700,500);
        f.getContentPane().add(sim.panel());
        f.pack();
        f.show();
        f.addWindowListener(SimulationGraphic.WINDOW_CLOSER);
    }
    
    public static final java.awt.event.WindowAdapter WINDOW_CLOSER 
        = new java.awt.event.WindowAdapter() {   //anonymous class to handle window closing
            public void windowClosing(java.awt.event.WindowEvent e) {System.exit(0);}
        };
        
    /**
     * Demonstrates how this class is implemented.
     */
    public static void main(String[] args) {
        Simulation.instance = new SimulationGraphic(new Space2D());
        DefaultGraphic.ATOM_COLOR = java.awt.Color.red;
   //     Default.ATOM_SIZE = 1.0;                   
	    IntegratorHard integratorHard = new IntegratorHard();
	    SpeciesSpheresMono speciesSpheres = new SpeciesSpheresMono();
	    speciesSpheres.setNMolecules(30);
	    Phase phase = new Phase();
	    Potential2 potential = new P2HardSphere();
	    Controller controller = new Controller();
	    DisplayPhase displayPhase = new DisplayPhase();
//	    IntegratorMD.Timer timer = integratorHard.new Timer(integratorHard.chronoMeter());
//	    timer.setUpdateInterval(10);
//        integratorHard.setTimeStep(0.01);
/*        displayPhase.setColorScheme(new ColorSchemeNull());
        for(Atom atom=phase.firstAtom(); atom!=null; atom=atom.nextAtom()) {
            atom.setColor(ConstantsGraphic.randomColor());
        }*/
        
        //this method call invokes the mediator to tie together all the assembled components.
		Simulation.instance.elementCoordinator.go();
		                                    
		((SimulationGraphic)Simulation.instance).panel().setBackground(java.awt.Color.yellow);
        SimulationGraphic.makeAndDisplayFrame(Simulation.instance);
                
     //   controller.start();
    }//end of main
    
}


