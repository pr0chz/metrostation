package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifications;
import cz.prochy.metrostation.tracking.Station;
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

	private Station currentStation;
	
	private final Notifications notifications;
	
	public ToastStationListener(Notifications notifications) {
		this.notifications = Check.notNull(notifications);
	}
	
	@Override
	public void onStation(Station station) {
		currentStation = Check.notNull(station);
		notifications.toastStationArrival(station.getName());
	}

	@Override
	public void onUnknownStation() {
		currentStation = null;
	}
	
	@Override
	public void onDisconnect() {
		if (currentStation != null) {
			notifications.toastStationDeparture(currentStation.getName());
			currentStation = null;
		}
	}
	
}
