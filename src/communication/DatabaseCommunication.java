package communication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import update.Observable;
import update.Observer;

public class DatabaseCommunication implements Observable {
	private List<Observer> tabObserver;

	public DatabaseCommunication() {
		System.out.println("Database initialized.");
	}
	
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
		// Has to search first if the entry does not exist before creating a new one (calling setSensorConnection())
		System.out.println("[DB] Entry added to the database : Name = " + sensorName + " Info = " + sensorInfo);
	}
	public void setSensorConnection(String sensorName, boolean isConnected) {
		// TODO Complete this method
		System.out.println("[DB] Entry changed to the database : Name = " + sensorName + " Connected = " + isConnected);
	}
	public void setSensorValue(String sensorName, String value, Date date) {
		// TODO Complete this method
		System.out.println("[DB] Entry changed to the database : Name = " + sensorName + " Value = " + value + " Date = " + date.toString());
	}
	public void setSensorMinLimit(String sensorName, float minLimit) {
		// TODO Complete this method
	}
	public void setSensorMaxLimit(String sensorName, float maxLimit) {
		// TODO Complete this method
	}
	public List<String> getConnectedSensors() {
		// TODO Complete this method
		List<String> list = new ArrayList<String>();
		list.add("sensor1:ELECTRICITE:U3:2:U3-205:6:0:10:True");
		list.add("sensor2:ELECTRICITE:U3:2:U3-206:-4:0:10:True");
		list.add("sensor3:ELECTRICITE:U3:2:U3-207:13:0:10:True");
		return list;
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
