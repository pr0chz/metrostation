package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.internal.*;

public class Builder {

    public static CellListener createListener(TrackingStationGraph stations, Notifier notifier, long trackLostTimeoutMs,
                                              long transferTimeoutMs) {

        return new StationsCellListener(
                stations,
                new Tracker(
                        new NotifyListener(new Deduplicator(notifier)),
                        trackLostTimeoutMs,
                        transferTimeoutMs)
                );

    }

}
