package cz.prochy.metrostation.tracking;

public class LoggingCellListener implements CellListener {

    private final int id;
    private final BoundedChainBuffer<String> cellLogger;
    private final CellListener cellListener;

    public LoggingCellListener(int id, int size, CellListener cellListener) {
        this.id = id;
        this.cellLogger = new BoundedChainBuffer<>(size);
        this.cellListener = Check.notNull(cellListener);
    }

    private String disconnectMessage(long ts) {
        return "{\"id\": " + id +", \"ts\": " + ts + "}";
    }

    private String cellMessage(long ts, int cid, int lac) {
        return "{\"id\": " + id +", \"ts\": " + ts + ", \"cid\": " + cid + ", \"lac\": " + lac + "}";
    }

    public BoundedChainBuffer<String> getCellLogger() {
        return cellLogger;
    }

    @Override
    public void cellInfo(long ts, int cid, int lac) {
        cellLogger.append(cellMessage(ts, cid, lac));
        cellListener.cellInfo(ts, cid, lac);
    }

    @Override
    public void disconnected(long ts) {
        cellLogger.append(disconnectMessage(ts));
        cellListener.disconnected(ts);
    }

}
