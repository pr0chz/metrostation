package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifier;

public class NotifyListener implements StationListener {

    private final Notifier notifier;

    private StationGroup lastStations = StationGroup.empty();
    private StationGroup lastPredictions = StationGroup.empty();

    public NotifyListener(Notifier notifier) {
        this.notifier = Check.notNull(notifier);
    }

    @Override
    public void onStation(long ts, StationGroup stations, StationGroup predictions) {
        Check.notNull(stations);
        Check.notNull(predictions);
        lastStations = stations;
        lastPredictions = predictions;
        if (stations.hasSingleValue()) {
            notifier.onStation(stations.getStation().getName());
        } else if (stations.isEmpty()) {
            notifier.onUnknownStation();
        }
    }

    @Override
    public void onDisconnect(long ts) {
        if (lastStations.hasSingleValue()) {
            if (lastPredictions.hasSingleValue()) {
                notifier.onDisconnect(lastStations.getStation().getName(), lastPredictions.getStation().getName());
            } else {
                notifier.onDisconnect(lastStations.getStation().getName());
            }
        }
    }
}
