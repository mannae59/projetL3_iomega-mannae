package display;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class AlertWindow extends JFrame {
	public AlertWindow(int nbRedRows, List<List<String>> sensorsList) {
		if(nbRedRows == 0) {
			JOptionPane.showMessageDialog(null,  "Aucune alerte.");
		}
		else {
			initUI(sensorsList);
		}
	}
	private void initUI(List<List<String>> sensorsList) {
		setSize(480,320);
		setTitle("Liste des alertes");
		setLocationRelativeTo(null);
		setLayout(null);
		// Table
		JTable table = new JTable(new RealTimeTableModel(sensorsList) {
			@Override
			public int getColumnCount() {
				return 6;
			}
			
		});
		// Sets the new cell renderer (whitch colors a line if the value exceeds the limits)
 		table.setDefaultRenderer(Object.class,new ColorTableModel());
		JScrollPane sp = new JScrollPane(table);
		Rectangle r = new Rectangle(0,0,getWidth()-15,getHeight());
		r.height -= 80; // Reducing the height to let the button appear
		sp.setBounds(r);
		
		// Close button
		JButton btnClose = new JButton("OK"); 
		btnClose.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent a) {
				dispose();
			}
		});
		btnClose.setBounds(r.width/2-30, r.height + 10, 60, 30);
		
		add(sp);
		add(btnClose);
		setVisible(true);
	}
}
