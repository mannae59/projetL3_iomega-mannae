package communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Fluid {
	private Map<String,String> map = new HashMap<>();
	public static final String EAU = "EAU";
	public static final String ELECTRICITE = "ELECTRICITE";
	public static final String TEMPERATURE = "TEMPERATURE";
	public static final String AIRCOMPRIME = "AIRCOMPRIME";
	public Fluid(){
		map.put(EAU,"m3");
		map.put(ELECTRICITE,"kWh");
		map.put(TEMPERATURE, "°C");
		map.put(AIRCOMPRIME,"m3/h");
	}
	// Returns the keys (not the values)
	public Set<String> keySet() {
		return map.keySet();
	}
	// Returns the values (i.e the units) - not the keys.
	public Collection<String> values() {
		return map.values();
	}
	
	public String get(String fluid) {
		return map.get(fluid.toUpperCase());
	}
	public String valueOf(String key) {
		if(map.keySet().contains(key.toUpperCase())) {
			return key;
		}
		return null;
	}
	public int getIndex(String element) {
		if(map.containsKey(element)) {
			int i = 0;
			for(String item : keySet()) {
				if(item.equals(element)) return i;
				else i++;
			}
		}
		return -1;
	}
}
