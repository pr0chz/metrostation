package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifications;
import net.jcip.annotations.NotThreadSafe;

/**
 * This class statefully translates station events into notifications suitable to be presented in notification
 * area. It implements following logic:
 * <ul>
 *     <li>On known station - notification appears for given station, notification will be visible as long as we are
 *     on same known station</li>
 *     <li>On unknown station - disappear timer is started and notification is left in place if any currently exist</li>
 *     <li>On disconnect - if last location was known station, disappear timer is started and notification is
 *     changed to "leaving station". Otherwise it does nothing.</li>
 * </ul>
 */
@NotThreadSafe
public class NotificationStationListener implements StationListener {

	private final Notifications notifications;

	private StationGroup lastStations = new StationGroup();
    private StationGroup lastPredictions = new StationGroup();

	public NotificationStationListener(Notifications notifications) {
		this.notifications = Check.notNull(notifications);
	}

    @Override
    public void onStation(StationGroup stations, StationGroup predictions) {
        Check.notNull(stations);
        Check.notNull(predictions);
        lastStations = stations;
        lastPredictions = predictions;
        if (stations.hasSingleValue()) {
            notifications.notifyStationArrival(stations.getStation().getName());
        }
    }

	@Override
	public void onDisconnect() {
        if (lastStations.hasSingleValue()) {
            notifications.notifyStationDeparture(lastStations.getStation().getName());
        }
	}
	
}
