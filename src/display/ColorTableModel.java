package display;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class ColorTableModel extends DefaultTableCellRenderer {
	public ColorTableModel() {
		super();
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object text, boolean isSelected, boolean hasFocus, int row, int column) {
		setText(String.valueOf(text));
		setHorizontalAlignment(JLabel.CENTER);
		
		String value = ((RealTimeTableModel)table.getModel()).getValueAt(row, 8).toString();
		if(value.equals("1")) {
			setBackground(Color.RED);
		}
		else {
			setBackground(Color.WHITE);
		}
		setForeground(Color.BLACK);
		return this;
	}
}
