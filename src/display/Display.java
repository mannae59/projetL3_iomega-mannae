package display;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import communication.DatabaseCommunication;
import communication.Fluid;
import communication.Main;
import update.Observable;
import update.Observer;

@SuppressWarnings("serial")
public class Display extends JFrame implements Runnable,Observable {
	private List<Observer> tabObserver;
	private List<String> listConnectedSensors;
	private List<String> sensorDateVariation1;
	private List<String> sensorDateVariation2;
	private List<String> sensorDateVariation3;
	private DatabaseCommunication db;
	private int nbRedRows = 0;
	private int port;
	private Fluid fluid;
	JPanel mainPanel;
	JPanel askPort;
	Main main;
	// TODO Verifier ces variables : doute sur leur validite --Nicolas
	private List<Date> sensorDateDisplay;
	private TreeMap<String,TreeMap<String,List<String>>> batiments;
	
	public Display(DatabaseCommunication db, Main main){
		this.db = db;
		this.fluid = new Fluid();
		this.main = main;
		initUI();
	}
	
	
	
	private void initUI(){
		// Essayer d'appliquer le look de l'OS au lieu de celui de Swing
//		try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//        }
		setTitle("Interface Neocampus"); // Titre de la fenetre
		setSize(640,480); // Taille de la fenetre : 640 x 480
		setLocationRelativeTo(null); // Centrage
		setDefaultCloseOperation(EXIT_ON_CLOSE); // Appui sur la croix -> quitter
		JTabbedPane tabs = new JTabbedPane();
		JPanel realTime = initTabRealTime();
		JPanel later = initTabLater();
		JPanel configuration = initTabConfiguration();
		tabs.addTab("Temps réel",realTime);
		tabs.addTab("A postériori",later);
		tabs.addTab("Configuration",configuration);
		add(tabs);
		setVisible(true);
	}
	
	public void run() {
		this.setVisible(true);
	}
	
