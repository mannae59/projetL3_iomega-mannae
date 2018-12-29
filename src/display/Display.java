package display;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import update.Observable;
import update.Observer;

public class Display extends JFrame implements Observable {
	private List<Observer> tabObserver;
	private List<String> listConnectedSensors;
	private List<String> sensorDateVariation1;
	private List<String> sensorDateVariation2;
	private List<String> sensorDateVariation3;
	// TODO Verifier ces variables : doute sur leur validite --Nicolas
	private List<Date> sensorDateDisplay;
	private TreeMap<String,TreeMap<String,List<String>>> batiments;
	
	public Display(){
		initUI();
	}
	private void initUI(){
		// Essayer d'appliquer le look de l'OS au lieu de celui de Swing
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
		
		setTitle("Interface Neocampus"); // Titre de la fenetre
		setSize(640,480); // Taille de la fenetre : 640 x 480
		setLocationRelativeTo(null); // Centrage
		setDefaultCloseOperation(EXIT_ON_CLOSE); // Appui sur la croix -> quitter

		JPanel pnlButton = new JPanel();
		JButton boutonOK = new JButton("OK"); 
		// Choix des positions des boutons manuellement
		//this.setLayout(null);
		// Initialisation d'un bouton
		//pnlButton.setLayout(null);
		JTabbedPane tabs = new JTabbedPane();
		JPanel realTime = initTabRealTime();
		JPanel later = initTabLater();
		JPanel configuration = initTabConfiguration();
		tabs.addTab("Temps réel",realTime);
		tabs.addTab("A postériori",later);
		tabs.addTab("Configuration",configuration);
		pnlButton.add(boutonOK);
		add(pnlButton,BorderLayout.PAGE_END);
		add(tabs);
		//pnlButton.setLocation(0,0);
	}
	public JPanel initTabRealTime() { // Decoupage en 6 colonnes, 5 lignes
		// Creation de la frame
		JPanel realTime = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		// Creation des elements de la frame
		String[][] donnees = {{"a","b","c"},{"a","b","c"},{"a","b","c"}};
		String[] entetes = {"lettre","nom","prenom"};
		// Table
		JTable table = new JTable(donnees,entetes);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		// Ajout du header
		realTime.add(table.getTableHeader(),c);
		// Ajout de la table avec scrollPane
		c.gridy = 1;
		c.gridheight = 4;
		JScrollPane scroller = new JScrollPane(table);
		//realTime.add(scroller,c);
		realTime.add(table,c);
		JLabel lblTrierPar = new JLabel("Trier par...");
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		realTime.add(lblTrierPar,c);
		String[] sorting = {"Nom","Bâtiment"};
		JComboBox<String> sortList = new JComboBox<>(sorting) ;
		c.gridy = 1;
		realTime.add(sortList,c);
		JLabel label = new JLabel("test");
		c.gridx = 1;
		c.gridy = 6;
		
		realTime.add(label,c);
		
		//lblTrierPar.setLocation((int)size.width /2,(int)size.height/2);
		
		// Ajout des elements dans la frame
		return realTime;
	}
	
	public JPanel initTabLater() {
		JPanel later = new JPanel();
		return later;
	}
	
	public JPanel initTabConfiguration() {
		JPanel configuration = new JPanel();
		return configuration;
	}
	
	@Override
	public void addObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}
	public void update(Observable o) {
		
	}

	public List<String> getSensorsWithFluid(String fluidType) {
		// TODO Complete this method
		return null;
	}

	public List<String> getSensorWithDate(String sensorName, Date start, Date stop) {
		// TODO Complete this method
		return null;
	}
	public void updateTabRealTime() {
		// TODO Complete this method
	}
	
	public void updateTreeSensor() {
		// TODO Complete this method
	}
	
	public void displayCurveTime() {
		// TODO Complete this method
	}
	
	public void displaySensorsGestion() {
		// TODO Complete this method
	}
}
