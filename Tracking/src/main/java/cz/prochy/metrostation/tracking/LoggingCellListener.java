/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
