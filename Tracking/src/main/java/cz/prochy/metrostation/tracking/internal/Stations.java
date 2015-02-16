package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.LineBuilder;
import cz.prochy.metrostation.tracking.Station;

import java.util.HashMap;
import java.util.Map;

abstract public class Stations {

    private final Map<Long, StationGroup> cellMap = new HashMap<>();

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

    protected void station(LineBuilder lineBuilder, String name, long... ids) {
        Station station = new Station(name);
        lineBuilder.addStation(station);
        for (long id : ids) {
            if (!cellMap.containsKey(id)) {
                cellMap.put(id, new StationGroup());
            }
            cellMap.get(id).add(station);
        }
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
