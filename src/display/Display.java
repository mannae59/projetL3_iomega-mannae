package display;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import communication.DatabaseCommunication;
import communication.Fluid;
import communication.Main;
import update.Observable;
import update.Observer;

@SuppressWarnings("serial")
public class Display extends JFrame implements Observable {
	private List<Observer> tabObserver;
	private List<String> listConnectedSensors;
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
	Main main;
	JButton btnSensorSelection; // Button on the 'later' panel that needs to be declared here
	JPopupMenu menu; // Also needs to be declared here
	// TODO Verifier ces variables : doute sur leur validite --Nicolas
	private List<Date> sensorDateDisplay;
	
	public Display(DatabaseCommunication db, Main main){
		this.db = db;
		this.fluid = new Fluid();
		this.main = main;
		initUI();
	}
	
	public void setNbRedRows(int nb) {
		this.nbRedRows = nb;
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
		setSize(640,480); // Taille de la fenetre : 640 x 480
		setLocationRelativeTo(null); // Centrage
		setCloseOperation(); // Sets the action performed when we click on the 'X'
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
	
	private JButton createWarningButton() {
		JButton button = new JButton();
		  try {
		    Image img = ImageIO.read(getClass().getResource("warning.png"));
		    button.setIcon(new ImageIcon(img));
		    button.setHorizontalTextPosition(SwingConstants.CENTER);
		    button.setVerticalTextPosition(SwingConstants.BOTTOM);
		    button.addActionListener(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new AlertWindow(nbRedRows, sensorsOutOfLimit);
					
				}
			});
		  } catch (IOException ex) {
			  JOptionPane.showMessageDialog(null,"Erreur : Impossible d'accéder au dossier courant.");
		  } catch(IllegalArgumentException e) {
			  JOptionPane.showMessageDialog(null,"Erreur : le logo du bouton Warning est introuvable.");
		  }
		  return button;
	}
	
	private void updateSensorsOutOfLimit(List<List<String>> sensorsList, JButton button) {
		sensorsOutOfLimit = new ArrayList<>();
		for(List<String> sensor : sensorsList) {
			if(sensor.get(8).equals("1")) {
				sensorsOutOfLimit.add(sensor);
			}
		}
		if(sensorsOutOfLimit.isEmpty()) {
			nbRedRows = 0;
		} else {
			nbRedRows = sensorsOutOfLimit.size();
		}
		button.setText(Integer.toString(nbRedRows));
	}

	private List<List<String>> getConnectedSensors(){
		List<List<String>> data = new ArrayList<>();
		List<String> sensorsConnected = db.getConnectedSensors();
		if(sensorsConnected != null) {
			for(String sensor : sensorsConnected) {
				data.add(Arrays.asList(sensor.split(":")));
			}
		}
		return data;
	}
	
	public JPanel initTabRealTime() { // Decoupage en 6 colonnes, 5 lignes
		// Creation de la frame
		JPanel realTime = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		/* Creation des elements de la frame */
		
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
	    
	 // Table
 		List<List<String>> sensorsList = getConnectedSensors(); // List of sensors connected to display
 		RealTimeTableModel rttmodel = new RealTimeTableModel(sensorsList);
 		JTable table = new JTable(rttmodel) {
 			@Override
 			public Dimension getPreferredScrollableViewportSize()
 		    {
 		        return new Dimension(410, 310);
 		    }
 		};
 		
 		// Warning button
		JButton button = createWarningButton();
		updateSensorsOutOfLimit(sensorsList, button);
		c.insets = new Insets(0,10,60,10);
		c.gridy = 4;
		c.gridwidth = 2;
		c.gridheight = 2;
	    realTime.add(button,c);
 		
 		// Sets the new cell renderer (whitch colors a line if the value exceeds the limits)
 		table.setDefaultRenderer(Object.class,new ColorTableModel());
 		// Choix de la largeur des colonnes
 		if( table.getColumnModel().getSelectedColumnCount() > 3) {
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
	
	private JButton createSelectorFromType(String type) {
		sensorsAvailable = db.getSensorsWithFluid(type); // Get the sensors to show
		JButton button = new JButton("Capteur..."); // Button which opens the list
		menu = new JPopupMenu(); // List which show the sensors
		for(String sensor : sensorsAvailable) {
			JMenuItem item = new JCheckBoxMenuItem(sensor.split(":")[0]);
			item.addActionListener(new OpenAction(menu, button));
			menu.add(item);
		}
		// Action to view and hide the menu
		button.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        if (!menu.isVisible()) {
		            Point p = button.getLocationOnScreen();
		            menu.setInvoker(button);
		            menu.setLocation((int) p.getX(),
		                    (int) p.getY() + button.getHeight());
		            menu.setVisible(true);
		        } else {
		            menu.setVisible(false);
		        }

		    }
		});
		return button;
	}

	private static class OpenAction implements ActionListener {
	    private JPopupMenu menu;
	    private JButton button;
	    private OpenAction(JPopupMenu menu, JButton button) {
	        this.menu = menu;
	        this.button = button;
	    }
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        menu.show(button, 0, button.getHeight());
	    }
	}
	
	private List<String> getSelectedItems(JPopupMenu menu){
		List<String> list = new ArrayList<>();
		for(Component i : menu.getComponents()) {
			if(i instanceof JCheckBoxMenuItem) {
				JCheckBoxMenuItem j = (JCheckBoxMenuItem) i;
				if(j.isSelected()) {
					list.add(j.getText());
				}
			}
		}
		return list;
	}
	
	public JPanel initTabLater() {
		JPanel later = new JPanel();
		later.setLayout(null); // Decision de positionner a la main pour eviter les bugs avec JFreeChart
		
		// Labels
		JLabel lblAddSensor = new JLabel("Ajouter un capteur :", SwingConstants.CENTER);
		JLabel lblSensorType = new JLabel("Type de capteur", SwingConstants.CENTER);
		JLabel lblSensorSelection = new JLabel("<html><center>Sélection du<br>capteur</center></html>", SwingConstants.CENTER);
		JLabel lblStart = new JLabel("Début", SwingConstants.CENTER);
		JLabel lblEnd = new JLabel("Fin", SwingConstants.CENTER);
		lblAddSensor.setFont(new Font("Serif", Font.BOLD, 20));
		lblAddSensor.setBounds(getWidth()/2-90, 0, 180, 30);
		lblSensorType.setBounds(20,40,120,20);
		lblSensorSelection.setBounds(160,30,80,40);
		lblStart.setBounds(260,40,100,20);
		lblEnd.setBounds(380,40,100,20);
		later.add(lblAddSensor);
		later.add(lblSensorType);
		later.add(lblSensorSelection);
		later.add(lblStart);
		later.add(lblEnd);
		
		// Combo boxes
		JComboBox<String> cbbSensorType = new JComboBox<>();
		btnSensorSelection = new JButton("Capteur...");
		JComboBox<String> cbbStart = new JComboBox<>();
		JComboBox<String> cbbEnd = new JComboBox<>();
		// Some combo boxes are disabled at the beginning
		btnSensorSelection.setEnabled(false);
		cbbStart.setEnabled(false);
		cbbEnd.setEnabled(false);
		
		// Positionning the combo boxes
		cbbSensorType.setBounds(20,70,120,20);
		btnSensorSelection.setBounds(160,70,80,20);
		cbbStart.setBounds(260,70,100,20);
		cbbEnd.setBounds(380,70,100,20);
		
		// Adding the elements to the combo boxes
		Set<String> list = fluid.keySet();
		for(String item : list) {
			cbbSensorType.addItem(item);
		}
		
		// What happens when a type is selected
		cbbSensorType.addActionListener(new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String selected = cbbSensorType.getSelectedItem().toString();
				btnSensorSelection.setVisible(false);
				btnSensorSelection = createSelectorFromType(selected);
				btnSensorSelection.setBounds(160,70,80,20);
				btnSensorSelection.setEnabled(true);
				cbbStart.setEnabled(true);
				cbbEnd.setEnabled(true);
				later.add(btnSensorSelection);
			}
		});
		
		// Adding the combo boxes
		later.add(cbbSensorType);
		later.add(btnSensorSelection);
		later.add(cbbStart);
		later.add(cbbEnd);
		
		// OK button
		JButton confirmButton = new JButton("OK");
		confirmButton.setBounds(500,70,100,20);
		confirmButton.addActionListener(new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> selected = getSelectedItems(menu);
				//List<String> chartData = db.getSensorWithDate(selected,cbbStart.getSelectedItem(),cbbEnd.getSelectedItem()); // Data to use for the chart
				btnSensorSelection = new JButton("Capteur...");
				btnSensorSelection.setEnabled(false);
				cbbStart.setEnabled(false);
				cbbEnd.setEnabled(false);
			}
		});
		
		later.add(confirmButton);
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
