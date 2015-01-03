package cz.prochy.metrostation.tracking;

import net.jcip.annotations.NotThreadSafe;

import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public class CompositeStationListener implements StationListener {
	
	private final Set<StationListener> listeners = new HashSet<StationListener>();

	public void addListener(StationListener listener) {
		listeners.add(Check.notNull(listener));
	}
	
	@Override
	public void onStation(String station) {
        Check.notNull(station);
		for (StationListener listener : listeners) {
			listener.onStation(station);
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
