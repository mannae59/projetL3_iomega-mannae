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
import update.Observable;
import update.Observer;

@SuppressWarnings("serial")
public class Display extends JFrame implements Observable {
	private List<Observer> tabObserver;
	private List<String> listConnectedSensors;
	private List<String> sensorDateVariation1;
	private List<String> sensorDateVariation2;
	private List<String> sensorDateVariation3;
	private DatabaseCommunication db;
	private int nbRedRows = 0;
	// TODO Verifier ces variables : doute sur leur validite --Nicolas
	private List<Date> sensorDateDisplay;
	private TreeMap<String,TreeMap<String,List<String>>> batiments;
	
	public Display(DatabaseCommunication db){
		this.db = db;
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
		if(sensorsList != null) {
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
	
	/* Code fourni par le prof  - exemple de base */
	private static CategoryDataset createCategoryDataset() {
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
		private static JFreeChart createChart(CategoryDataset d) {
		JFreeChart chart = ChartFactory.createLineChart("Evolution of sellers" , "Trimester",
		"Cars sold", d, PlotOrientation.VERTICAL, true, true, false);
		return chart;
		}
		
		 /*Fin code prof */
		
	public JPanel initTabLater() {
		JPanel later = new JPanel();
		later.setLayout(null); // Decision de positionner a la main pour eviter les bugs avec JFreeChart
		
		JFreeChart chart = createChart(createCategoryDataset());
		ChartPanel cp = new ChartPanel(chart,true);
		cp.setBounds(0,0,600,400);
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
		return Arrays.asList(sensor.split(":"));
	}
	
	public List<List<String>> getAllSensors(){
		// TODO implementer avec les fonctions de la BDD
		
		List<List<String>> data = new ArrayList<>();;
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
