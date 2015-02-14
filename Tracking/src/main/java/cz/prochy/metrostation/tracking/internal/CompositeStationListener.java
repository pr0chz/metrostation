package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import net.jcip.annotations.NotThreadSafe;

import java.util.HashSet;
import java.util.Set;

/**
 * This class enables multiple StationListener instances to listen to single event source.
 * Events are propagated to all listeners added to this object.
 */
@NotThreadSafe
public class CompositeStationListener implements StationListener {
	
	private final Set<StationListener> listeners = new HashSet<StationListener>();

	public void addListener(StationListener listener) {
		listeners.add(Check.notNull(listener));
	}
	
	@Override
	public void onStation(StationGroup stations, StationGroup predictions) {
        Check.notNull(stations);
        Check.notNull(predictions);
		for (StationListener listener : listeners) {
			listener.onStation(stations, predictions);
		}
	}

	@Override
	public void onDisconnect() {
		for (StationListener listener : listeners) {
			listener.onDisconnect();
		}
	}
	

}
