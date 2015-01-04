package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifications;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class NotificationStationListener implements StationListener {

	private final Notifications notifications;
	private final Timeout timeout;
		
	private String lastStation;
	
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
	public void onStation(String station) {
		Check.notNull(station);
		timeout.cancel();
		lastStation = station;
		notifications.notificationIncomingStation(station);
	}
	
	private void resetTimer() {
		timeout.reset(cancelNotificationTask);
	}
	
	@Override
	public void onUnknownStation() {
		resetTimer();
		lastStation = null;
	}
	
	@Override
	public void onDisconnect() {
		if (lastStation != null) {
			resetTimer();
			notifications.notificationLeavingStation(lastStation);
			lastStation = null;
		}
	}
	
}
