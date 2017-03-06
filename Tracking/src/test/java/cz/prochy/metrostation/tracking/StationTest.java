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

import cz.prochy.metrostation.tracking.internal.Line;
import cz.prochy.metrostation.tracking.internal.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StationTest {

    private final static String STATION_NAME = "TEST";
    private final static String NEXT_STATION_NAME = "TEST2";

    private final Line line = new Line();

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
        station.addNext(line, nextStation);
        Set<Station> set = station.getNext();
        assertEquals(1, set.size());
        assertEquals(nextStation, set.iterator().next());
    }

    @Test
    public void testPrev() throws Exception {
        station.addPrev(line, nextStation);
        Set<Station> set = station.getPrev();
        assertEquals(1, set.size());
        assertEquals(nextStation, set.iterator().next());
    }

}