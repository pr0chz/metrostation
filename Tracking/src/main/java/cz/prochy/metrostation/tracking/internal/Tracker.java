package cz.prochy.metrostation.tracking.internal;

public class Tracker implements StationListener {

    private final StationListener listener;
    private final long trackLostTimeoutMs;

    private StationGroup current;
    private StationGroup prediction;
    private Direction directionHint;
    private long lastTs;

    private enum Direction {
        LEFT, RIGHT, UNKNOWN
    }

    public Tracker(StationListener listener, long trackLostTimeoutMs) {
        this.listener = listener;
        this.trackLostTimeoutMs = trackLostTimeoutMs;
        reset();
    }

    private void setState(StationGroup stations, StationGroup predictions, Direction direction) {
        this.current = stations.immutable();
        this.prediction = predictions.immutable();
        this.directionHint = direction;
        this.lastTs = System.currentTimeMillis();
    }

    private void reset() {
        setState(NO_STATIONS, NO_STATIONS, Direction.UNKNOWN);
    }

    private boolean hasIntersection(StationGroup set1, StationGroup set2) {
        // TODO unnecessary allocation of new group
        return !set1.intersect(set2).isEmpty();
    }

    private void notifyListener() {
        listener.onStation(current, prediction);
    }

    private int countTrue(boolean ... bools) {
        int counter = 0;
        for (boolean b : bools) {
            if (b) {
                ++counter;
            }
        }
        return counter;
    }

    private boolean timedOut() {
        return System.currentTimeMillis() - lastTs > trackLostTimeoutMs;
    }

    @Override
    public void onStation(StationGroup stations, StationGroup ignoredPredictions) {
        if (stations.isEmpty()) {
            if (!current.isEmpty() && timedOut()) {
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

        if (goesLeft && goesRight) { // use directionHint hint
            if (directionHint == Direction.LEFT) {
                goesRight = false;
            } else if (directionHint == Direction.RIGHT) {
                goesLeft = false;
            }
        }

        if (countTrue(narrowsCurrent, goesLeft, goesRight) == 1) {
            if (goesLeft) {
                setState(left.intersect(stations), left.left(), Direction.LEFT);
            } else if (goesRight) {
                setState(right.intersect(stations), right.right(), Direction.RIGHT);
            } else if (narrowsCurrent) {
                setState(current.intersect(stations), NO_STATIONS, directionHint);
            }
        } else {
            // we are not sure what happened or we are completely somewhere else
            setState(stations, NO_STATIONS, Direction.UNKNOWN);
        }

        notifyListener();
    }

    @Override
    public void onDisconnect() {
        listener.onDisconnect();
    }
}
