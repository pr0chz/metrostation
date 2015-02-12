package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Station;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PredictiveStationListener implements StationListener {

    private final StationListener listener;
    private final Timeout timeout;

    private final StationCache stationCache = new StationCache(TimeUnit.MINUTES.toMillis(5));

    public PredictiveStationListener(StationListener listener, Timeout timeout) {
        this.listener = Objects.requireNonNull(listener);
        this.timeout = Objects.requireNonNull(timeout);
    }

    @Override
    public void onStation(Station station) {
        stationCache.add(System.currentTimeMillis(), station);
        timeout.cancel();
        listener.onStation(station);
    }

    @Override
    public void onUnknownStation() {
        timeout.cancel();
        listener.onUnknownStation();
    }

    // s1 next <-> prev s2 next ->
    // s1 prev <-> next s2 prev ->

    static Station getNext(Station s1, Station s2) {
        if (s1.getNext() == s2 && s2.getPrev() == s1 && s2.getNext() != null) {
            return s2.getNext();
        } else if (s1.getPrev() == s2 && s2.getNext() == s1 && s2.getPrev() != null) {
            return s2.getPrev();
        } else {
            return null;
        }
    }

    private Station getNextStation() {
        List<StationCache.Entry> stationEntries = stationCache.getEntries(System.currentTimeMillis());
        if (stationEntries.size() == 2) {
            return getNext(stationEntries.get(0).station, stationEntries.get(1).station);
        }
        return null;
    }

    @Override
    public void onDisconnect() {
        timeout.cancel();
        final Station nextStation = getNextStation();
        if (nextStation != null) {
            timeout.reset(new Runnable() {
                @Override
                public void run() {
                    listener.onStation(nextStation);
                }
            });
        }
        listener.onDisconnect();
    }
}
