package display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import communication.DatabaseCommunication;

public class ConfigurationTreePanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane ;
	private JPanel panelGauche;
	private JPanel panelDroite;
	private JTree arbre;
	private JButton modif ;
	private JLabel nom;
	private JLabel type;
	private JTable tabLoc;
	private JLabel min;
	private JLabel max;
	private JLabel sMin;
	private JLabel sMax;
	private JTextField tMin;
	private JTextField tMax;
	private JPanel panelModif;
	private JPanel panelButton ;
	private DefaultTableModel tableur;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode racine =null;
	//model tableau non editable
	private class NonEditableDefaultTableModel extends DefaultTableModel {
		 
		public boolean isCellEditable(int iRowIndex, int iColumnIndex) { 
	 
			return false; 
		}
	}
	//mettre element du tableau en gras
	private class MyRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1147415359404633003L;
	 
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
							boolean hasFocus, int row, int column)  {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(JLabel.CENTER); 
			
			if (row == 0) {
				this.setFont(new Font("Verdana", Font.BOLD, 12));
			}
			else {
				this.setFont(new Font("Verdana",Font.ITALIC,12));
			}
			return this;
		}
	}
	private Map<String,Map<String,List<String>>> treeGestionSensor = new TreeMap<>();
	private Map<String,String> nomInfoCapteur; // <Nom,lieu:typeFluide:seuilMin:seuilMax:bat:etage>
	private String selectedSensor=null;
	
	DatabaseCommunication db;
	public ConfigurationTreePanel(DatabaseCommunication db,JFrame fenetre) {
		
		super(new BorderLayout());
		nomInfoCapteur =new TreeMap<>();
		this.db=db;
		//creation panel
		panelGauche = new JPanel(new BorderLayout());
		panelDroite = new JPanel(new GridLayout(7,1 ));
		//localisation panel pour le border layout
		sMax =new JLabel("changer seuil MAX", SwingConstants.CENTER);
		sMin=new JLabel("changer seuil MIN", SwingConstants.CENTER);
		tMax =new JTextField("");
		tMin = new JTextField("");
		modif = new JButton("MODIFIER");
		modif.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changerSeuil();
				
			}
		});
		
		panelButton = new JPanel();
		panelModif= new JPanel(new GridLayout(2,2));
		panelModif.add(sMin);
		panelModif.add(sMax);
		panelModif.add(tMin);
		panelModif.add(tMax);
		panelButton.add(modif);
		
		
		
		
		
		panelDroite.setBackground(Color.white); //couleur
		//creation cote droit
		nom = new JLabel("NOM :", SwingConstants.CENTER);
		//nom.setAlignmentX(Component.CENTER_ALIGNMENT);
		type = new JLabel("TYPE :", SwingConstants.CENTER);
		
		tableur = new NonEditableDefaultTableModel();
		tableur.addColumn("Batiment");
		tableur.addColumn("Etage");
		tableur.addColumn("Lieu");
		tableur.setRowCount(2);
		tabLoc = new JTable(tableur);
		//mettre en gras
		tabLoc.setDefaultRenderer(Object.class,new MyRenderer());
		tabLoc.setValueAt("BATIMENT", 0, 0);
		tabLoc.setValueAt("ETAGE", 0, 1);
		tabLoc.setValueAt("LIEU", 0, 2);
		// On n'autorise pas la sélection d'une ligne entière
		tabLoc.setRowSelectionAllowed(false);		 
		// Sélection d'une colonne impossible
		tabLoc.setColumnSelectionAllowed(false);
		tabLoc.setCellSelectionEnabled(false);
				
				
		min = new JLabel("SEUIL MIN :", SwingConstants.CENTER);
		max = new JLabel("SEUIL MAX :", SwingConstants.CENTER);
		
		panelDroite.add(nom);
		panelDroite.add(type);
		panelDroite.add(tabLoc);
		panelDroite.add(min);
		panelDroite.add(max);
		panelDroite.add(panelModif);
		panelDroite.add(panelButton);
		
		
		panelGauche.setMinimumSize(panelGauche.getMaximumSize());
		
		//cheat.setVisible(false);
		
		//CREATION ARBRE
		
		racine =initialiserRacine();
		
		
		
		arbre = new JTree(racine) ; 
		arbre.setRootVisible(false);
		model = (DefaultTreeModel) arbre.getModel();
		
		
		
		
		
		
		//ICONE leaf
		ImageIcon leafIcon = new ImageIcon("rec.png");
		if (leafIcon != null) {
		    DefaultTreeCellRenderer renderer1 = 
		        new DefaultTreeCellRenderer();
		    renderer1.setLeafIcon(leafIcon);
		    arbre.setCellRenderer(renderer1);
		}
		
		
		
		//LISTENER permet d'ecouter si quelqu'un selectionne un noeud
		arbre.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode noeud =
						(DefaultMutableTreeNode) arbre.getLastSelectedPathComponent();
				if(noeud == null)
					return ; 
				if(noeud.isLeaf()) {
					selectedSensor = noeud.toString();
					
					String infoSelected =nomInfoCapteur.get(selectedSensor);
					//info selected format <Nom,lieu:typeFluide:seuilMin:seuilMax:bat:etage>
					String[] splitInfoSelected = infoSelected.split(":");
					nom.setText("NOM : "+selectedSensor);
					tabLoc.setValueAt( splitInfoSelected[4], 1, 0);
					tabLoc.setValueAt( splitInfoSelected[5], 1, 1);
					tabLoc.setValueAt( splitInfoSelected[0], 1, 2);
					type.setText("TYPE : "+splitInfoSelected[1]);
					max.setText("SEUIL MAX : "+splitInfoSelected[3]);
					min.setText("SEUIL MIN : "+splitInfoSelected[2]);
					sMin.setText("");
					sMax.setText("");
					
				}
				
			}
		});
			
		panelGauche.add(arbre, BorderLayout.CENTER);
		
        
        
        

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   panelGauche, panelDroite);
        splitPane.setResizeWeight(0.3);
     
        splitPane.setContinuousLayout(true);
        

        add(splitPane, BorderLayout.CENTER);
        								
		this.add(splitPane);				
		
	}
	
	public void updateTree() {
		racine =initialiserRacine();
		if(racine!=null)
			((DefaultTreeModel) arbre.getModel()).setRoot(racine);
	}
	//pas encore fait la contrainte smin<smax
	public void changerSeuil() {
		String newSmin= tMin.getText();
		
		String newSmax = tMax.getText();
		
		Boolean isNumber;
		//verifier su un capteur a ete selectionne
		if(selectedSensor==null) {
			JOptionPane.showMessageDialog(this,
					"Pas de capteur selectionne ", "ATTENTION ",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		if (!newSmax.equals("")) {
			//verif si la valeur est bonne 
			isNumber =isANumber(newSmax);
			//valeur pas bonne
			if (!isNumber) {
				JOptionPane.showMessageDialog(this,
						"Ce que vous avez rentrer pour Seuil Max n'est pas un nombre ", "ATTENTION ",
						JOptionPane.ERROR_MESSAGE);
				tMax.setText("");
			}
			else {
				Double newMax =Double.parseDouble(newSmax);
				db.setSensorMaxLimit(selectedSensor, newMax);
				max.setText("SEUIL MAX : "+newMax);
			}
		}
		if (!newSmin .equals("")) {
			//verif si la valeur est bonne 
			isNumber =isANumber(newSmin);
			//valeur pas bonne
			if (!isNumber) {
				JOptionPane.showMessageDialog(this,
						"Ce que vous avez rentrer pour Seuil min n'est pas un nombre ", "ATTENTION ",
						JOptionPane.ERROR_MESSAGE);
				tMin.setText("");
			}
			else {
				Double newmin =Double.parseDouble(newSmin);
				db.setSensorMinLimit(selectedSensor, newmin);
				min.setText("SEUIL MIN : "+newmin);
			}
		}
	}
	
	public DefaultMutableTreeNode initialiserRacine() {
		
		
		//on recupere le map de map de liste
		treeGestionSensor=db.getTreeGestionSensor();
		if(treeGestionSensor.isEmpty()) {
			return null;
		}
			
		//instanciation racine
		racine = new DefaultMutableTreeNode() ;
			
		//parcours de treeGestionSensor et ajout dans organisationArbre 
		for(String batiment :treeGestionSensor.keySet() ) {
			//creation noeud batiment
			DefaultMutableTreeNode batC =new DefaultMutableTreeNode(batiment);
			
			Map<String,List<String>> mapEtage = treeGestionSensor.get(batiment);

			for(String etage : mapEtage.keySet()) {
				//creation noeud etage
				DefaultMutableTreeNode etageC =new DefaultMutableTreeNode(etage);
				
				List<String> listeInfoCapteur = mapEtage.get(etage);
				
				for(String infoCapteur : listeInfoCapteur) {
					
					String[] infoSplit = infoCapteur.split(":");
					String nom =  infoSplit[0];
					String info = infoSplit[1]+":"+infoSplit[2]+ ":"+infoSplit[3]+":"+infoSplit[4]+
							":"+infoSplit[5]+":"+infoSplit[6];
					
					//stocker les info de chaque capteur dans une map
					nomInfoCapteur.put(nom, info); // <Nom,lieu:typeFluide:seuilMin:seuilMax:bat:etage>
					
					//creation de la feuille avec le nom du capteur
					DefaultMutableTreeNode nomC =new DefaultMutableTreeNode(nom) ;
					etageC.add(nomC);
				}
				batC.add(etageC);
				
			}
			racine.add(batC);
			
		}
		return racine;
					
	}
	//reconnaitre si un string est un double
	public static boolean isANumber(String chaine){
        try{              
              Double.parseDouble(chaine);
              return true;
        }catch(NumberFormatException nfe){
              return false;
        }
   //MEMO POUR LES AUTRES UPDATE     
  //thread pour timer
	/*Timer timer = new Timer();
	timer.start();

private class Timer extends Thread{
    public void run(){
    	while(true) {
    		try {
    			
    			
    			
				sleep(2000);
				
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
    	}
    	
    }*/

    }
	
	
	

}
