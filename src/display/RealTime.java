package display;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import communication.DataFilter;
import communication.DatabaseCommunication;

public class RealTime extends JPanel{
	private static final long serialVersionUID = 1L;
	private JLabel lblTrierPar;
	private JComboBox<String> sortList;
	private RealTimeTableModel rttmodel ;
	private ColorTableModel ctmodel;
	JTable table; 
	private JPanel panelBtnWarning;
	private JPanel panelD;//panel de droite mais pas de panel a gauche pour que la table prennent toute la place
	private JPanel panelTrierPar;
	private JPanel panelSortList;
	private List<List<String>> sensorsOutOfLimit;
	private int nbRedRows = 0;
	private DatabaseCommunication db;
	private List<String> listConnectedSensors;
	private JButton btnWarning; // Buuton on the 'real time' panel which opens the new panel 
	private JButton btnSensorSelection; // Button on the 'later' panel that needs to be declared here
	
	@SuppressWarnings("serial")
	public RealTime(DatabaseCommunication db) {
		super(new BorderLayout());
		this.db=db;
		
		//creation panel gauche avec grid layout
		panelD = new JPanel(new GridLayout(3, 1));
		
		//sert a rendre la taille adapte au label ou checkbox ou warning
		panelTrierPar=new JPanel(new BorderLayout()); //centrer trier par
		panelSortList = new JPanel();
		panelBtnWarning = new JPanel();
		//met le panel droite a l'est du borderlayout
		this.add(panelD, BorderLayout.EAST);
		
				
		// Label "Trier par" (argument2 premet de centrer le label au milieu
		lblTrierPar = new JLabel("Trier par...", SwingConstants.CENTER);
		

				
		// Combo box
		String[] sorting = {"Nom","Type","Batiment"};
		sortList = new JComboBox<>(sorting) ;
		sortList.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				rttmodel.update(splitData(DataFilter.filterSensorsRealTime(listConnectedSensors,sortList.getSelectedIndex())));
				rttmodel.fireTableChanged(null);
				rttmodel.fireTableDataChanged();
				if( table.getColumnModel().getColumnCount() > 3) {
		 			table.getColumnModel().getColumn(0).setWidth(100);
		 			table.getColumnModel().getColumn(1).setPreferredWidth(100);
		 			table.getColumnModel().getColumn(2).setPreferredWidth(150);
		 			table.getColumnModel().getColumn(3).setPreferredWidth(50);
		 		}
			}
		});
		
	 // Table
		//recupere et stocke les capteurs connecte
 		List<List<String>> sensorsList = getConnectedSensors(); // List of sensors connected to display
 		rttmodel = new RealTimeTableModel(sensorsList);
 		table = new JTable(rttmodel);
		// Warning button
		btnWarning = createWarningButton();
		updateSensorsOutOfLimit(sensorsList, btnWarning);

		 		
 		// Sets the new cell renderer (whitch colors a line if the value exceeds the limits)
		ctmodel = new ColorTableModel();
 		table.setDefaultRenderer(Object.class,new ColorTableModel());
 		// Choix de la largeur des colonnes
 		if( table.getColumnModel().getColumnCount() > 3) {
 			table.getColumnModel().getColumn(0).setWidth(100);
 			table.getColumnModel().getColumn(1).setPreferredWidth(100);
 			table.getColumnModel().getColumn(2).setPreferredWidth(150);
 			table.getColumnModel().getColumn(3).setPreferredWidth(50);
 		}
		// Ajout de la table avec scrollPane

 		JScrollPane scroller = new JScrollPane(table);
 		this.add(scroller,BorderLayout.CENTER);
 		
 		//ajout sousPanel plus orientation a l'aide de layout
 		panelTrierPar.add(lblTrierPar,BorderLayout.SOUTH);
 		panelBtnWarning.add(btnWarning);
 		panelSortList.add(sortList);
 		//ajout dans l'ordre des lignes du gridlayout
 		panelD.add(panelTrierPar);
 		panelD.add(panelSortList);
 		panelD.add(panelBtnWarning);
 		
 		
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

	
	private JButton createWarningButton() {
		JButton button = new JButton();
		  try {
		    Image img = ImageIO.read(getClass().getResource("warning.png"));
		    button.setIcon(new ImageIcon(img));
		    button.setHorizontalTextPosition(SwingConstants.CENTER);
		    button.setVerticalTextPosition(SwingConstants.BOTTOM);
		    button.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					new AlertWindow(nbRedRows, sensorsOutOfLimit);
					
				}
			});
		  } catch (IOException ex) {
			  JOptionPane.showMessageDialog(null,"Erreur : Impossible d'acceder au dossier courant.");
		  } catch(IllegalArgumentException e) {
			  JOptionPane.showMessageDialog(null,"Erreur : le logo du bouton Warning est introuvable.");
		  }
		  return button;
	}
	
	
	
	public void setNbRedRows(int nb) {
		this.nbRedRows = nb;
	}
	
	private List<List<String>> getConnectedSensors(){
		listConnectedSensors = db.getConnectedSensors();
		return splitData(listConnectedSensors);
	}
	
	private List<List<String>> splitData(List<String> unsplittedData){
		List<List<String>> data = new ArrayList<>();
		if(unsplittedData != null) {
			for(String sensor : unsplittedData) {
				data.add(Arrays.asList(sensor.split(":")));
			}
		}
		return data;
	}
	
	public void updateTabRealTime() {
		listConnectedSensors = db.getConnectedSensors();
		List<List<String>> data = splitData(DataFilter.filterSensorsRealTime(listConnectedSensors,sortList.getSelectedIndex()));
		rttmodel.update(data);
		updateSensorsOutOfLimit(splitData(listConnectedSensors), btnWarning);
	}
	
}
