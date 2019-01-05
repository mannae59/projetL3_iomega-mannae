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
		return 4;
		// We do not display neither the 'limit exceeded' boolean which
		// allows us to know if we should paint the row in red or not,
		// nor the minimum and maximum limits.
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
		switch(indiceColonne) {
		case 0: return data.get(indiceLigne).get(0);
		case 1: return data.get(indiceLigne).get(5);
		case 2: return data.get(indiceLigne).get(1) + " - Etage " + data.get(indiceLigne).get(2) + " - " + data.get(indiceLigne).get(3);
		case 3: return data.get(indiceLigne).get(4);
		case 4: return data.get(indiceLigne).get(6);
		case 5: return data.get(indiceLigne).get(7);
		default : return data.get(indiceLigne).get(indiceColonne);
		}
		
	}
	
	@Override
	public String getColumnName(int indiceColonne) {
		switch(indiceColonne) {
		case 0 : return "Nom";
		case 1 : return "Type";
		case 2 : return "Localisation";
		case 3 : return "Valeur";
		case 4 : return "Seuil min";
		case 5 : return "Seuil max";
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