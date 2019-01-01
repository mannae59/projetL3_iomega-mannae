package display;

import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class RealTimeTableModel extends AbstractTableModel{
	private List<List<String>> data;
	public RealTimeTableModel(List<List<String>> data) {
		this.data = data;
	}
	
	@Override
	public int getColumnCount() {
		if(getRowCount()==0) {
			return 0;
		}
		return data.get(0).size();
	}
	
	@Override
	public int getRowCount() {
		if(data == null) {
			return 0;
		}
		return data.size();
	}
	
	@Override
	public Object getValueAt(int indiceLigne, int indiceColonne) {
		return data.get(indiceLigne).get(indiceColonne);
	}
	
	@Override
	public String getColumnName(int indiceColonne) {
		switch(indiceColonne) {
		case 0 : return "Nom";
		case 1 : return "Type";
		case 2 : return "Localisation";
		case 3 : return "Valeur";
		default : return null;
		}
	}
	
	@Override
	public void setValueAt(Object val, int indiceLigne, int indiceColonne) {
		// Does nothing, because the values should not be modified.
	}
	
	@Override
	public boolean isCellEditable(int indiceLigne, int indiceColonne) {
		return false; // No cell is editable
	}
}