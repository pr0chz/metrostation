package cz.prochy.metrostation.tracking.internal;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class StationsCellListenerTest {

    private Stations stations;
    private StationsCellListener listener;
    private StationListener output;

    @Before
    public void setUp() throws Exception {
        stations = createStrictMock(Stations.class);
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
        output.onDisconnect();
        expectLastCall().once();
        replay(output);
        listener.disconnected();
        verify(output);
    }

    @Test
    public void testOnStation() throws Exception {
        StationGroup stationGroup = new StationGroup();
        output.onStation(same(stationGroup), same(StationListener.NO_STATIONS));
        expectLastCall().once();

        final int cid = 123;
        final int lac = 345;
        expect(stations.getStations(eq(cid), eq(lac))).andReturn(stationGroup).once();
        replay(output, stations);

        listener.cellInfo(cid, lac);

        verify(output, stations);
    }
}