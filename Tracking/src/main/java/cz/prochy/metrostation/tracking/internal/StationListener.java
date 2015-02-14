package cz.prochy.metrostation.tracking.internal;

/**
 * General interface for listener observing station events. All events act as a state transition
 * so you can expect that each will be called just once in a row (e.g. you will not receive
 * the same event multiple times).
 */
public interface StationListener {

    public final static StationGroup NO_STATIONS = new StationGroup();

    /**
     * There has been a change in GSM cells and this set represents possible stations where we are. When empty
     * cell ids do not map to any known station.
     * @param stations Station.
     */
	void onStation(StationGroup stations, StationGroup predictions);

    /**
     * We have been disconnected from network. If last cell was a station this probably
     * means we are in a tunnel travelling to next station.
     */
	void onDisconnect();
	
}
