package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifications;
import cz.prochy.metrostation.tracking.Station;
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
	private final Timeout timeout;
		
	private Station lastStation;
	
	private final Runnable cancelNotificationTask = new Runnable() {
		public void run() {
			notifications.hideNotification();
		};
	};
	
	public NotificationStationListener(Notifications notifications, Timeout timeout) {
		this.notifications = Check.notNull(notifications);
		this.timeout = Check.notNull(timeout);
	}
	
	@Override
	public void onStation(Station station) {
		Check.notNull(station);
		timeout.cancel();
		lastStation = station;
		notifications.notifyStationArrival(station.getName());
	}
	
	private void resetTimer() {
		timeout.reset(cancelNotificationTask);
	}
	
	@Override
	public void onUnknownStation() {
        if (lastStation != null) {
            resetTimer();
            lastStation = null;
        }
	}
	
	@Override
	public void onDisconnect() {
		if (lastStation != null) {
			resetTimer();
			notifications.notifyStationDeparture(lastStation.getName());
			lastStation = null;
		}
	}
	
}
