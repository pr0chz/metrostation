package cz.prochy.metrostation.tracking;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StationTest {

    private final static String STATION_NAME = "TEST";
    private final static String NEXT_STATION_NAME = "TEST2";

    private Station station;
    private Station nextStation;

    @Before
    public void setUp() throws Exception {
        station = new Station(STATION_NAME);
        nextStation = new Station(NEXT_STATION_NAME);
    }

    @Test
    public void testStationName() throws Exception {
        assertEquals(STATION_NAME, station.getName());
    }

    @Test
    public void testEmptyNext() throws Exception {
        Set<Station> set = station.getNext();
        assertTrue(set.isEmpty());
    }

    @Test
    public void testEmptyPrev() throws Exception {
        Set<Station> set = station.getPrev();
        assertTrue(set.isEmpty());
    }

    @Test
    public void testNext() throws Exception {
        station.setNext(nextStation);
        Set<Station> set = station.getNext();
        assertEquals(1, set.size());
        assertEquals(nextStation, set.iterator().next());
    }

    @Test
    public void testPrev() throws Exception {
        station.setPrev(nextStation);
        Set<Station> set = station.getPrev();
        assertEquals(1, set.size());
        assertEquals(nextStation, set.iterator().next());
    }

}