package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.LineBuilder;
import cz.prochy.metrostation.tracking.Station;

import java.util.HashMap;
import java.util.Map;

abstract public class Stations {

    private final Map<Long, StationGroup> cellMap = new HashMap<>();
    private final Map<String, Station> transferStations = new HashMap<>();

    protected long id(String op, int cid, int lac) {
        return id(cid, lac);
    }

    private long id(int cid, int lac) {
        return ((long)cid << 32) | (lac & 0xffffffffL);
    }

    public Stations() {
        buildStations();
        sealLists();
    }

    private void addStation(Station station, LineBuilder lineBuilder, long ... ids) {
        lineBuilder.addStation(station);
        station.addLine(lineBuilder);
        for (long id : ids) {
            if (!cellMap.containsKey(id)) {
                cellMap.put(id, new StationGroup());
            }
            cellMap.get(id).add(station);
        }
    }

    protected void station(LineBuilder lineBuilder, String name, long... ids) {
        addStation(new Station(name), lineBuilder, ids);
    }

    protected void transferStation(LineBuilder lineBuilder, String name, long... ids) {
        if (!transferStations.containsKey(name)) {
            transferStations.put(name, new Station(name));
        }
        addStation(transferStations.get(name), lineBuilder, ids);
    }

    private void sealLists() {
        for (Long id : cellMap.keySet()) {
            cellMap.put(id, cellMap.get(id).immutable());
        }
    }

    abstract void buildStations();

    public StationGroup getStations(int cellId, int lac) {
        if (cellMap.containsKey(id(cellId, lac))) {
            return cellMap.get(id(cellId, lac));
        } else {
            return StationGroup.empty();
        }
    }
}
