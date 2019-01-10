package display;

import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class RealTimeTableModel extends AbstractTableModel{
	private List<List<String>> data = null;
	public RealTimeTableModel(List<List<String>> data) {
		this.data = data;
	}
	
	@Override
	public int getColumnCount() {
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
	
	
	public void update(List<List<String>> data) {
		int nbRows = Integer.min(data.size(),this.data.size());
			for(int i = 0; i < nbRows; i++) {
				for(int j = 0; j < this.data.get(0).size(); j++) {
					if(this.data.get(i).get(j) != data.get(i).get(j)) {
						this.data.get(i).set(j,data.get(i).get(j));
					}
				}
		}
		
		if(this.data.size() < data.size()) { // Il y a des nouvelles lignes
			for(int i = this.data.size(); i < data.size(); i++) {
				this.data.add(data.get(i));
			}
			fireTableRowsInserted(this.data.size(),data.size()-1);
		}
		else if(this.data.size() > data.size()){ // Il y a des lignes en moins
			for(int i = data.size(); i < this.data.size(); i++) {
				this.data.remove(data.size());
			}
		}
		fireTableDataChanged();
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
		if(val instanceof String) {
			String value = (String) val;
			data.get(indiceLigne).set(indiceColonne, value);
		}
	}
	
	@Override
	public boolean isCellEditable(int indiceLigne, int indiceColonne) {
		return false; // No cell is editable
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<List<String>> data) {
		this.data = data;
		fireTableDataChanged();
	}
}