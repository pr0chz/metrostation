package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.CellListener;
import cz.prochy.metrostation.tracking.Check;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class StationsCellListener implements CellListener {

    private final static StationGroup INITIAL = new StationGroup();
    private final static StationGroup DISCONNECTED = new StationGroup();

    private final Stations stations;
    private final StationListener listener;

    private StationGroup state = INITIAL;

    public StationsCellListener(Stations stations, StationListener listener) {
        this.stations = Check.notNull(stations);
        this.listener = Check.notNull(listener);
    }

    @Override
    public void cellInfo(int cid, int lac) {
        StationGroup group = stations.getStations(cid, lac);
        if (! (state == group)) {
            state = group;
            listener.onStation(state, StationListener.NO_STATIONS);
        }
    }

    @Override
    public void disconnected() {
        if (state != DISCONNECTED) {
            listener.onDisconnect();
            state = DISCONNECTED;
        }
    }


}
