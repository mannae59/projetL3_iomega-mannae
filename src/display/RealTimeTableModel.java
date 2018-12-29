package display;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class RealTimeTableModel extends AbstractTableModel{
	String[][] data;
	public RealTimeTableModel(String[][] data) {
		this.data = data;
	}
	public int getColumnCount() {
		return data[0].length;
	}
	public int getRowCount() {
		return data.length;
	}
	public Object getValueAt(int indiceLigne, int indiceColonne) {
		return data[indiceLigne][indiceColonne];
	}
	public String getColumnName(int indiceColonne) {
		switch(indiceColonne) {
		case 0 : return "Nom";
		case 1 : return "Type";
		case 2 : return "Localisation";
		case 3 : return "Valeur";
		default : return null;
		}
	}
	public void setValueAt(Object val, int indiceLigne, int indiceColonne) {
		// Does nothing, because the values should not be modified.
			
		}
	public boolean isCellEditable(int indiceLigne, int indiceColonne) {
		return false; // No cell is editable
	}
}