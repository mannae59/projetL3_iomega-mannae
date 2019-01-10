package display;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class AskPort extends JFrame {
	int port = 0;
	public AskPort() {
		// New window asking the port
		setSize(200,150); // Taille de la fenetre : 640 x 480
		setLocationRelativeTo(null); // Centrage
		setCloseOperation(); // Closes the database connection before exiting
		setLayout(null);
		
		// Ajout du label
		JLabel label = new JLabel("Entrez le numero de port :");
		label.setBounds(20,10,150,20);
		add(label);
		
		// Ajout de la zone de texte
		JTextField text = new JTextField("5678");
		// Creation de l'action "lancer la fenetre principale"
		Action lancerProgramme = createAction(text);
		text.addActionListener(lancerProgramme);
		text.setBounds(25,50,50,20);
		add(text);
		// Ajout du bouton OK
		JButton okButton = new JButton("OK");
		// Action a effectuer lors de l'appui du bouton OK
		okButton.addActionListener(lancerProgramme);
		okButton.setBounds(100,50,75,20);
		add(okButton);
	}
	
	public int getPort() {
		return port;
	}
	
	public void setCloseOperation() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		WindowListener exitListener = new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent e) {
				System.exit(0);
		    }
		};
		addWindowListener(exitListener);
	}
	
	// Cree l'action liee au champ et au bouton : ici cache la fenetre et lance la nouvelle fenetre
	// apres avoir recupere le port
	private Action createAction(JTextField text) {
		return new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						port = Integer.parseInt(text.getText());
						if(port < 1 || port > 65535) {
							throw new IllegalArgumentException("Port doit etre compris entre 1 et 65535");
						}
						dispose();
					}
					catch(IllegalArgumentException n) {
						JOptionPane.showMessageDialog(null,  "\"" + text.getText() + "\" n'est pas un nombre valide.\nEntrez un numero de port valide.");
					}
				}
			};
	}
	
}
