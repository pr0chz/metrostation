package cz.prochy.metrostation.tracking;

public interface CellListener {
    void cellInfo(int cid, int lac);
    void disconnected();
}
