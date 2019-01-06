package update;

public interface Observable {
	public void addObserver(Observer o);
	public void notifyObserver();
	public void deleteObserver(Observer o);
}
