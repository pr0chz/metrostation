package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.CellListener;
import cz.prochy.metrostation.tracking.Check;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class StationsCellListener implements CellListener {

    private final Stations stations;
    private final StationListener listener;

    public StationsCellListener(Stations stations, StationListener listener) {
        this.stations = Check.notNull(stations);
        this.listener = Check.notNull(listener);
    }

    @Override
    public void cellInfo(int cid, int lac) {
        listener.onStation(stations.getStations(cid, lac), StationListener.NO_STATIONS);
    }

    @Override
    public void disconnected() {
        listener.onDisconnect();
    }


}
