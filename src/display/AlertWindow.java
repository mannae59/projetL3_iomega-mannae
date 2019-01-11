package display;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class AlertWindow extends JFrame {
	private JPanel panelTable;
	JTable table;
	JButton btnClose;
	JPanel panelBouton ;
	JScrollPane sp;
	
	public AlertWindow(int nbRedRows, List<List<String>> sensorsList) {
		
		
		if(nbRedRows == 0) {
			JOptionPane.showMessageDialog(null,  "Aucune alerte.");
		}
		else {
			initUI(sensorsList);
		}
	}
	private void initUI(List<List<String>> sensorsList) {
		panelTable= new JPanel(new BorderLayout());
		panelBouton = new JPanel();
		
		setSize(520,320);
		setTitle("Liste des alertes");
		setLocationRelativeTo(null);
		
		// Table
		table = new JTable(new RealTimeTableModel(sensorsList) {
			@Override
			public int getColumnCount() {
				return 6;
			}
			
		});
		// Sets the new cell renderer (whitch colors a line if the value exceeds the limits)
 		table.setDefaultRenderer(Object.class,new ColorTableModel());
 		table.getColumnModel().getColumn(0).setWidth(130);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(70);
		table.getColumnModel().getColumn(4).setPreferredWidth(70);
		table.getColumnModel().getColumn(5).setPreferredWidth(70);
		sp = new JScrollPane(table);
		Rectangle r = new Rectangle(0,0,getWidth()-15,getHeight());
		r.height -= 80; // Reducing the height to let the button appear
		sp.setBounds(r);
		
		// Close button
		btnClose = new JButton("OK"); 
		btnClose.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent a) {
				dispose();
			}
		});
		panelTable.add(sp);
		panelBouton.add(btnClose);
		btnClose.setBounds(r.width/2-30, r.height + 10, 60, 30);
		this.add(panelTable,BorderLayout.CENTER);
		this.add(panelBouton,BorderLayout.SOUTH);

		setVisible(true);
		
	}
}
