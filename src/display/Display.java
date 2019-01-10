package display;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import communication.DatabaseCommunication;
import communication.Fluid;
import communication.Main;
import update.Observable;
import update.Observer;

@SuppressWarnings("serial")
public class Display extends JFrame implements Observer {
	private List<Observer> tabObserver;
	
	
	private ConfigurationTreePanel configuration;
	private LaterTab laterTab;
	
	private List<String> sensorDateVariation1;
	private List<String> sensorDateVariation2;
	private List<String> sensorDateVariation3;
	private List<String> sensorsAvailable; // Sensors got from the choice of the fluid on the 'later' panel
	private List<List<String>> sensorsOutOfLimit;
	private DatabaseCommunication db;
	private int nbRedRows = 0;
	private Fluid fluid;
	JPanel mainPanel;
	JPanel askPort;
	JPanel later;
	Main main;
	
	JPopupMenu menu; // Also needs to be declared here
	// TODO Verifier ces variables : doute sur leur validite --Nicolas
	private List<Date> sensorDateDisplay;
	
	public Display(DatabaseCommunication db, Main main){
		this.db = db;
		this.fluid = new Fluid();
		this.main = main;
		
		initUI();
	}
	
	
	
	public void setCloseOperation() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		WindowListener exitListener = new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent e) {
				db.close();
				System.exit(0);
		    }
		};
		addWindowListener(exitListener);
	}

	    
	
	private void initUI(){
		// Essayer d'appliquer le look de l'OS au lieu de celui de Swing
//		try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//        }
		setTitle("Interface Neocampus"); // Titre de la fenetre
		setSize(1024,768); // Taille de la fenetre : 640 x 480
		setLocationRelativeTo(null); // Centrage
		setCloseOperation(); // Sets the action performed when we click on the 'X'
		JTabbedPane tabs = new JTabbedPane();
		JPanel realTime = new RealTime(db);
		laterTab = new LaterTab(db,fluid);
		configuration =new ConfigurationTreePanel(db,this);
		tabs.addTab("Temps reel",realTime);
		tabs.addTab("A posteriori",laterTab);
		tabs.addTab("Configuration",configuration);
		add(tabs);
//		setVisible(true);
	}
	
	
	
	
	
	
	
	
	public List<String> getSensorsWithFluid(String fluidType) {
		// TODO Complete this method
		return null;
	}

	public List<String> getSensorWithDate(String sensorName, Date start, Date stop) {
		// TODO Complete this method
		return null;
	}
	
	
	public void update(int i) {
//		System.out.println("*** update  ***");
		switch (i) {
		case 0: 
			// seuil change             
			configuration.updateTree();//recupere la map et maj du tree
            
            break;
		case 1: //donnee mise a jour
           
            updateDateSensor(); //update le tableau si cest les donnees de capteurs concerne par l'affichage
            //appel getSensorWithDate(String sensorName, Date start, Date stop) et met a jour les attributs de Display 
            break;
		case 2: //nouvelle connection 
           
            configuration.updateTree();
            break;
		
			
		}
		
        
	}
	
	public void updateDateSensor() {
		
	}
	
//	public void updateTabRealTime() {
//		System.out.println("*** update tabrealtime ***");
//		//met a jour la liste de capteurs connecte
//		listConnectedSensors = db.getConnectedSensors();
//		//les organise cf nicolas
//		List<List<String>> sensorsList = getConnectedSensors(); 
//		System.out.println("nb sensors connected : " + sensorsList.size());
//		//les remplace dans la classe realTimeTableModel
//		rttmodel.setData(sensorsList);
//		//et relance une fenetre 
//		rttmodel.fireTableDataChanged();
//		updateSensorsOutOfLimit(sensorsList,btnWarning);
//		
//	}
	
	
	
	public void displayCurveTime() {
		// TODO Complete this method
	}
	
	public void displaySensorsGestion() {
		// TODO Complete this method
	}
}
