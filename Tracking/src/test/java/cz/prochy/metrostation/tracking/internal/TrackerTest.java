package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.LineBuilder;
import cz.prochy.metrostation.tracking.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.*;

public class TrackerTest {

    private Tracker tracker;
    private StationListener listener;
    private StepVerifier verifier;

    private Stations stations;

    private final static long TIMEOUT = TimeUnit.DAYS.toMillis(1);

    @Before
    public void setUp() throws Exception {
        listener = createStrictMock(StationListener.class);
        tracker = new Tracker(listener, TIMEOUT);
        verifier = new StepVerifier(listener);
    }

    private final Stations detStations = new Stations() {
        @Override
        void buildStations() {
            final LineBuilder line = new LineBuilder();

            station(line, "station 1", id("", 1, 1));
            station(line, "station 2", id("", 2, 2));
            station(line, "station 3", id("", 3, 3));
            station(line, "station 4", id("", 4, 4));
            station(line, "station 5", id("", 5, 5));
        }
    };

    private StationGroup getStations(int id) {
        return stations.getStations(id, id);
    }

    private Runnable stations(int id) {
        return () -> tracker.onStation(getStations(id), StationListener.NO_STATIONS);
    }

    private Runnable emptyStations() {
        return () -> tracker.onStation(StationGroup.empty(), StationGroup.empty());
    }

    private Runnable expect(StationGroup stations, StationGroup predictions) {
        return () -> listener.onStation(eq(stations), eq(predictions));
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
        tracker = new Tracker(listener, 0);
        step(stations(2), expect(getStations(2), StationGroup.empty()));
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        step(emptyStations(), expect(StationGroup.empty(), StationGroup.empty()));
        step(stations(3), expect(getStations(3), StationGroup.empty()));
    }


    private final NonDetStations nonDetStations = new NonDetStations();

    public final static String S11 = "s11";
    public final static String S12 = "s12";
    public final static String S21 = "s21";
    public final static String S22 = "s22";
    public final static String S31 = "s31";
    public final static String S32 = "s32";
    public final static String S5 = "s5";

    private static class NonDetStations extends Stations {

        @Override
        void buildStations() {
            final LineBuilder line = new LineBuilder();

            station(line, S21, id("", 2, 2));
            station(line, S5, id("", 5, 5));
            station(line, S11, id("", 1, 1));
            station(line, S31, id("", 3, 3));
            station(line, S12, id("", 1, 1));
            station(line, S22, id("", 2, 2));
            station(line, S32, id("", 3, 3));
        }

        public StationGroup findSingle(int id, String name) {
            for (Station station : getStations(id, id).asSet()) {
                if (station.getName().equals(name)) {
                    return StationGroup.from(station);
                }
            }
            throw new NoSuchElementException("Cannot find " + name);
        }

    };


    private StationGroup findSingle(int id, String name) {
        return nonDetStations.findSingle(id, name);
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
    public void testDisconnect() throws Exception {
        listener.onDisconnect();
        expectLastCall().once();
        replay(listener);
        tracker.onDisconnect();
        verify(listener);
    }
}