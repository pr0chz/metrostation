package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.CellListener;
import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Station;
import cz.prochy.metrostation.tracking.Stations;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class StationsCellListener implements CellListener {

    private final static Station INITIAL = new Station("~~INITIAL~~");
	private final static Station UNKNOWN_STATION = new Station("~~UNKNOWN~~");
    private final static Station DISCONNECTED = new Station("~~DISCONNECTED~~");

	private final Stations stations;
	private final StationListener listener;

    private Station state = INITIAL;

    public StationsCellListener(Stations stations, StationListener listener) {
		this.stations = Check.notNull(stations);
		this.listener = Check.notNull(listener);
	}
	
	@Override
	public void cellInfo(int cid, int lac) {
		if (stations.isStation(cid, lac)) {
			Station name = stations.getStation(cid, lac);
			if (!state.equals(name)) {
				state = name;
				listener.onStation(state);
			}
		} else {
            if (state != UNKNOWN_STATION) {
                listener.onUnknownStation();
                state = UNKNOWN_STATION;
            }
		}		
	}
	
	@Override
	public void disconnected() {
        if (state != DISCONNECTED) {
            listener.onDisconnect();
            state = DISCONNECTED;
        }
	}
	
	
}
