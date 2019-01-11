package display;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
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
	private static final long serialVersionUID = 1L;
	private DatabaseCommunication db;
	private Fluid fluid = new Fluid();

	private List<String> sensorDateVariation1;
	private List<String> sensorDateVariation2;
	private List<String> sensorDateVariation3;
	private List<String> sensorsAvailable; // Sensors got from the choice of the fluid on the 'later' panel
	private JPopupMenu menu;
	
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
	private JCheckBoxMenuItem j;
	private JLabel lblSensorSelection;
	private JLabel emptyLabel;
	private JLabel lblSensor1;
	private JLabel lblSensor2;
	private JLabel lblSensor3;
	
	private JComboBox<String> cbbSensorType;
	private JLabel lblStart;
	private JLabel lblEnd;
	private JToggleButton btnSensorSelection;
	private JToggleButton btnStart;
	private JToggleButton btnEnd;
	private JButton confirmButton;
	private Date dateStart;
	private Date dateEnd;
	private TimeChooser tStart;
	private TimeChooser tEnd;
	private DefaultCategoryDataset dataset;
	private JFreeChart chart;
	private ChartPanel cp;
	Date firstDate;
	
	public LaterTab(DatabaseCommunication db, Fluid f) {
		super(new BorderLayout());
		this.db = db;
		this.fluid = f;
		initTabLater();
	}

	// Not using varargs because of heap risk
	private CategoryDataset createDataset(String name1, List<String> sensor1, String name2, List<String> sensor2, String name3, List<String> sensor3) {
		dataset = new DefaultCategoryDataset();
		
		if(name1 != null) {
			for(String value : sensor1) {
				String[] data = value.split(":",2);
				dataset.setValue(Double.parseDouble(data[0]), name1 , data[1]);
			}
		}
		if(name2 != null) {
			for(String value : sensor2) {
				String[] data = value.split(":",2);
				dataset.setValue(Double.parseDouble(data[0]), name2 , data[1]);
			}
		}
		if(name3 != null) {
			for(String value : sensor3) {
			String[] data = value.split(":",2);
			dataset.setValue(Double.parseDouble(data[0]), name3 , data[1]);
		}
	}
		
		
		return dataset;
	}
	
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
		btnSensorSelection.addActionListener(e -> {
		        if (btnSensorSelection.isSelected()) {
		            Point p = btnSensorSelection.getLocationOnScreen();
		            menu.setInvoker(btnSensorSelection);
		            menu.setLocation((int) p.getX(), (int) p.getY() + btnSensorSelection.getHeight());
		            menu.setVisible(true);
		        } else {
		            menu.setVisible(false);
		        }

		    });
	}
	// ActionListener menu item
	// Securite : empeche la selection de plus de 3 elements a la fois
	private class OpenAction implements ActionListener {    
	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	getSelectedItems();
    		for(Component i : menu.getComponents()) {
				if(i instanceof JCheckBoxMenuItem) {
					j = (JCheckBoxMenuItem) i;
			    		if(!j.isSelected())
			    			j.setEnabled(selectedSensor3 == null);
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
			lblSensor1.setText("Capteur 1 selectionne : " + selectedSensor1);
		} else {
			selectedSensor1 = null;
			lblSensor1.setText("");
		}
		if(nbSelected > 1) {
			selectedSensor2 = list.get(1);
			lblSensor2.setText("Capteur 2 selectionne : " + selectedSensor2);
		} else {
			selectedSensor2 = null;
			lblSensor2.setText("");
		}
		if(nbSelected > 2) {
			selectedSensor3 = list.get(2);
			lblSensor3.setText("Capteur 3 selectionne : " + selectedSensor3);
		} else {
			selectedSensor3 = null;
			lblSensor3.setText("");
		}
		if(nbSelected == 0) btnSensorSelection.setText("Aucun element selectionne");
		else if(nbSelected == 1) btnSensorSelection.setText("Un element selectionne");
		else btnSensorSelection.setText(nbSelected + " elements selectionnes");
		
	}

	public boolean isBeforeNow(Date dateToTest) {
		Date date = new Date();
		return (date.after(dateToTest));
	}
	
	public void initTabLater() {
		// Cacher les menus ouverts lorsqu'on clique a l'exterieur de ceux ci
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(menu != null) menu.setVisible(false);
				if(tStart != null) tStart.setVisible(false);
				if(tEnd != null) tEnd.setVisible(false);
				btnSensorSelection.setSelected(false);
				btnStart.setSelected(false);
				btnEnd.setSelected(false);
			}
		});
		
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
		btnSensorSelection = new JToggleButton("Aucun element selectionne");
		btnStart = new JToggleButton("Non renseigne");
		btnEnd = new JToggleButton("Non renseigne");
		// Some combo boxes are disabled at the beginning
		btnSensorSelection.setEnabled(false);
		btnStart.setEnabled(false);
		btnEnd.setEnabled(false);
		
		
		
		// Adding the elements to the combo boxes
		Set<String> list = fluid.keySet();
		for(String listItem : list) {
			cbbSensorType.addItem(listItem);
		}
		
		// What happens when a type is selected
		cbbSensorType.addActionListener(new AbstractAction(){
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				String selected = cbbSensorType.getSelectedItem().toString();
				createSelectorFromType(selected);
				btnSensorSelection.setText("Aucun element selectionne");
				btnSensorSelection.setEnabled(true);
				btnStart.setEnabled(true);
				// Initialise the defaul value of start
				setInitialStartValue();
				btnEnd.setEnabled(true);
			}
		});

		// Action sur le bouton End
		tEnd = new TimeChooser(btnEnd,null);
		// Action sur le bouton Start
		tStart = new TimeChooser(btnStart,tEnd);
		
		
		chart = createChart(createDataset(null,null,null,null,null,null));
		cp = new ChartPanel(chart,true);
		cp.setVisible(false);
		panelMilieu.add(cp);
		
		
		// OK button
		confirmButton = new JButton("OK");
		confirmButton.addActionListener(new AbstractAction(){
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) { // Action sur le bouton OK
				getSelectedItems();
				dateStart = tStart.getDate();
				dateEnd = tEnd.getDate();
				if(selectedSensor1 == null) {
					JOptionPane.showMessageDialog(null, "Aucun capteur n'a ete selectionne.");
				}
				else if((dateStart == null || dateEnd == null)) {
					JOptionPane.showMessageDialog(null, "Entrez une date/heure valide dans les champs de date.");
				}
				else if(!isBeforeNow(dateEnd)) {
					JOptionPane.showMessageDialog(null, "Erreur : La date/heure de fin est ulterieure a celle actuelle.");
				}
				else if (dateStart.after(dateEnd)) {
					JOptionPane.showMessageDialog(null, "Erreur : La date/heure de debut est apres la date/heure de fin.");
				}
				else {
					// Recuperation des informations
					if(selectedSensor1 != null) sensorDateVariation1 = db.getSensorWithDate(selectedSensor1,dateStart,dateEnd);
					if(selectedSensor2 != null) sensorDateVariation2 = db.getSensorWithDate(selectedSensor2,dateStart,dateEnd);
					if(selectedSensor3 != null) sensorDateVariation3 = db.getSensorWithDate(selectedSensor3,dateStart,dateEnd);
					// Creating the chart
					chart = createChart(createDataset(selectedSensor1,sensorDateVariation1,selectedSensor2,sensorDateVariation2,selectedSensor3,sensorDateVariation3));
					cp.setChart(chart);
					cp.setVisible(true);
				}
			}
		});

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
		firstDate = db.getFirstDate();
		if(firstDate != null) {
			tStart.setDate(firstDate);
		}
	}
	
}
