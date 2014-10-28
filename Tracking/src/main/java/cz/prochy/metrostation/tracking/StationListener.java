package cz.prochy.metrostation.tracking;

public interface StationListener {
	
	void onStation(String station);
	void onUnknownStation();
	void onDisconnect();
	
}
