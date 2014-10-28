package cz.prochy.metrostation;

public interface CellListener {
	void cellInfo(int cid, int lac);
	void disconnected();
}
