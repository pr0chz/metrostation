package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.internal.*;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Builder {

    public static CellListener createListener(ScheduledExecutorService service, long timeoutS, Stations stations,
                                              Notifications notifications) {

        CompositeStationListener compositeStationListener = new CompositeStationListener();
        compositeStationListener.addListener(new ToastStationListener(notifications));
        compositeStationListener.addListener(new NotificationStationListener(notifications));

        Tracker tracker = new Tracker(compositeStationListener, TimeUnit.MINUTES.toMillis(3));
        StationsCellListener stationsCellListener = new StationsCellListener(stations, tracker);
        CellListener rootListener = new CellListenerFilter(stationsCellListener);

        return rootListener;
    }

}
