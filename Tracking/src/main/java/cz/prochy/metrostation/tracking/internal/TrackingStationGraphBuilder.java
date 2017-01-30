package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.StationGraphBuilder;

import java.util.HashMap;
import java.util.Map;

public class TrackingStationGraphBuilder implements StationGraphBuilder<Long, TrackingStationGraph> {

    private final Map<Long, StationGroup> cellMap = new HashMap<>();
    private final Map<String, Station> stations = new HashMap<>();

    private class LineBuilder implements StationGraphBuilder.LineBuilder<Long> {
        private final Line line = new Line();
        private Station last;

        private void connectToPrevious(Station station) {
            if (last == null) {
                last = station;
            } else {
                last.addNext(line, station);
                station.addPrev(line, last);
                last = station;
            }
        }

        private void addStation(Station station, Long ... ids) {
            Check.notNull(station);
            connectToPrevious(station);
            station.addLine(line);
            for (long id : ids) {
                if (!cellMap.containsKey(id)) {
                    cellMap.put(id, new StationGroup());
                }
                cellMap.get(id).add(station);
            }
        }

        private Station getStation(String name) {
            if (!stations.containsKey(name)) {
                stations.put(name, new Station(name));
            }
            return stations.get(name);
        }

        @Override
        public void station(String name, Long ... ids) {
            Station station = getStation(name);
            addStation(station, ids);
        }
    }

    public static class StationGraph implements TrackingStationGraph {

        private final Map<Long, StationGroup> cellMap = new HashMap<>();

        public StationGraph(Map<Long, StationGroup> cellMap) {
            Check.notNull(cellMap);
            for (Long id : cellMap.keySet()) {
                this.cellMap.put(id, cellMap.get(id).immutable());
            }
        }

        public StationGroup getStations(int cellId, int lac) {
            if (cellMap.containsKey(longId(cellId, lac))) {
                return cellMap.get(longId(cellId, lac));
            } else {
                return StationGroup.empty();
            }
        }

    }

    @Override
    public LineBuilder newLine(String name) {
        return new LineBuilder();
    }

    @Override
    public Long id(String op, int cid, int lac) {
        return longId(cid, lac);
    }

    public static Long longId(int cid, int lac) {
        return ((long)cid << 32) | (lac & 0xffffffffL);
    }

    @Override
    public TrackingStationGraph build() {
        return new StationGraph(cellMap);
    }
}
