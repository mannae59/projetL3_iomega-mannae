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
			String[] s2 = c2.split(":");
			return s1[0].compareTo(s2[0]);
		}
		
	}
	
	public static class TypeComparator implements Comparator<String>{
		@Override
		public int compare(String c1, String c2) {
			String[] s1 = c1.split(":");
			String[] s2 = c2.split(":");
			int difference = s1[5].compareTo(s2[5]);
			if(difference == 0) difference = s1[0].compareTo(s2[0]);
			return difference;
		}
		
	}
	
	public static class BuildingComparator implements Comparator<String>{
		@Override
		public int compare(String c1, String c2) {
			String[] s1 = c1.split(":");
			String[] s2 = c2.split(":");
			int difference = s1[1].compareTo(s2[1]);
			if(difference == 0) difference = s1[0].compareTo(s2[0]);
			return difference;
		}
		
	}
	
	
	public static List<String> filterSensorsRealTime(List<String> sensorsConnected, int sort) {
		TreeSet<String> data;
		switch(sort) {
		case 0: data = new TreeSet<>(new NameComparator());break;
		case 1: data = new TreeSet<>(new TypeComparator());break;
		case 2: data = new TreeSet<>(new BuildingComparator());break;
		default:JOptionPane.showMessageDialog(null,  "Erreur : ce mode de tri n'est pas supporte.");
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
			String item = it.next();
			sortedData.add(item);
		}
		return sortedData;
	}
	public static Map<String,Map<String,List<String>>> filterByLocation() {
		// TODO Complete this method
		return null;
	}
}
