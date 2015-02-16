package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.internal.*;

public class Builder {

    public static CellListener createListener(Stations stations, Notifier notifier, long trackLostTimeoutMs) {

        NotifyListener notifyListener = new NotifyListener(notifier);
        Deduplicator deduplicator = new Deduplicator(notifyListener);

        Tracker tracker = new Tracker(deduplicator, trackLostTimeoutMs);
        CellListener rootListener = new StationsCellListener(stations, tracker);

        return rootListener;
    }

}
