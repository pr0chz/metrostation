package cz.prochy.metrostation.tracking;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class CellListenerFilter implements CellListener {
	
	private int currentCid = -1;
	private int currentLac = -1;
	private boolean connected = false;
    private boolean first = true;
	
	private final CellListener listener;
	
	public CellListenerFilter(CellListener listener) {
		this.listener = Check.notNull(listener);
	}
	
	@Override
	public void cellInfo(int cid, int lac) {
		if (cid != currentCid || lac != currentLac || !connected || first) {
			currentCid = cid;
			currentLac = lac;
			connected = true;
            first = false;
			listener.cellInfo(cid, lac);
		}
	}
	
	@Override
	public void disconnected() {
		if (connected || first) {
			connected = false;
            first = false;
			listener.disconnected();
		}
	}
	
}
