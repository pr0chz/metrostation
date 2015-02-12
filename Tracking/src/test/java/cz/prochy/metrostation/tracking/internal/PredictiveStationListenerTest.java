package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.LineBuilder;
import cz.prochy.metrostation.tracking.Station;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredictiveStationListenerTest {


    private PredictiveStationListener predictiveStationListener;

    private IMocksControl stationListenerControl;
    private StationListener stationListener;

    private Timeout timeout;

    private Capture<Runnable> timeoutTask = new Capture<>();

    private final static Station STATION = createNiceMock(Station.class);
    private Station[] stations;

    @Before
    public void setUp() throws Exception {
        timeout = createStrictMock(Timeout.class);
        stationListenerControl = createStrictControl();
        stationListener = stationListenerControl.createMock(StationListener.class);
        predictiveStationListener = new PredictiveStationListener(stationListener, timeout);
    }

    @Test(expected = NullPointerException.class)
    public void testNullStationThrows() throws Exception {
        predictiveStationListener.onStation(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullStationListenerThrows() throws Exception {
        new PredictiveStationListener(null, timeout);
    }

    @Test(expected = NullPointerException.class)
    public void testNullTimeoutThrows() throws Exception {
        new PredictiveStationListener(stationListener, null);
    }

    @Test
    public void testOnStationIsPropagated() throws Exception {
        stationListener.onStation(STATION);
        expectLastCall().once();
        replay(stationListener);
        predictiveStationListener.onStation(STATION);
        verify(stationListener);
    }

    @Test
    public void testOnUnknownIsPropagated() throws Exception {
        stationListener.onUnknownStation();
        expectLastCall().once();
        replay(stationListener);
        predictiveStationListener.onUnknownStation();
        verify(stationListener);
    }

    @Test
    public void testOnDisconnectIsPropagated() throws Exception {
        stationListener.onDisconnect();
        expectLastCall().once();
        replay(stationListener);
        predictiveStationListener.onDisconnect();
        verify(stationListener);

    }

    private void expectTimeoutCancel() {
        timeout.cancel();
        expectLastCall().once();
        replay(timeout);
    }

    @Test
    public void testTimerIsCancelledOnStation() throws Exception {
        expectTimeoutCancel();
        predictiveStationListener.onStation(STATION);
        verify(timeout);
    }

    @Test
    public void testTimerIsCancelledOnUnknownStation() throws Exception {
        expectTimeoutCancel();
        predictiveStationListener.onUnknownStation();
        verify(timeout);
    }

    private Station [] buildStations(Station ... stations) {
        LineBuilder builder = new LineBuilder();
        for (Station station : stations) {
            builder.addStation(station);
        }
        return stations;
    }

    private void expectTimeoutTask() {
        timeout.cancel();
        expectLastCall().anyTimes();
        timeout.reset(capture(timeoutTask));
        expectLastCall().once();
        replay(timeout);
    }

    private void ignoreStationListener() {
        stationListenerControl.resetToNice();
        reset(stationListener);
        replay(stationListener);
    }

    private void expectPrediction(int stationIndex) {
        stationListenerControl.resetToStrict();
        reset(stationListener);
        stationListener.onStation(stations[stationIndex]);
        expectLastCall().once();
        replay(stationListener);
    }

    private void expectNoPrediction() {
        stationListenerControl.resetToStrict();
        reset(stationListener);
        replay(stationListener);
    }


    private void setupPrediction() {
        stations = buildStations(new Station("1st"), new Station("2nd"), new Station("3rd"), new Station("4th"));
        expectTimeoutTask();
        ignoreStationListener();
    }

    private void executeStations(int ... indexes) {
        for (int index : indexes) {
            predictiveStationListener.onStation(stations[index]);
            predictiveStationListener.onDisconnect();
        }
    }

    @Test
    public void testForwardPrediction() throws Exception {
        setupPrediction();
        executeStations(0, 1);
        expectPrediction(2);

        assertTrue(timeoutTask.hasCaptured());
        timeoutTask.getValue().run();

        verify();
    }

    @Test
    public void testBackwardPrediction() throws Exception {
        setupPrediction();
        executeStations(2, 1);
        expectPrediction(0);

        assertTrue(timeoutTask.hasCaptured());
        timeoutTask.getValue().run();

        verify();
    }

    @Test
    public void testConfusedPrediction() throws Exception {
        setupPrediction();
        executeStations(3, 1);
        expectNoPrediction();

        assertFalse(timeoutTask.hasCaptured());

        verify();
    }

    @Test
    public void testPredictionAtTheEndOfLine() throws Exception {
        Station [] stations = buildStations(STATION, new Station("2nd"), new Station("3rd"), new Station("4th"));
        expectTimeoutTask();
        ignoreStationListener();

        predictiveStationListener.onStation(stations[2]);
        predictiveStationListener.onDisconnect();
        predictiveStationListener.onStation(stations[3]);
        predictiveStationListener.onDisconnect();

        expectNoPrediction();

        assertFalse(timeoutTask.hasCaptured());

        verify();
    }

    @Test
    public void testPredictionAtTheStartOfLine() throws Exception {
        setupPrediction();
        executeStations(1, 0);
        expectNoPrediction();

        assertFalse(timeoutTask.hasCaptured());

        verify();
    }

}