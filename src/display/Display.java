package display;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import update.Observable;
import update.Observer;

public class Display implements Observable {
	private List<Observer> tabObserver;
	private List<String> listConnectedSensors;
	private List<String> sensorDateVariation1;
	private List<String> sensorDateVariation2;
	private List<String> sensorDateVariation3;
	// TODO Verifier ces variables : doute sur leur validite --Nicolas
	private List<Date> sensorDateDisplay;
	private TreeMap<String,TreeMap<String,List<String>>> batiments;

	@Override
	public void addObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}
	public void update(Observable o) {
		
	}

	public List<String> getSensorsWithFluid(String fluidType) {
		// TODO Complete this method
		return null;
	}

	public List<String> getSensorWithDate(String sensorName, Date start, Date stop) {
		// TODO Complete this method
		return null;
	}
	public void updateTabRealTime() {
		// TODO Complete this method
	}
	
	public void updateTreeSensor() {
		// TODO Complete this method
	}
	
	public void displayCurveTime() {
		// TODO Complete this method
	}
	
	public void displaySensorsGestion() {
		// TODO Complete this method
	}
}
