package cz.prochy.metrostation.tracking.internal.graph;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.internal.StationGroup;

import java.util.HashMap;
import java.util.Map;

public class StationGraph {

    private final Map<Long, StationGroup> cellMap = new HashMap<>();

    public StationGraph(Map<Long, StationGroup> cellMap) {
        Check.notNull(cellMap);
        for (Long id : cellMap.keySet()) {
            this.cellMap.put(id, cellMap.get(id).immutable());
        }
    }

    public StationGroup getStations(int cellId, int lac) {
        if (cellMap.containsKey(TrackingGraphBuilder.longId(cellId, lac))) {
            return cellMap.get(TrackingGraphBuilder.longId(cellId, lac));
        } else {
            return StationGroup.empty();
        }
    }

}
