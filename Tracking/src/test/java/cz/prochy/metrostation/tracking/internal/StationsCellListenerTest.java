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

import cz.prochy.metrostation.tracking.internal.graph.StationGraph;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class StationsCellListenerTest {

    private StationGraph stations;
    private StationsCellListener listener;
    private StationListener output;

    @Before
    public void setUp() throws Exception {
        stations = createStrictMock(StationGraph.class);
        output = createStrictMock(StationListener.class);
        listener = new StationsCellListener(stations, output);
    }

    @Test(expected = NullPointerException.class)
    public void testNullStationsThrows() throws Exception {
        new StationsCellListener(null, output);
    }

    @Test(expected = NullPointerException.class)
    public void testNullListenerThrows() throws Exception {
        new StationsCellListener(stations, null);
    }

    @Test
    public void testDisconnect() throws Exception {
        final long ts = 13;
        output.onDisconnect(eq(ts));
        expectLastCall().once();
        replay(output);
        listener.disconnected(ts);
        verify(output);
    }

    @Test
    public void testOnStation() throws Exception {
        final long ts = 10;
        StationGroup stationGroup = new StationGroup();
        output.onStation(eq(ts), same(stationGroup), same(StationListener.NO_STATIONS));
        expectLastCall().once();

        final int cid = 123;
        final int lac = 345;
        expect(stations.getStations(eq(cid), eq(lac))).andReturn(stationGroup).once();
        replay(output, stations);

        listener.cellInfo(ts, cid, lac);

        verify(output, stations);
    }
}