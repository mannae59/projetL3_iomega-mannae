package display;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class WindowAlert extends JFrame{
	public WindowAlert(int nbRedRows) {
		if(nbRedRows == 0) {
			JOptionPane.showMessageDialog(null,  "Aucune alerte.");
		}
		else {
			// TODO implement this method.
			JOptionPane.showMessageDialog(null,  "Il y a des alertes !");
		}
	}
}
