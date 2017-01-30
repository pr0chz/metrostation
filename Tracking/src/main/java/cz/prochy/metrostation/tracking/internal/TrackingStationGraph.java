package cz.prochy.metrostation.tracking.internal;

public interface TrackingStationGraph {
    StationGroup getStations(int cellId, int lac);
}
