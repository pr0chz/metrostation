package cz.prochy.metrostation.tracking.internal;

/**
 * General interface for listener observing station events. All events act as a state transition
 * so you can expect that each will be called just once in a row (e.g. you will not receive
 * the same event multiple times).
 */
public interface StationListener {

    /**
     * We have just connected to a cell which represents this new station.
     * @param station Station name.
     */
	void onStation(String station);

    /**
     * We are connected to a cell but it does not translate to any known station.
     */
	void onUnknownStation();

    /**
     * We have been disconnected from network. If last cell was a station this probably
     * means we are in a tunnel travelling to next station.
     */
	void onDisconnect();
	
}
