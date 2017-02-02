package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.internal.*;
import cz.prochy.metrostation.tracking.internal.graph.StationGraph;

public class Builder {

    public static CellListener createListener(StationGraph graph, Notifier notifier, long trackLostTimeoutMs,
                                              long transferTimeoutMs) {

        return new StationsCellListener(
                graph,
                new Tracker(
                        new NotifyListener(new Deduplicator(notifier)),
                        trackLostTimeoutMs,
                        transferTimeoutMs)
                );

    }

}