	public JPanel initTabRealTime() { // Decoupage en 6 colonnes, 5 lignes
		// Creation de la frame
		JPanel realTime = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		/* Creation des elements de la frame */
		
		// Table
		List<List<String>> sensorsList = getAllSensors();
		RealTimeTableModel rttmodel = new RealTimeTableModel(sensorsList);
		JTable table = new JTable(rttmodel) {
			@Override
			public Dimension getPreferredScrollableViewportSize()
		    {
		        return new Dimension(310, 310);
		    }
		};
		// Choix de la largeur des colonnes
		if(sensorsList != null && table.getColumnModel().getSelectedColumnCount() > 3) {
			table.getColumnModel().getColumn(0).setPreferredWidth(60);
			table.getColumnModel().getColumn(1).setPreferredWidth(140);
			table.getColumnModel().getColumn(2).setPreferredWidth(110);
			table.getColumnModel().getColumn(3).setPreferredWidth(50);
		}
	
		// Ajout de la table avec scrollPane
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 8;
		c.insets = new Insets(20,10,60,10);
		JScrollPane scroller = new JScrollPane(table);
		realTime.add(scroller,c);
		
		// Label "Trier par"
		JLabel lblTrierPar = new JLabel("Trier par...");
		c.insets = new Insets(30,0,60,10);
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		realTime.add(lblTrierPar,c);
		
		// Combo box
		String[] sorting = {"Nom","Type","Bâtiment"};
		JComboBox<String> sortList = new JComboBox<>(sorting) ;
		c.insets = new Insets(0,10,60,10);
		c.gridy = 1;
		realTime.add(sortList,c);
		
		JButton button = new JButton(Integer.toString(nbRedRows));
		  try {
		    Image img = ImageIO.read(getClass().getResource("warning.png"));
		    button.setIcon(new ImageIcon(img));
		    button.setHorizontalTextPosition(SwingConstants.CENTER);
		    button.setVerticalTextPosition(SwingConstants.BOTTOM);
		    button.addActionListener(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new WindowAlert(nbRedRows);
					
				}
			});
		  } catch (IOException ex) {
		    ex.printStackTrace();
		  }
		c.insets = new Insets(0,10,60,10);
		c.gridy = 4;
		c.gridwidth = 2;
		c.gridheight = 2;
	    realTime.add(button,c);
		return realTime;
	}
	
	// HashSet time -> Value
	// Not using varargs because of heap risk
	private CategoryDataset createCategoryDataset(String name1, HashMap<String,String> sensor1, String name2, HashMap<String,String> sensor2, String name3, HashMap<String,String> sensor3) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if(sensor1 != null) {
			for(String time : sensor1.keySet()) {
				dataset.setValue(Integer.parseInt(sensor1.get(time)), name1 , time);
			}
		}
		
		if(sensor2 != null) {
			for(String time : sensor2.keySet()) {
				dataset.setValue(Integer.parseInt(sensor2.get(time)), name2 , time);
			}
		}
		
		if(sensor3 != null) {
			for(String time : sensor3.keySet()) {
				dataset.setValue(Integer.parseInt(sensor3.get(time)), name3 , time);
			}
		}
		return dataset;
		}
	/* Code fourni par le prof  - exemple de base - ne servira plus quand tout sera fonctionnel */
	private CategoryDataset createCategoryDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.setValue(10, "SELLER 1" , "Jan-Mar");
		dataset.setValue(8, "SELLER 1" , "Avr-Jui");
		dataset.setValue(12, "SELLER 1" , "Jui-Sep");
		dataset.setValue(20, "SELLER 1" , "Oct-Dec");
		dataset.setValue(4, "SELLER 2" , "Jan-Mar");
		dataset.setValue(8, "SELLER 2" , "Avr-Jui");
		dataset.setValue(12, "SELLER 2" , "Jui-Sep");
		dataset.setValue(24, "SELLER 2" , "Oct-Dec");
		dataset.setValue(30, "SELLER 3" , "Jan-Mar");
		dataset.setValue(4, "SELLER 3" , "Avr-Jui");
		dataset.setValue(12, "SELLER 3" , "Jui-Sep");
		dataset.setValue(1, "SELLER 3" , "Oct-Dec");
		return dataset;
	}
	 /*Fin code prof */
	
	private JFreeChart createChart(CategoryDataset d) {
		return ChartFactory.createLineChart("Valeurs envoyees par les capteurs" , "Temps",
		"Valeur", d, PlotOrientation.VERTICAL, true, true, false);
	}
		
	public JPanel initTabLater() {
		JPanel later = new JPanel();
		later.setLayout(null); // Decision de positionner a la main pour eviter les bugs avec JFreeChart
		
		// Labels
		JLabel lblAddSensor = new JLabel("Ajouter un capteur :");
		JLabel lblSensorType = new JLabel("Type de capteur");
		JLabel lblSensorSelection = new JLabel("Sélection du capteur");
		JLabel lblStart = new JLabel("Début");
		JLabel lblEnd = new JLabel("Fin");
		lblAddSensor.setBounds(getWidth()/2-60, 0, 120, 30);
		lblSensorType.setBounds(20,40,50,20);
		lblSensorSelection.setBounds(90,40,50,20);
		lblStart.setBounds(160,40,50,20);
		lblEnd.setBounds(230,40,50,20);
		later.add(lblAddSensor);
		later.add(lblSensorType);
		later.add(lblSensorSelection);
		later.add(lblStart);
		later.add(lblEnd);
		
		// Combo boxes
		//String[] sensorTypes = (String[]) fluid.keySet().toArray();
		JComboBox<String> cbbSensorType = new JComboBox<>();
		JComboBox<String> cbbSensorSelection = new JComboBox<>();
		JComboBox<String> cbbStart = new JComboBox<>();
		JComboBox<String> cbbEnd = new JComboBox<>();
		// Some combo boxes are disabled at the beginning
		cbbSensorSelection.setEnabled(false);
		cbbStart.setEnabled(false);
		cbbEnd.setEnabled(false);
		cbbSensorType.setBounds(20,70,50,20);
		cbbSensorSelection.setBounds(90,70,50,20);
		cbbStart.setBounds(160,70,50,20);
		cbbEnd.setBounds(230,70,50,20);
		later.add(cbbSensorType);
		later.add(cbbSensorSelection);
		later.add(cbbStart);
		later.add(cbbEnd);
		
		// getSensorsWithFluid()
		// When the sensor(s) is (are) selected, generate the chart
		JFreeChart chart = createChart(createCategoryDataset());
		ChartPanel cp = new ChartPanel(chart,true);
		cp.setBounds(0,100,getWidth()-20,getHeight()-170);
		later.add(cp);
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
	
	private List<String> decodeInfo(String sensor){
		// Splitter les donnees
		List<String> rawData = Arrays.asList(sensor.split(":"));
		// Ranger les donnees
		List<String> sortedData = new ArrayList<String>();
		sortedData.add(rawData.get(0)); // Ajout du nom
		String type = rawData.get(1); // Recuperation du type de fluide
		sortedData.add(type); // Ajout du type
		// Construction de la localisation
		String loc = rawData.get(2) + " - Etage " + rawData.get(3) + " - " + rawData.get(4);
		sortedData.add(loc);
		// Association valeur + unité
		String value = rawData.get(5) + " " + fluid.get(type);
		sortedData.add(value);
		//sortedData = rawData; // Temporaire
		return sortedData;
	}
	
	public List<List<String>> getAllSensors(){
		List<List<String>> data = new ArrayList<>();
		List<String> sensorsConnected = db.getSensorsConnected();
		if(sensorsConnected != null) {
			for(String sensor : sensorsConnected) {
				data.add(decodeInfo(sensor));
			}
			return data;
		}
		return null;
//		List<List<String>> data = Arrays.asList(Arrays.asList("capteur1","ELECTRICITE","U3 - U3-215","2 kWh"),Arrays.asList("capteur3","TEMPERATURE","U4 - Couloir","1 °C"));
//		return data;
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
