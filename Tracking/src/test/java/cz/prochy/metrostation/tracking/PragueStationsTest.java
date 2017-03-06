/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.PragueStations;
import cz.prochy.metrostation.tracking.internal.graph.StationGraph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PragueStationsTest {

    private StationGraph stations;

    @Before
    public void setUp() throws Exception {
        stations = PragueStations.newGraph();
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
        assertTrue(stations.getStations(2801, 21780).hasMultipleValues());
    }

    @Test
    public void testTransferStationHasFlag() throws Exception {
        assertTrue(stations.getStations(18836, 34300).getStation().isTransfer());
    }

}