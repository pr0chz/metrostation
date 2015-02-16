package cz.prochy.metrostation.tracking;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class LineBuilderTest {

    private LineBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new LineBuilder();
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsOnNullStation() throws Exception {
        builder.addStation(null);
    }

    @Test
    public void testFirstStationNotChanged() throws Exception {
        Station station = createStrictMock(Station.class);
        replay(station);
        builder.addStation(station);
        verify(station);
    }

    @Test
    public void testSecondStationIsConnected() throws Exception {
        Station station1 = createStrictMock(Station.class);
        Station station2 = createStrictMock(Station.class);
        station1.setNext(eq(station2));
        expectLastCall().once();
        station2.setPrev(eq(station1));
        expectLastCall().once();
        replay(station1, station2);
        builder.addStation(station1);
        builder.addStation(station2);
        verify(station1, station2);
    }

    @Test
    public void testWithRealStation() throws Exception {
        final int testSize = 10;
        Station station = new Station("first");
        builder.addStation(station);
        for (int i=0; i<testSize; i++) {
            builder.addStation(new Station(Integer.toString(i)));
        }
        int counter = 0;
        while (!station.getNext().isEmpty() || counter > testSize * 10) {
            station = station.getNext().iterator().next();
            ++counter;
        }
        assertEquals(testSize, counter);
        assertEquals("9", station.getName());
    }
}