package cz.prochy.metrostation;

public class CellListenerFilter implements CellListener {
	
	private int currentCid = -1;
	private int currentLac = -1;
	private boolean connected = false;
	
	private final CellListener listener;
	
	public CellListenerFilter(CellListener listener) {
		this.listener = Check.notNull(listener);
	}
	
	@Override
	public void cellInfo(int cid, int lac) {
		if (cid != currentCid || lac != currentLac || !connected) {
			currentCid = cid;
			currentLac = lac;
			connected = true;
			listener.cellInfo(cid, lac);
		}
	}
	
	@Override
	public void disconnected() {
		if (connected) {
			connected = false;
			listener.disconnected();
		}
	}
	
}
