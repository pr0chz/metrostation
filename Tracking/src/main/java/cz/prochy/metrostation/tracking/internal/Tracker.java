package cz.prochy.metrostation.tracking.internal;

public class Tracker implements StationListener {

    private final StationListener listener;
    private final long resetTimeout;

    private StationGroup current;
    private StationGroup prediction;
    private long lastTs;

    public Tracker(StationListener listener, long resetTimeout) {
        this.listener = listener;
        this.resetTimeout = resetTimeout;
        reset();
    }

    private void setState(StationGroup stations, StationGroup predictions) {
        current = stations.immutable();
        prediction = predictions.immutable();
        lastTs = System.currentTimeMillis();
    }

    private void reset() {
        setState(NO_STATIONS, NO_STATIONS);
    }

    private boolean hasIntersection(StationGroup set1, StationGroup set2) {
        return !set1.intersect(set2).isEmpty();
    }

    private void notifyListener() {
        listener.onStation(current, prediction);
    }

    @Override
    public void onStation(StationGroup stations, StationGroup ignoredPredictions) {
        if (stations.isEmpty()) {
            if (!current.isEmpty() && System.currentTimeMillis() - lastTs > resetTimeout) {
                reset();
                notifyListener();
            }
            return;
        }

        StationGroup left = current.left().immutable();
        StationGroup right = current.right().immutable();

        boolean narrowsCurrent = hasIntersection(current, stations);
        boolean goesLeft = hasIntersection(left, stations);
        boolean goesRight = hasIntersection(right, stations);

        if (goesLeft) {
            setState(left.intersect(stations), left.left());
        } else if (goesRight) {
            setState(right.intersect(stations), right.right());
        } else if (narrowsCurrent) {
            setState(current.intersect(stations), NO_STATIONS);
        } else {
            setState(stations, NO_STATIONS);
        }

        notifyListener();
    }

    @Override
    public void onDisconnect() {
        listener.onDisconnect();
    }
}
