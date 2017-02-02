package cz.prochy.metrostation.tracking.internal.graph;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.internal.Station;
import cz.prochy.metrostation.tracking.internal.StationGroup;

import java.util.Map;

class StationBuilder implements cz.prochy.metrostation.tracking.graph.StationBuilder {

    private final Map<Long, StationGroup> cellMap;
    private final Station station;

    public StationBuilder(Map<Long, StationGroup> cellMap, Station station) {
        this.cellMap = Check.notNull(cellMap);
        this.station = Check.notNull(station);
    }

    private void addStation(long id) {
        if (!cellMap.containsKey(id)) {
            cellMap.put(id, new StationGroup());
        }
        cellMap.get(id).add(station);
    }

    @Override
    public StationBuilder id(String op, int cid, int lac) {
        addStation(TrackingGraphBuilder.longId(cid, lac));
        return this;
    }
}
