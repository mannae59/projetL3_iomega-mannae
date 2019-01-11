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
	
	
	private ConfigurationTreePanel configuration;
	private LaterTab laterTab;
	private DatabaseCommunication db;
	private Fluid fluid;
	private Timer timer;
	private int ok =1;
	private boolean stop=false;
	private RealTime realTime;
	private JPanel askPort;
	private JPopupMenu menu; // Also needs to be declared here
	
	public Display(DatabaseCommunication db){
		this.db = db;
		this.fluid = new Fluid();
		
		initUI();

 		//demarrer le thread actualisation
 		timer =new Timer();
 		timer.start();
	}
	
	
	
	public void setCloseOperation() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);//on ferme la frame quand on appuie sur la croix
		WindowListener exitListener = new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent e) {
				ok=0;
				while(!stop) {
					//attendre que le thread Timer se termine
				}
				db.close();				
				setVisible(false);
		    }
		};
		
		addWindowListener(exitListener);
	}

	    
	
	private void initUI(){
		setTitle("Interface Neocampus"); // Titre de la fenetre
		setSize(1024,768); // Taille de la fenetre : 640 x 480
		setLocationRelativeTo(null); // Centrage
		setCloseOperation(); // Sets the action performed when we click on the 'X'
		JTabbedPane tabs = new JTabbedPane();
		realTime = new RealTime(db);
		laterTab = new LaterTab(db,fluid);
		configuration =new ConfigurationTreePanel(db,this);
		tabs.addTab("Temps reel",realTime);
		tabs.addTab("A posteriori",laterTab);
		tabs.addTab("Configuration",configuration);
		add(tabs);
	}
	
	/* Les connexions / deconnexions se font trop vite on est donc obliges d'utiliser un thread a part
	 * pour 'reguler' les update  */
	public void update(int i) {
		switch (i) {
		case 0: 
			// seuil change             
			configuration.updateTree();//recupere la map et maj du tree
            
            break;
		case 1: //donnee mise a jour
           
//            realTime.z(); //update le tableau si cest les donnees de capteurs concerne par l'affichage
			//realTime.updateTabRealTime();
            //appel getSensorWithDate(String sensorName, Date start, Date stop) et met a jour les attributs de Display 
            break;
		case 2: // case 2: nouvelle connection 
           
            configuration.updateTree();
            break;
		
			
		}
		
        
	}
	
	
	private class Timer extends Thread{
		@Override
        public void run(){
        	while(ok==1) {
        		try {
					sleep(1000);
					realTime.updateTabRealTime();
				} catch (InterruptedException e) {
					interrupt();
				}
        	}
        	stop =true;
			System.exit(0);

        }

    }
	
}
