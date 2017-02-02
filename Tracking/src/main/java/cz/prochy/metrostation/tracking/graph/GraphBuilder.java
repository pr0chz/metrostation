package cz.prochy.metrostation.tracking.graph;

public interface GraphBuilder<StationGraph> {
    LineBuilder newLine(String name);
    StationGraph build();
}
