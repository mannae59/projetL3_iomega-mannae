package display;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class ColorTableModel extends DefaultTableCellRenderer {
	Display display;
	JButton button;
	List<List<Color>> color;
	public ColorTableModel(Display display, JButton button, int nbRows) {
		super();
		this.color = new ArrayList<>(4);
		List<Color> subList = new ArrayList<>();
		for(int i = 0; i < nbRows; i++) {
			subList.add(Color.WHITE);
		}
		for(int i = 0; i < 4; i++)
			this.color.add(subList);
		this.display = display;
		this.button = button;
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object text, boolean isSelected, boolean hasFocus, int row, int column) {
		setText(String.valueOf(text));
		setHorizontalAlignment(JLabel.CENTER);
		
		String value = ((RealTimeTableModel)table.getModel()).getValueAt(row, 3).toString();
		double valueExceeds = Double.parseDouble(value.split(" ")[0]);
		
		if(valueExceeds == 1) {
			if(column == 0 && !color.get(column).get(row).equals(Color.RED)) {
				int nbRedRows = Integer.parseInt(button.getText())+1;
				display.setNbRedRows(nbRedRows);
				button.setText((Integer.toString(nbRedRows)));
			}
			setBackground(Color.RED);
			color.get(column).set(row, Color.RED);
		}
		else {
			if(column == 0 && color.get(column).get(row).equals(Color.WHITE)) {
				int nbRedRows = Integer.parseInt(button.getText())-1;
				display.setNbRedRows(nbRedRows);
			}
			setBackground(Color.WHITE);
			color.get(column).set(row, Color.WHITE);
		}
		setForeground(Color.BLACK);
		return this;
	}
}
