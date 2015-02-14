package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Station;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class StationsCellListenerTest {

    private final static int CID1 = 1000;
    private final static int LAC1 = 2000;
    private final static Station STATION1 = new Station("station1");

    private final static int CID2 = 4000;
    private final static int LAC2 = 4000;
    private final static Station STATION2 = new Station("station2");

    private Stations stations;
    private StationListener target;
    private StationsCellListener listener;
    private StepVerifier verifier;

    private Stations mockStations() {
        Stations stations = createNiceMock(Stations.class);
        expect(stations.isStation(eq(CID1), eq(LAC1))).andReturn(true).anyTimes();
        expect(stations.getStations(eq(CID1), eq(LAC1))).andReturn(STATION1).anyTimes();

        expect(stations.isStation(eq(CID2), eq(LAC2))).andReturn(true).anyTimes();
        expect(stations.getStations(eq(CID2), eq(LAC2))).andReturn(STATION2).anyTimes();

        expect(stations.isStation(anyInt(), anyInt())).andReturn(false).anyTimes();
        expect(stations.getStations(anyInt(), anyInt())).andReturn(null).anyTimes();
        replay(stations);
        return stations;
    }

    @Before
    public void setUp() throws Exception {
        stations = mockStations();
        target = createStrictMock(StationListener.class);
        listener = new StationsCellListener(stations, target);
        verifier = new StepVerifier(target);
    }

    public void step(Runnable action, Runnable expect) {
        verifier.step(action, expect);
    }

    public Runnable cellInfo(int cid, int lac) {
        return () -> listener.cellInfo(cid, lac);
    }

    public Runnable disconnected() {
        return () -> listener.disconnected();
    }

    public Runnable expectStation(Station station) {
        return () -> {
            target.onStation(eq(station));
            expectLastCall().once();
        };
    }

    public Runnable expectUnknown() {
        return () -> {
            target.onUnknownStation();
            expectLastCall().once();
        };
    }

    public Runnable expectDisconnect() {
        return () -> {
            target.onDisconnect();
            expectLastCall().once();
        };
    }

    public Runnable expectNone() {
        return () -> {};
    }


    @Test
    public void testKnownStationIsNotified() throws Exception {
        step(cellInfo(CID1, LAC1), expectStation(STATION1));
    }

    @Test
    public void testUnknownStationIsNotified() throws Exception {
        step(cellInfo(CID1, LAC1 + 1), expectUnknown());
    }

    @Test
    public void testDisconnectIsNotified() throws Exception {
        step(disconnected(), expectDisconnect());
    }

    @Test
    public void testSecondStation() throws Exception {
        step(cellInfo(CID1, LAC1), expectStation(STATION1));
        step(cellInfo(CID2, LAC2), expectStation(STATION2));
    }

    @Test
    public void testSameStationIsSuppressed() throws Exception {
        step(cellInfo(CID1, LAC1), expectStation(STATION1));
        step(cellInfo(CID1, LAC1), expectNone());
    }

    @Test
    public void testSecondUnknownStationIsSuppressed() throws Exception {
        step(cellInfo(CID1, LAC1 + 1), expectUnknown());
        step(cellInfo(CID1 + 1, LAC1), expectNone());
    }

    @Test
    public void testSecondDisconnectIsSuppressed() throws Exception {
        step(disconnected(), expectDisconnect());
        step(disconnected(), expectNone());
    }

    private void stepStation() {
        step(cellInfo(CID1, LAC1), expectStation(STATION1));
    }

    private void stepDisconnected() {
        step(disconnected(), expectDisconnect());
    }

    private void stepUnknown() {
        step(cellInfo(CID1 + 1, LAC1), expectUnknown());
    }

    @Test
    public void testStationDisconnectTransition() throws Exception {
        stepStation();
        stepDisconnected();
    }

    @Test
    public void testStationUnknownTransition() throws Exception {
        stepStation();
        stepUnknown();
    }

    @Test
    public void testUnknownDisconnectTransition() throws Exception {
        stepUnknown();
        stepDisconnected();
    }

    @Test
    public void testUnknownStationTransition() throws Exception {
        stepUnknown();
        stepStation();
    }

    @Test
    public void testDisconnectStationTransition() throws Exception {
        stepDisconnected();
        stepStation();
    }

    @Test
    public void testDisconnectUnknownTransition() throws Exception {
        stepDisconnected();
        stepUnknown();
    }

    @Test(expected = NullPointerException.class)
    public void testNullStationsThrows() throws Exception {
        new StationsCellListener(null, target);
    }

    @Test(expected = NullPointerException.class)
    public void testNullListenerThrows() throws Exception {
        new StationsCellListener(stations, null);
    }



}