package display;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import communication.DatabaseCommunication;
import communication.Fluid;

public class LaterTab extends JPanel{
	private DatabaseCommunication db;
	private Fluid fluid = new Fluid();

	private List<String> sensorDateVariation1;
	private List<String> sensorDateVariation2;
	private List<String> sensorDateVariation3;
	private List<String> sensorsAvailable; // Sensors got from the choice of the fluid on the 'later' panel
	private JButton btnSensorSelection; // Button on the 'later' panel that needs to be declared here
	private JPopupMenu menu; // Also needs to be declared here
	
	private String selectedSensor1 = null;
	private String selectedSensor2 = null;
	private String selectedSensor3 = null;
	
	private JPanel panelHaut;
	private JPanel panelMilieu;
	private JPanel panelBas;
	private JPanel panelTextes;
	private JPanel panelBoutons;
	
	private JMenuItem item;
	private JLabel lblAddSensor;
	private JLabel lblSensorType;
	private JButton confirmButton;
	private JCheckBoxMenuItem j;
	private JLabel lblSensorSelection;
	private JLabel emptyLabel;
	private JLabel lblSensor1;
	private JLabel lblSensor2;
	private JLabel lblSensor3;
	
	private JComboBox<String> cbbSensorType;
	private JLabel lblStart;
	private JLabel lblEnd;
	private JButton btnStart;
	private JButton btnEnd;
	private Date dateStart;
	private Date dateEnd;
	private TimeChooser tStart;
	private TimeChooser tEnd;
	private DefaultCategoryDataset dataset;
	private JFreeChart chart;
	private ChartPanel cp;
	
	
	public LaterTab(DatabaseCommunication db, Fluid f) {
		super(new BorderLayout());
		this.db = db;
		this.fluid = f;
		initTabLater();
	}

	// Not using varargs because of heap risk
	private CategoryDataset createCategoryDataset(String name1, List<String> sensor1, String name2, List<String> sensor2, String name3, List<String> sensor3) {
		dataset = new DefaultCategoryDataset();
		System.out.println("Entering createCategoryDataset");
		System.out.println(name1 + " -> " + sensor1);
		System.out.println(name2 + " -> " + sensor2);
		System.out.println(name3 + " -> " + sensor3);
		if(sensor1 != null) {
			for(String value : sensor1) {
				String[] data = value.split(":");
				assert(data.length == 2); // Should be 2 : value:timestamp
				dataset.setValue(Double.parseDouble(data[0]), name1 , data[1]);
			}
		}
		
		if(sensor2 != null) {
			for(String value : sensor2) {
				String[] data = value.split(":");
				assert(data.length == 2); // Should be 2 : value:timestamp
				dataset.setValue(Double.parseDouble(data[0]), name2 , data[1]);
			}
		}
		
		if(sensor3 != null) {
			for(String value : sensor3) {
				String[] data = value.split(":");
				assert(data.length == 2); // Should be 2 : value:timestamp
				dataset.setValue(Double.parseDouble(data[0]), name3 , data[1]);
			}
		}
		return dataset;
	}
	
