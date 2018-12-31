package display;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import communication.SimulatorsCommunication;

@SuppressWarnings("serial")
public class AskPort extends JFrame {
	int port = 0;
	public AskPort() {
		// New window asking the port
		setSize(200,150); // Taille de la fenetre : 640 x 480
		setLocationRelativeTo(null); // Centrage
		setDefaultCloseOperation(EXIT_ON_CLOSE); // Appui sur la croix -> quitter
		setLayout(null);
		
		// Ajout du label
		JLabel label = new JLabel("Entrez le numéro de port :");
		label.setBounds(20,10,150,20);
		add(label);
		
		// Ajout de la zone de texte
		JTextField text = new JTextField();
		// Creation de l'action "lancer la fenetre principale"
		Action lancerProgramme = createAction(text);
		text.addActionListener(lancerProgramme);
		text.setBounds(25,50,50,20);
		add(text);
		// Ajout du bouton OK
		JButton OKbutton = new JButton("OK");
		// Action a effectuer lors de l'appui du bouton OK
		OKbutton.addActionListener(lancerProgramme);
		OKbutton.setBounds(100,50,75,20);
		add(OKbutton);
	}
	// Cree l'action liee au champ et au bouton : ici cache la fenetre et lance la nouvelle fenetre
	// apres avoir recupere le port
	private Action createAction(JTextField text) {
		return new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						port = Integer.parseInt(text.getText());
						setVisible(false);
						SimulatorsCommunication.startWindows(port);
					}
					catch(NumberFormatException n) {
						JOptionPane.showMessageDialog(null,  "Entrez un numéro de port valide.");
					}
				}
			};
	}
	
}
