package cz.prochy.metrostation.tracking.internal.graph;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.internal.Line;
import cz.prochy.metrostation.tracking.internal.Station;
import cz.prochy.metrostation.tracking.internal.StationGroup;

import java.util.Map;

class LineBuilder implements cz.prochy.metrostation.tracking.graph.LineBuilder {
    private final Map<Long, StationGroup> cellMap;
    private final Map<String, Station> stations;
    private final Line line = new Line();
    private Station last;

    public LineBuilder(Map<Long, StationGroup> cellMap, Map<String, Station> stations) {
        this.cellMap = Check.notNull(cellMap);
        this.stations = Check.notNull(stations);
    }

    private Station getStation(String name) {
        if (!stations.containsKey(name)) {
            stations.put(name, new Station(name));
        }
        return stations.get(name);
    }

    private void connectToPrevious(Station station) {
        if (last == null) {
            last = station;
        } else {
            last.addNext(line, station);
            station.addPrev(line, last);
            last = station;
        }
    }

    @Override
    public StationBuilder station(String name) {
        Check.notNull(name);
        Station station = getStation(name);
        connectToPrevious(station);
        station.addLine(line);
        return new StationBuilder(cellMap, station);
    }

}
