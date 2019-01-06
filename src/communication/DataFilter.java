package communication;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JOptionPane;

public class DataFilter {
	
	public static class NameComparator implements Comparator<String>{
		@Override
		public int compare(String c1, String c2) {
			String[] s1 = c1.split(":");
			String[] s2 = c1.split(":");
			return Integer.parseInt(s1[0]) - Integer.parseInt(s2[0]);
		}
		
	}
	
	
	public static List<String> filterSensorsRealTime(List<String> sensorsConnected, int sort) {
		// Copied from "getConnectedSensors"
		TreeSet<String> data;
		switch(sort) {
		case 1: data = new TreeSet<>(new NameComparator());
		case 2: data = new TreeSet<>(new NameComparator());
		default:JOptionPane.showMessageDialog(null,  "Erreur : ce mode de tri n'est pas supporté.");
				data = new TreeSet<>(new NameComparator());
				break;
		}
		if(sensorsConnected != null) {
			for(String sensor : sensorsConnected) {
				data.add(sensor);
			}
		}
		// On renvoie un TreeSet ? Un List<String> ? Un List<List<String>> ?
		List<String> sortedData = new ArrayList<>();
		Iterator<String> it = data.iterator();
		while(it.hasNext()) {
			sortedData.add(it.next());
		}
		return sortedData;
	}
	public static Map<String,Map<String,List<String>>> filterByLocation() {
		// TODO Complete this method
		return null;
	}
}
