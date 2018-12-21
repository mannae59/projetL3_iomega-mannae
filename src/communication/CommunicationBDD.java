package communication;

import java.util.Date;
import java.util.List;

import update.Observable;
import update.Observer;

public class CommunicationBDD implements Observable {
	private List<Observer> tabObserver;

	public void addObserver(Observer o) {
		// TODO Complete this method
	}
	public void notifyObserver(Observer o) {
		// TODO Complete this method
	}
	public void deleteObserver(Observer o) {
		// TODO Complete this method
	}
	public void addNewSensor(String sensorName, String sensorInfo) {
		// TODO Complete this method
	}
	public void setSensorConnection(String sensorName, boolean isConnected) {
		// TODO Complete this method
	}
	public void setSensorValue(String sensorName, float value, Date date) {
		// TODO Complete this method
	}
	public void setSensorMinLimit(String sensorName, float minLimit) {
		// TODO Complete this method
	}
	public void setSensorMaxLimit(String sensorName, float maxLimit) {
		// TODO Complete this method
	}
	public List<String> getSensorsConnected() {
		// TODO Complete this method
		return null;
	}
	public List<String> getSensorsWithFluid(String fluidType) {
		// TODO Complete this method
		return null;
	}
	public List<String> getSensorWithDate(String sensorName, Date start, Date stop) {
		// TODO Complete this method
		return null;
	}
	public List<String> getAllSensors() {
		// TODO Complete this method
		return null;
	}
}
