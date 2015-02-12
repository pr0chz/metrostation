package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Station;

import java.util.*;

public class StationCache {

    private final static int MAX_SIZE = 2;

    private final Deque<Entry> entries = new ArrayDeque<>(3);

    private final long maxDelay;

    public final static class Entry {
        public final long timestamp;
        public final Station station;

        public Entry(long timestamp, Station station) {
            this.timestamp = timestamp;
            this.station = Objects.requireNonNull(station);
        }
    }

    public StationCache(long maxDelay) {
        this.maxDelay = maxDelay;
    }

    public void add(long timestamp, Station station) {
        entries.addLast(new Entry(timestamp, station));
        if (entries.size() > MAX_SIZE) {
            entries.removeFirst();
        }
    }

    private void cleanupOld(long currentTimestamp) {
        long deadline = currentTimestamp - maxDelay;
        while (!entries.isEmpty() && entries.peekFirst().timestamp < deadline) {
            entries.removeFirst();
        }
    }

    public List<Entry> getEntries(long currentTimestamp) {
        cleanupOld(currentTimestamp);
        return new ArrayList<Entry>(entries);
    }



}
