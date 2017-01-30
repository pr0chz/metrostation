package cz.prochy.metrostation.tracking;

public interface StationGraphBuilder<StationId, StationGraph> {

    interface LineBuilder<StationId> {
        void station(String name, StationId ... ids);
    }

    LineBuilder<StationId> newLine(String name);
    StationId id(String op, int cid, int lac);
    StationGraph build();
}
