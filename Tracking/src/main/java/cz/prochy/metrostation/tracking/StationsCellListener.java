package cz.prochy.metrostation.tracking;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class StationsCellListener implements CellListener {

    private final static String INITIAL = "~~INITIAL~~";
	private final static String UNKNOWN_STATION = "~~UNKNOWN~~";
    private final static String DISCONNECTED = "~~DISCONNECTED~~";

	private final Stations stations;
	private final StationListener listener;

    private String state = INITIAL;

    public StationsCellListener(Stations stations, StationListener listener) {
		this.stations = Check.notNull(stations);
		this.listener = Check.notNull(listener);
	}
	
	@Override
	public void cellInfo(int cid, int lac) {
		if (stations.isStation(cid, lac)) {
			String name = stations.getName(cid, lac);
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
