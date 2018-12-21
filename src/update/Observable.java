package update;

public interface Observable {
	public void addObserver(Observer o);
	public void notifyObserver(Observer o);
	public void deleteObserver(Observer o);
}
