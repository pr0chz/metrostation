package cz.prochy.metrostation;

public class StationsCellListener implements CellListener {

	private final static String UNKNOWN_STATION = "";
	
	private String stationName;
	
	private final Stations stations;
	private final StationListener listener;
	
	public StationsCellListener(Stations stations, StationListener listener) {
		this.stations = Check.notNull(stations);
		this.listener = Check.notNull(listener);
		reset();
	}
	
	private void reset() {
		stationName = UNKNOWN_STATION;
	}
	
	@Override
	public void cellInfo(int cid, int lac) {
		if (stations.isStation(cid, lac)) {
			String name = stations.getName(cid, lac);
			if (!stationName.equals(name)) {
				stationName = name;
				listener.onStation(stationName);
			}
		} else {
			listener.onUnknownStation();
			reset();
		}		
	}
	
	@Override
	public void disconnected() {
		listener.onDisconnect();
		reset();
	}
	
	
}
