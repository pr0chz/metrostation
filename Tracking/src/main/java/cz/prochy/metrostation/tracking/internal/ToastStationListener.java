package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifications;
import net.jcip.annotations.NotThreadSafe;

/**
 * This class statefully translates StationListener events to toast Notification API calls.
 * Logic is as follows:
 * <ul>
 *     <li>Notify arrival into known station - this is independent of previous state.</li>
 *     <li>Notify departure from the station - only if we know our immediate previous station
 *     (i.e. station was not unknown)</li>
 * </ul>
 */
@NotThreadSafe
public class ToastStationListener implements StationListener {

    private StationGroup currentStations;
    private StationGroup currentPredictions;

    private final Notifications notifications;

    public ToastStationListener(Notifications notifications) {
        this.notifications = Check.notNull(notifications);
    }

    @Override
    public void onStation(StationGroup stations, StationGroup predictions) {
        currentStations = Check.notNull(stations);
        currentPredictions = Check.notNull(predictions);
        if (currentStations.hasSingleValue()) {
            notifications.toastStationArrival(currentStations.getStation().getName());
        }
    }

    @Override
    public void onDisconnect() {
        if (currentStations.hasSingleValue()) {
            if (currentPredictions.hasSingleValue()) {
                notifications.toastStationDeparture(currentStations.getStation().getName(), currentPredictions.getStation().getName());
            } else {
                notifications.toastStationDeparture(currentStations.getStation().getName());
            }
        }
    }

}