	/* Code fourni par le prof  - exemple de base - ne servira plus quand tout sera fonctionnel */
	private CategoryDataset createCategoryDataset() {
		dataset = new DefaultCategoryDataset();
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
	
	private void createSelectorFromType(String type) {
		sensorsAvailable = db.getSensorsWithFluid(type); // Get the sensors to show
		menu = new JPopupMenu(); // List which show the sensors
		for(String sensor : sensorsAvailable) {
			item = new JCheckBoxMenuItem(sensor.split(":")[0]);
			item.addActionListener(new OpenAction());
			menu.add(item);
		}
		// Action to view and hide the menu
		btnSensorSelection.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        if (!menu.isVisible()) {
		            Point p = btnSensorSelection.getLocationOnScreen();
		            menu.setInvoker(btnSensorSelection);
		            menu.setLocation((int) p.getX(), (int) p.getY() + btnSensorSelection.getHeight());
		            menu.setSize(btnSensorSelection.getSize().width,getSize().height);
		            menu.setVisible(true);
		        } else {
		            menu.setVisible(false);
		        }

		    }
		});
	}
	// Securite : empeche la selection de plus de 3 elements a la fois
	private class OpenAction implements ActionListener {    
	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	getSelectedItems();
	    	if(selectedSensor3 != null) {
	    		for(Component i : menu.getComponents()) {
					if(i instanceof JCheckBoxMenuItem) {
						j = (JCheckBoxMenuItem) i;
				    		if(!j.isSelected())
					    		j.setEnabled(false);
					}
    			}	
	    	}else {
	    		for(Component i : menu.getComponents()) {
					if(i instanceof JCheckBoxMenuItem) {
						j = (JCheckBoxMenuItem) i;
				    		if(!j.isSelected())
				    			j.setEnabled(true);
					}
    			}	
	    	}
	        menu.show(btnSensorSelection, 0, btnSensorSelection.getHeight());
	    }
	}
	
	private  void getSelectedItems(){
		List<String> list = new ArrayList<>();
		for(Component i : menu.getComponents()) {
			if(i instanceof JCheckBoxMenuItem) {
				j = (JCheckBoxMenuItem) i;
				if(j.isSelected()) {
					list.add(j.getText());
				}
			}
		}
		int nbSelected = list.size();
		if(nbSelected > 0) {
			selectedSensor1 = list.get(0);
			lblSensor1.setText("Capteur 1 sélectionné : " + selectedSensor1);
		} else {
			selectedSensor1 = null;
			lblSensor1.setText("");
		}
		if(nbSelected > 1) {
			selectedSensor2 = list.get(1);
			lblSensor2.setText("Capteur 2 sélectionné : " + selectedSensor2);
		} else {
			selectedSensor2 = null;
			lblSensor2.setText("");
		}
		if(nbSelected > 2) {
			selectedSensor3 = list.get(2);
			lblSensor3.setText("Capteur 3 sélectionné : " + selectedSensor3);
		} else {
			selectedSensor3 = null;
			lblSensor3.setText("");
		}
		if(nbSelected == 0) btnSensorSelection.setText("Aucun élément sélectionné");
		else if(nbSelected == 1) btnSensorSelection.setText("Un élément sélectionné");
		else btnSensorSelection.setText(nbSelected + " éléments sélectionnés");
		
	}

	public void initTabLater() {
		panelHaut = new JPanel(new GridLayout(3,1));
		panelTextes = new JPanel(new GridLayout(1,5));
		panelBoutons = new JPanel(new GridLayout(1,5));
		panelMilieu = new JPanel();
		panelBas = new JPanel(new GridLayout(1,3));
		// Labels
		lblAddSensor = new JLabel("Ajouter un capteur :", SwingConstants.CENTER);
		lblSensorType = new JLabel("Type de capteur", SwingConstants.CENTER);
		lblSensorSelection = new JLabel("<html><center>Selection du<br>capteur</center></html>", SwingConstants.CENTER);
		lblStart = new JLabel("Debut", SwingConstants.CENTER);
		lblEnd = new JLabel("Fin", SwingConstants.CENTER);
		lblAddSensor.setFont(new Font("Serif", Font.BOLD, 20));
		emptyLabel = new JLabel("");

		panelTextes.add(lblSensorType);
		panelTextes.add(lblSensorSelection);
		panelTextes.add(lblStart);
		panelTextes.add(lblEnd);
		panelTextes.add(emptyLabel);
		
		// Combo boxes
		cbbSensorType = new JComboBox<>();
		btnSensorSelection = new JButton("Aucun élément sélectionné");
		btnStart = new JButton("Non renseigné");
		btnEnd = new JButton("Non renseigné");
		// Some combo boxes are disabled at the beginning
		btnSensorSelection.setEnabled(false);
		btnStart.setEnabled(false);
		btnEnd.setEnabled(false);
		
		
		
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
				createSelectorFromType(selected);
				btnSensorSelection.setEnabled(true);
				btnStart.setEnabled(true);
				// Initialise the defaul value of start
				setInitialStartValue();
				btnEnd.setEnabled(true);
				btnSensorSelection.setVisible(true);
//				later.add(btnSensorSelection);
			}
		});

		// Action sur le bouton Start
		tStart = new TimeChooser(btnStart);
		
		// Action sur le bouton End
		tEnd = new TimeChooser(btnEnd);
		
		chart = createChart(createCategoryDataset());
		cp = new ChartPanel(chart,true);
		cp.setVisible(true);
		panelMilieu.add(cp);
		
		
		// OK button
		confirmButton = new JButton("OK");
		confirmButton.addActionListener(new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) { // Action sur le bouton OK
				getSelectedItems();
				dateStart = tStart.getDate();
				dateEnd = tEnd.getDate();
				if(dateStart == null || dateEnd == null) {
					JOptionPane.showMessageDialog(null, "Entrez une date/heure valide dans les champs de date.");
				}
				else {
					// Recuperation des informations
					if(selectedSensor1 != null) sensorDateVariation1 = db.getSensorWithDate(selectedSensor1,dateStart,dateEnd);
					if(selectedSensor2 != null) sensorDateVariation2 = db.getSensorWithDate(selectedSensor2,dateStart,dateEnd);
					if(selectedSensor3 != null) sensorDateVariation3 = db.getSensorWithDate(selectedSensor3,dateStart,dateEnd);
					// Creating the chart
					chart = createChart(createCategoryDataset(selectedSensor1,sensorDateVariation1,selectedSensor2,sensorDateVariation2,selectedSensor3,sensorDateVariation3));
					cp = new ChartPanel(chart,true);
					cp.setVisible(false);
					panelMilieu.add(cp);
//					panelMilieu.revalidate();
//					panelMilieu.repaint();
					// Setting the components above to their initial state
					btnSensorSelection = new JButton("Selectionner");
					btnSensorSelection.setEnabled(false);
					btnStart.setEnabled(false);
					btnEnd.setEnabled(false);
				}
			}
		});
		
		// getSensorsWithFluid()
		// When the sensor(s) is (are) selected, generate the chart

		// Adding the combo boxes
		panelBoutons.add(cbbSensorType);
		panelBoutons.add(btnSensorSelection);
		panelBoutons.add(btnStart);
		panelBoutons.add(btnEnd);
		panelBoutons.add(confirmButton);
		
		panelHaut.add(lblAddSensor);
		panelHaut.add(panelTextes);
		panelHaut.add(panelBoutons);
		
		// Label sensors 
		lblSensor1 = new JLabel("",SwingConstants.CENTER);
		lblSensor2 = new JLabel("",SwingConstants.CENTER);
		lblSensor3 = new JLabel("",SwingConstants.CENTER);
		
		panelBas.add(lblSensor1);
		panelBas.add(lblSensor2);
		panelBas.add(lblSensor3);
		
		
		this.add(panelHaut,BorderLayout.NORTH);
		this.add(panelMilieu,BorderLayout.CENTER);
		this.add(panelBas,BorderLayout.SOUTH);
	}
	
	private void setInitialStartValue() {
		Date firstDate = db.getFirstDate();
		if(firstDate != null) {
			tStart.setDate(firstDate);
		}
	}
	
}
