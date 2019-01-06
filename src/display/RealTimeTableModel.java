package display;

import java.util.ArrayList;
import java.util.Iterator;
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
		int iCol;
		switch(indiceColonne) {
		case 0: iCol = 0; break;
		case 1: iCol = 5; break;
		case 2: return data.get(indiceLigne).get(1) + " - Etage " + data.get(indiceLigne).get(2) + " - " + data.get(indiceLigne).get(3);
		case 3: iCol = 4; break;
		case 4: iCol = 6; break;
		case 5: iCol = 7; break;
		default : iCol = indiceColonne; break;
		}
		return data.get(indiceLigne).get(iCol);
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