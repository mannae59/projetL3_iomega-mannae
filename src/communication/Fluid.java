package communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Fluid {
	private Map<String,String> map = new HashMap<>();
	
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
}
