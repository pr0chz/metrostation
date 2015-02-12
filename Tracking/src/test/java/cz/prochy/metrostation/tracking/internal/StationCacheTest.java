package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StationCacheTest {

    private static final long MAX_DELAY_MS = 10;
    private static final long TIMESTAMP_MS = 1000;

    private static final Station STATION = createNiceMock(Station.class);
    private static final Station STATION2 = createNiceMock(Station.class);

    private StationCache cache;

    @Before
    public void setUp() throws Exception {
        cache = new StationCache(MAX_DELAY_MS);
    }

    @Test
    public void testCacheIsEmpty() throws Exception {
        assertTrue(cache.getEntries(TIMESTAMP_MS).isEmpty());
    }

    @Test
    public void testStationIsAdded() throws Exception {
        cache.add(TIMESTAMP_MS, STATION);
        List<StationCache.Entry> list = cache.getEntries(TIMESTAMP_MS);
        assertEquals(1, list.size());
        assertEquals(STATION, list.get(0).station);
        assertEquals(TIMESTAMP_MS, list.get(0).timestamp);
    }

    @Test(expected = NullPointerException.class)
    public void testNullStationThrows() throws Exception {
        cache.add(TIMESTAMP_MS, null);
    }

    private void generateStations() {
        for (int i=0; i<10; i++) {
            cache.add(TIMESTAMP_MS, STATION);
        }
    }

    @Test
    public void testCacheLimitForTwoItems() throws Exception {
        generateStations();
        List<StationCache.Entry> list = cache.getEntries(TIMESTAMP_MS);
        assertEquals(2, list.size());
    }

    @Test
    public void testCacheLIFO() throws Exception {
        generateStations();
        cache.add(TIMESTAMP_MS, STATION2);
        List<StationCache.Entry> list = cache.getEntries(TIMESTAMP_MS);
        assertEquals(STATION, list.get(0).station);
        assertEquals(STATION2, list.get(1).station);
    }

    @Test
    public void testOldRecordsAreRemoved() throws Exception {
        cache.add(TIMESTAMP_MS, STATION);
        assertFalse(cache.getEntries(TIMESTAMP_MS + MAX_DELAY_MS).isEmpty());
        assertTrue(cache.getEntries(TIMESTAMP_MS + MAX_DELAY_MS + 1).isEmpty());
        assertTrue(cache.getEntries(TIMESTAMP_MS + MAX_DELAY_MS).isEmpty());
    }

    @Test
    public void testReturnedListDoesNotMutate() throws Exception {
        cache.add(TIMESTAMP_MS, STATION);
        List<StationCache.Entry> list = cache.getEntries(TIMESTAMP_MS);
        cache.add(TIMESTAMP_MS, STATION2);
        cache.add(TIMESTAMP_MS, STATION2);
        assertEquals(1, list.size());
        assertEquals(STATION, list.get(0).station);
        assertEquals(TIMESTAMP_MS, list.get(0).timestamp);
    }
}