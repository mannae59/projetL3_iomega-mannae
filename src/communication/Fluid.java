package communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Fluid {
	private Map<String,String> map = new HashMap<>();
	public final static String EAU = "EAU";
	public final static String ELECTRICITE = "ELECTRICITE";
	public final static String TEMPERATURE = "TEMPERATURE";
	public final static String AIRCOMPRIME = "AIRCOMPRIME";
	public Fluid(){
		map.put("EAU","m3");
		map.put("ELECTRICITE","kWh");
		map.put("TEMPERATURE", "°C");
		map.put("AIRCOMPRIME","m3/h");
	}
	public Set<String> keySet() {
		return map.keySet();
	}
	
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
}
