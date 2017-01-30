package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.StationGraphBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.*;

public class TrackerTest {

    private Tracker tracker;
    private StationListener listener;
    private StepVerifier verifier;

    private TrackingStationGraph stations;

    private final static long TRACK_LOST_TIMEOUT = TimeUnit.DAYS.toMillis(1);
    private final static long TRANSFER_TIMEOUT = TimeUnit.DAYS.toMillis(1);
    private final static long TS = 15;

    @Before
    public void setUp() throws Exception {
        listener = createStrictMock(StationListener.class);
        tracker = new Tracker(listener, TRACK_LOST_TIMEOUT, TRANSFER_TIMEOUT);
        verifier = new StepVerifier(listener);
    }

    private final TrackingStationGraph detStations = getDetStations();

    private TrackingStationGraph getDetStations() {
        final TrackingStationGraphBuilder builder = new TrackingStationGraphBuilder();
        final StationGraphBuilder.LineBuilder<Long> line = builder.newLine("");

        line.station("station 1", builder.id("", 1, 1));
        line.station("station 2", builder.id("", 2, 2));
        line.station("station 3", builder.id("", 3, 3));
        line.station("station 4", builder.id("", 4, 4));
        line.station("station 5", builder.id("", 5, 5));

        return builder.build();
    }

    private StationGroup getStations(int cid, int lac) {
        return stations.getStations(cid, lac);
    }

    private StationGroup getStations(int id) {
        return stations.getStations(id, id);
    }

    private Runnable stations(int id) {
        return stations(id, TS);
    }

    private Runnable stations(int id, long ts) {
        return () -> tracker.onStation(ts, getStations(id, id), StationListener.NO_STATIONS);
    }

    private Runnable emptyStations() {
        return emptyStations(TS);
    }

    private Runnable emptyStations(long ts) {
        return () -> tracker.onStation(ts, StationGroup.empty(), StationGroup.empty());
    }

    private Runnable expect(StationGroup stations, StationGroup predictions) {
        return expect(stations, predictions, TS);
    }

    private Runnable expect(StationGroup stations, StationGroup predictions, long ts) {
        return () -> listener.onStation(eq(ts), eq(stations), eq(predictions));
    }

    private Runnable expectNothing() {
        return () -> {};
    }

    private void step(Runnable action, Runnable ... expects) {
        verifier.step(action, expects);
    }


    @Test
    public void testTraversingSingleStations() throws Exception {
        stations = detStations;
        step(stations(2), expect(getStations(2), StationGroup.empty()));
        step(stations(3), expect(getStations(3), getStations(4)));
        step(stations(4), expect(getStations(4), getStations(5)));
        step(stations(5), expect(getStations(5), StationGroup.empty()));
    }

    @Test
    public void testTraversingSingleStationsWithUnknowns() throws Exception {
        stations = detStations;
        step(emptyStations(), expectNothing());
        step(stations(2), expect(getStations(2), StationGroup.empty()));
        step(emptyStations(), expectNothing());
        step(stations(3), expect(getStations(3), getStations(4)));
        step(emptyStations(), expectNothing());
    }

    @Test
    public void testTraversingSingleStationsReverse() throws Exception {
        stations = detStations;
        step(stations(3), expect(getStations(3), StationGroup.empty()));
        step(stations(2), expect(getStations(2), getStations(1)));
        step(stations(1), expect(getStations(1), StationGroup.empty()));
    }

    @Test
    public void testSingleStationJumps() throws Exception {
        stations = detStations;
        step(stations(1), expect(getStations(1), StationGroup.empty()));
        step(stations(3), expect(getStations(3), StationGroup.empty()));
        step(stations(5), expect(getStations(5), StationGroup.empty()));
    }

    @Test
    public void testEmptyStationsDoesNothing() throws Exception {
        stations = detStations;
        step(stations(2), expect(getStations(2), StationGroup.empty()));
        step(emptyStations(), expectNothing());
        step(stations(3), expect(getStations(3), getStations(4)));
    }

    @Test
    public void testStateGetsResetAfterTimeout() throws Exception {
        stations = detStations;
        tracker = new Tracker(listener, 10, TRANSFER_TIMEOUT);
        step(stations(2, 10), expect(getStations(2), StationGroup.empty(), 10));
        step(emptyStations(19), expectNothing());
        step(emptyStations(21), expect(StationGroup.empty(), StationGroup.empty(), 21));
        step(stations(3, 22), expect(getStations(3), StationGroup.empty(), 22));
    }

    // TODO add tests for transfer timeout

    private final TrackingStationGraph nonDetStations = getNonDetStations();

    public final static String S11 = "s11";
    public final static String S12 = "s12";
    public final static String S21 = "s21";
    public final static String S22 = "s22";
    public final static String S31 = "s31";
    public final static String S32 = "s32";
    public final static String S5 = "s5";

    private TrackingStationGraph getNonDetStations() {
        final TrackingStationGraphBuilder builder = new TrackingStationGraphBuilder();
        final StationGraphBuilder.LineBuilder<Long> line = builder.newLine("");

        line.station(S21, builder.id("", 2, 2));
        line.station(S5, builder.id("", 5, 5));
        line.station(S11, builder.id("", 1, 1));
        line.station(S31, builder.id("", 3, 3));
        line.station(S12, builder.id("", 1, 1));
        line.station(S22, builder.id("", 2, 2));
        line.station(S32, builder.id("", 3, 3));

        return builder.build();
    }


    private StationGroup findSingle(int id, String name) {
        for (Station station : nonDetStations.getStations(id, id).asSet()) {
            if (station.getName().equals(name)) {
                return StationGroup.from(station);
            }
        }
        throw new NoSuchElementException("Cannot find " + name);
    }

    @Test
    public void testNonDeterministic() throws Exception {
        stations = nonDetStations;
        step(stations(2), expect(getStations(2), StationGroup.empty()));
        step(stations(1), expect(findSingle(1, S12), findSingle(3, S31)));
        step(stations(3), expect(findSingle(3, S31), findSingle(1, S11)));
        step(stations(1), expect(findSingle(1, S11), getStations(5)));
        // change direction
        step(stations(3), expect(findSingle(3, S31), findSingle(1, S12)));
        step(stations(1), expect(findSingle(1, S12), findSingle(2, S22)));
    }

    @Test
    public void testNonDeterministicWithUnknowns() throws Exception {
        stations = nonDetStations;
        step(stations(2), expect(getStations(2), StationGroup.empty()));
        step(emptyStations(), expectNothing());
        step(emptyStations(), expectNothing());
        step(stations(1), expect(findSingle(1, S12), findSingle(3, S31)));
        step(emptyStations(), expectNothing());
        step(stations(3), expect(findSingle(3, S31), findSingle(1, S11)));
        step(emptyStations(), expectNothing());
    }

    @Test
    public void testDisconnect() throws Exception {
        listener.onDisconnect(eq(0L));
        expectLastCall().once();
        replay(listener);
        tracker.onDisconnect(0);
        verify(listener);
    }
}