package cz.prochy.metrostation.tracking.internal.graph;

import cz.prochy.metrostation.tracking.graph.GraphBuilder;
import cz.prochy.metrostation.tracking.internal.Station;
import cz.prochy.metrostation.tracking.internal.StationGroup;

import java.util.HashMap;
import java.util.Map;

public class TrackingGraphBuilder implements GraphBuilder<StationGraph> {

    private final Map<Long, StationGroup> cellMap = new HashMap<>();
    private final Map<String, Station> stations = new HashMap<>();

    @Override
    public LineBuilder newLine(String name) {
        return new LineBuilder(cellMap, stations);
    }

    static Long longId(int cid, int lac) {
        return ((long)cid << 32) | (lac & 0xffffffffL);
    }

    @Override
    public StationGraph build() {
        return new StationGraph(cellMap);
    }
}
