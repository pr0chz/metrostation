//package cz.prochy.metrostation.tracking.internal;
//
//import cz.prochy.metrostation.tracking.Station;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.easymock.EasyMock.createStrictMock;
//import static org.easymock.EasyMock.eq;
//
//public class DeduplicatorTest {
//
//    private final static long TS = 11;
//
//    private Deduplicator deduplicator;
//    private StationListener listener;
//    private StepVerifier stepVerifier;
//
//    private Station s1 = new Station("s1");
//    private Station s2 = new Station("s2");
//
//    private StationGroup sg1 = new StationGroup();
//    private StationGroup sg2 = new StationGroup();
//    private StationGroup sg3 = new StationGroup();
//
//    @Before
//    public void setUp() throws Exception {
//        listener = createStrictMock(StationListener.class);
//        deduplicator = new Deduplicator(listener);
//        stepVerifier = new StepVerifier(listener);
//
//        sg1.add(s1);
//        sg2.add(s1);
//        sg3.add(s2);
//    }
//
//    private Runnable onStation(StationGroup stations, StationGroup predictions) {
//        return () -> deduplicator.onStation(TS, stations, predictions);
//    }
//
//    private Runnable expectOnStation(StationGroup stations, StationGroup predictions) {
//        return () -> listener.onStation(eq(TS), eq(stations), eq(predictions));
//    }
//
//    private Runnable expectNothing() {
//        return () -> {};
//    }
//
//    private Runnable onDisconnect() {
//        return () -> deduplicator.onDisconnect(TS);
//    }
//
//    private Runnable expectDisconnect() {
//        return () -> listener.onDisconnect(eq(TS));
//    }
//
//    private void step(Runnable action, Runnable ... expects) {
//        stepVerifier.step(action, expects);
//    }
//
//    @Test
//    public void testFirstStationPropagates() throws Exception {
//        step(onStation(sg1, sg3), expectOnStation(sg1, sg3));
//    }
//
//    @Test
//    public void testFirstDisconnectPropagates() throws Exception {
//        step(onDisconnect(), expectDisconnect());
//    }
//
//    @Test
//    public void testTwoSameOnStationsDoesNotPropagate() throws Exception {
//        step(onStation(sg1, sg3), expectOnStation(sg1, sg3));
//        step(onStation(sg1, sg3), expectNothing());
//    }
//
//    @Test
//    public void testTwoOnDisconnectsDoesNotPropagate() throws Exception {
//        step(onDisconnect(), expectDisconnect());
//        step(onDisconnect(), expectNothing());
//    }
//
//    @Test
//    public void testSameButNotIdenticalGroupsOnStationsAreNotPropagated() throws Exception {
//        step(onStation(sg1, sg3), expectOnStation(sg1, sg3));
//        step(onStation(sg2, sg3), expectNothing());
//    }
//
//    @Test
//    public void testSameButNotIdenticalGroupsOnPredictionsAreNotPropagated() throws Exception {
//        step(onStation(sg3, sg1), expectOnStation(sg3, sg1));
//        step(onStation(sg3, sg2), expectNothing());
//    }
//
//    @Test
//    public void testComplexScenario() throws Exception {
//        step(onStation(sg1, sg3), expectOnStation(sg1, sg3));
//        step(onDisconnect(), expectDisconnect());
//        step(onDisconnect(), expectNothing());
//        step(onStation(sg1, sg3), expectOnStation(sg1, sg3));
//        step(onStation(sg1, sg1), expectNothing());
//        step(onStation(sg2, sg2), expectNothing());
//        step(onDisconnect(), expectDisconnect());
//    }
//}