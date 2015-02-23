package cz.prochy.metrostation.tracking.internal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PragueStationsTest {

    private PragueStations stations;

    @Before
    public void setUp() throws Exception {
        stations = new PragueStations();
    }

    @Test
    public void testUnknownStation() throws Exception {
        assertTrue(stations.getStations(21322, 232131).isEmpty());
    }

    @Test
    public void testSingleMatchingStation() throws Exception {
        assertTrue(stations.getStations(18812, 34300).hasSingleValue());
    }

    @Test
    public void testMultipleMatchingStations() throws Exception {
        assertTrue(stations.getStations(21198, 1139).hasMultipleValues());
    }

    @Test
    public void testTransferStationHasFlag() throws Exception {
        assertTrue(stations.getStations(18836, 34300).getStation().isTransfer());
    }

}