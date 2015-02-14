package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.internal.*;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Builder {

    public static CellListener createListener(ScheduledExecutorService service, long timeoutS, Stations stations,
                                              Notifications notifications) {

        CompositeStationListener compositeStationListener = new CompositeStationListener();
        PredictiveStationListener predictiveStationListener =
                new PredictiveStationListener(compositeStationListener, new Timeout(service, 25, TimeUnit.SECONDS));
        StationsCellListener stationsCellListener = new StationsCellListener(stations, predictiveStationListener);
        CellListener rootListener = new CellListenerFilter(stationsCellListener);

        Timeout timeout = new Timeout(service, timeoutS, TimeUnit.SECONDS);

        compositeStationListener.addListener(new ToastStationListener(notifications));
        compositeStationListener.addListener(new NotificationStationListener(notifications, timeout));

        return rootListener;
    }

}
