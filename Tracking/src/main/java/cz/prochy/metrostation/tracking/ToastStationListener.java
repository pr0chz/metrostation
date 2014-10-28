package cz.prochy.metrostation.tracking;

public class ToastStationListener implements StationListener {

	private String currentStation;
	
	private final Notifications notifications;
	
	public ToastStationListener(Notifications notifications) {
		this.notifications = Check.notNull(notifications);
	}
	
	@Override
	public void onStation(String station) {
		currentStation = station;
		notifications.toastIncomingStation(station);
	}

	@Override
	public void onUnknownStation() {
		currentStation = null;
	}
	
	@Override
	public void onDisconnect() {
		if (currentStation != null) {
			notifications.toastLeavingStation(currentStation);
			currentStation = null;
		}
	}
	
}
