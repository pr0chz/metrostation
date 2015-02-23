package cz.prochy.metrostation.tracking;

public interface CellListener {
    void cellInfo(long ts, int cid, int lac);
    void disconnected(long ts);
}
