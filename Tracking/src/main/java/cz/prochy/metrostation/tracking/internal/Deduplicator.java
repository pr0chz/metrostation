package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;

public class Deduplicator implements StationListener {

    private final static StationGroup INITIAL = new StationGroup();
    private final static StationGroup DISCONNECT = new StationGroup();

    private StationGroup lastStations = INITIAL;
    private StationGroup lastPredictions = INITIAL;

    private final StationListener listener;

    public Deduplicator(StationListener listener) {
        this.listener = Check.notNull(listener);
    }

    @Override
    public void onStation(StationGroup stations, StationGroup predictions) {
        if (!lastStations.equals(stations) || !lastPredictions.equals(predictions)) {
            lastStations = stations;
            lastPredictions = predictions;
            listener.onStation(stations, predictions);
        }
    }

    @Override
    public void onDisconnect() {
        if (lastStations != DISCONNECT) {
            lastStations = DISCONNECT;
            listener.onDisconnect();
        }
    }
}
