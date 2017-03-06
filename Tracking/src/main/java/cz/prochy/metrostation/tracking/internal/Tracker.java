/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.prochy.metrostation.tracking.internal;

import java.util.Set;

public class Tracker implements StationListener {

    private final static Line NO_LINE = new Line();

    private final StationListener listener;
    private final long trackLostTimeoutMs;
    private final long transferStationTimeoutMs;

    private StationGroup current;
    private StationGroup prediction;
    private Direction directionHint;
    private Line line = NO_LINE;
    private long lastTs;
    private boolean lastEventWasDisconnect = false;

    private enum Direction {
        LEFT, RIGHT, UNKNOWN
    }

    public Tracker(StationListener listener, long trackLostTimeoutMs, long transferStationTimeoutMs) {
        this.listener = listener;
        this.trackLostTimeoutMs = trackLostTimeoutMs;
        this.transferStationTimeoutMs = transferStationTimeoutMs;
        reset(0);
    }

    private void setState(long ts, StationGroup stations, StationGroup predictions, Direction direction) {
        this.current = stations.immutable();
        this.prediction = predictions.immutable();
        this.directionHint = direction;
        this.lastTs = ts;
    }

    private void updateLine(StationGroup stations) {
        Line l = getLine(stations);
        if (l != NO_LINE) {
            this.line = l;
        }
    }


    private void reset(long ts) {
        setState(ts, NO_STATIONS, NO_STATIONS, Direction.UNKNOWN);
    }

    private void notifyListener(long ts) {
        listener.onStation(ts, current, prediction);
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

    private boolean timedOut(long ts) {
        return ts - lastTs > trackLostTimeoutMs;
    }

    private Line getLine(StationGroup stations) {
        Line line = NO_LINE;
        if (stations.hasSingleValue()) {
            Set<Line> lines = stations.getStation().getLines();
            if (lines.size() == 1) {
                line = lines.iterator().next();
            }
        }
        return line;
    }

    @Override
    public void onStation(long ts, StationGroup stations, StationGroup ignoredPredictions) {
        lastEventWasDisconnect = false;

        if (stations.isEmpty()) {
            if (!current.isEmpty() && timedOut(ts)) {
                reset(ts);
                notifyListener(ts);
            }
            return;
        }

        if (stations.equals(current)) {
            return;
        }

        StationGroup left = current.left().immutable();
        StationGroup right = current.right().immutable();

        boolean narrowsCurrent = current.intersects(stations);
        boolean goesLeft = left.intersects(stations);
        boolean goesRight = right.intersects(stations);

        if (goesLeft && goesRight) { // use directionHint hint
            if (directionHint == Direction.LEFT) {
                goesRight = false;
            } else if (directionHint == Direction.RIGHT) {
                goesLeft = false;
            }
        }

        updateLine(current);
        updateLine(stations);
        if (countTrue(narrowsCurrent, goesLeft, goesRight) == 1) {
            if (goesLeft) {
                StationGroup leftStations = left.intersect(stations);
                setState(ts, leftStations, line == NO_LINE ? leftStations.left() : leftStations.left(line), Direction.LEFT);
            } else if (goesRight) {
                StationGroup rightStations = right.intersect(stations);
                setState(ts, rightStations, line == NO_LINE ? rightStations.right() : rightStations.right(line), Direction.RIGHT);
            } else if (narrowsCurrent) {
                setState(ts, current.intersect(stations), prediction, directionHint);
            }
        } else {
            // we are not sure what happened or we are completely somewhere else
            setState(ts, stations, NO_STATIONS, Direction.UNKNOWN);
            line = NO_LINE;
            updateLine(stations);
        }

        notifyListener(ts);
    }

    @Override
    public void onDisconnect(long ts) {
        // cancel prediction on transfer timeout
        if (ts - lastTs > transferStationTimeoutMs
                && current.hasSingleValue()
                && current.getStation().isTransfer()
                && !lastEventWasDisconnect) {
            setState(ts, current, NO_STATIONS, Direction.UNKNOWN);
            line = NO_LINE;
            notifyListener(ts);
        }
        lastEventWasDisconnect = true;

        listener.onDisconnect(ts);
    }
}
