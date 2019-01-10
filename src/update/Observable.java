package update;

public interface Observable {
	public void addObserver(Observer o);
	public void notifyObserver(int i);
	public void deleteObserver(Observer o);
}
