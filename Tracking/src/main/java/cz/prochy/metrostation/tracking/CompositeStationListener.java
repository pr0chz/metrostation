package cz.prochy.metrostation.tracking;

import java.util.ArrayList;
import java.util.List;

public class CompositeStationListener implements StationListener {
	
	private final List<StationListener> listeners = new ArrayList<StationListener>();

	public void addListener(StationListener listener) {
		listeners.add(Check.notNull(listener));
	}
	
	@Override
	public void onStation(String station) {
		for (StationListener listener : listeners) {
			listener.onStation(Check.notNull(station));
		}
	}

	@Override
	public void onUnknownStation() {
		for (StationListener listener : listeners) {
			listener.onUnknownStation();
		}
	}
	
	@Override
	public void onDisconnect() {
		for (StationListener listener : listeners) {
			listener.onDisconnect();
		}
	}
	

}
