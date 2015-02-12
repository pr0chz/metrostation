package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Station;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class CompositeStationListenerTest {

    private final static Station STATION = new Station("21station21");

    private CompositeStationListener composite;
    private StationListener listener1;
    private StationListener listener2;

    @Before
    public void setUp() throws Exception {
        composite = new CompositeStationListener();
        listener1 = createStrictMock(StationListener.class);
        listener2 = createStrictMock(StationListener.class);
    }

    @Test
    public void testOnStationDoesNotCrashOnNoListeners() throws Exception {
        composite.onStation(STATION);
    }

    @Test
    public void testOnUnknownStationDoesNotCrashOnNoListeners() throws Exception {
        composite.onUnknownStation();
    }

    @Test
    public void testOnDisconnectDoesNotCrashOnNoListeners() throws Exception {
        composite.onDisconnect();
    }


    @Test(expected = NullPointerException.class)
    public void testThrowsOnNullStation() throws Exception {
        composite.onStation(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsOnAddingNullListener() throws Exception {
        composite.addListener(null);
    }

    private void setupTwoListeners() {
        composite.addListener(listener1);
        composite.addListener(listener2);
    }

    @Test
    public void testOnStationIsCalledOnTwoListeners() throws Exception {
        setupTwoListeners();
        listener1.onStation(eq(STATION));
        expectLastCall();
        listener2.onStation(eq(STATION));
        expectLastCall();
        replay(listener1, listener2);
        composite.onStation(STATION);
        verify(listener1, listener2);
    }

    @Test
    public void testOnUnknownStationIsCalledOnTwoListeners() throws Exception {
        setupTwoListeners();
        listener1.onUnknownStation();
        expectLastCall();
        listener2.onUnknownStation();
        expectLastCall();
        replay(listener1, listener2);
        composite.onUnknownStation();
        verify(listener1, listener2);
    }

    @Test
    public void testDisconnectIsCalledOnTwoListeners() throws Exception {
        setupTwoListeners();
        listener1.onDisconnect();
        expectLastCall();
        listener2.onDisconnect();
        expectLastCall();
        replay(listener1, listener2);
        composite.onDisconnect();
        verify(listener1, listener2);
    }

}