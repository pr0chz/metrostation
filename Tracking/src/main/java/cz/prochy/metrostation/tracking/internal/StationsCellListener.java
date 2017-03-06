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

package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.CellListener;
import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.internal.graph.StationGraph;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class StationsCellListener implements CellListener {

    private final StationGraph stations;
    private final StationListener listener;

    public StationsCellListener(StationGraph stations, StationListener listener) {
        this.stations = Check.notNull(stations);
        this.listener = Check.notNull(listener);
    }

    @Override
    public void cellInfo(long ts, int cid, int lac) {
        listener.onStation(ts, stations.getStations(cid, lac), StationListener.NO_STATIONS);
    }

    @Override
    public void disconnected(long ts) {
        listener.onDisconnect(ts);
    }


}
