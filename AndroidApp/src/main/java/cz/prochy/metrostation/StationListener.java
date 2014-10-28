package cz.prochy.metrostation;

public interface StationListener {
	
	void onStation(String station);
	void onUnknownStation();
	void onDisconnect();
	
}
