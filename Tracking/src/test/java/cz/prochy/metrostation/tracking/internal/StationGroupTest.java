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

package cz.prochy.metrostation.tracking.internal;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class StationGroupTest {

    private StationGroup stationGroup;

    @Before
    public void setUp() throws Exception {
        stationGroup = new StationGroup();
    }

    private Station addStation() {
        Station station = createNiceMock(Station.class);
        stationGroup.add(station);
        return station;
    }

    @Test(expected = NullPointerException.class)
    public void testNullGroupThrows() throws Exception {
        new StationGroup(null);
    }

    @Test
    public void testGroupIsEmptyAfterCreation() throws Exception {
        assertTrue(stationGroup.isEmpty());
    }

    @Test
    public void testGroupIsNotEmptyWhenItContainsStation() throws Exception {
        addStation();
        assertFalse(stationGroup.isEmpty());
    }

    @Test
    public void testHasSingleValue() throws Exception {
        assertFalse(stationGroup.hasSingleValue());
        addStation();
        assertTrue(stationGroup.hasSingleValue());
        addStation();
        assertFalse(stationGroup.hasSingleValue());
    }

    @Test
    public void testHasMultipleValues() throws Exception {
        assertFalse(stationGroup.hasMultipleValues());
        addStation();
        assertFalse(stationGroup.hasMultipleValues());
        addStation();
        assertTrue(stationGroup.hasMultipleValues());
    }

    @Test
    public void testGetStation() throws Exception {
        Station station = addStation();
        assertSame(station, stationGroup.getStation());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetStationThrowsForEmptyGroup() throws Exception {
        stationGroup.getStation();
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetStationThrowsForBiggerGroup() throws Exception {
        addStation();
        addStation();
        stationGroup.getStation();
    }

    @Test
    public void testCopyConstructorContainsSameStations() throws Exception {
        addStation();
        addStation();
        assertEquals(stationGroup, new StationGroup(stationGroup));
    }

    @Test
    public void testCopyConstructorDoesACopy() throws Exception {
        addStation();
        addStation();
        StationGroup copy = new StationGroup(stationGroup);
        addStation();
        assertThat(stationGroup, not(equalTo(copy)));
    }

    @Test
    public void testImmutableContainsSameStations() throws Exception {
        addStation();
        addStation();
        assertEquals(stationGroup, stationGroup.immutable());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImmutableFailsOnAdd() throws Exception {
        stationGroup.immutable().add(createNiceMock(Station.class));
    }

    @Test
    public void testImmutableOfImmutableDoesNotCreateNewObject() throws Exception {
        StationGroup immutable = stationGroup.immutable();
        assertSame(immutable, immutable.immutable());
    }

    @Test(expected = NullPointerException.class)
    public void testAddThrowsOnNull() throws Exception {
        stationGroup.add(null);
    }

    @Test
    public void testIntersection() throws Exception {
        addStation();
        Station commonStation = addStation();

        StationGroup otherGroup = new StationGroup();
        otherGroup.add(commonStation);
        otherGroup.add(createNiceMock(Station.class));

        StationGroup intersection = stationGroup.intersect(otherGroup);

        assertSame(commonStation, intersection.getStation());
    }

    @Test
    public void testLeft() throws Exception {
        Station leftStation = createNiceMock(Station.class);
        Station station = createNiceMock(Station.class);
        expect(station.getPrev()).andReturn(new HashSet<>(Arrays.asList(leftStation))).anyTimes();
        expect(station.getNext()).andReturn(new HashSet<>()).anyTimes();
        replay(leftStation, station);

        stationGroup.add(station);
        assertSame(leftStation, stationGroup.left().getStation());
        assertTrue(stationGroup.right().isEmpty());
    }

    @Test
    public void testRight() throws Exception {
        Station rightStation = createNiceMock(Station.class);
        Station station = createNiceMock(Station.class);
        expect(station.getNext()).andReturn(new HashSet<>(Arrays.asList(rightStation))).anyTimes();
        expect(station.getPrev()).andReturn(new HashSet<>()).anyTimes();
        replay(rightStation, station);

        stationGroup.add(station);
        assertSame(rightStation, stationGroup.right().getStation());
        assertTrue(stationGroup.left().isEmpty());
    }

    @Test
    public void testEquality() throws Exception {
        Station station = createNiceMock(Station.class);
        StationGroup stationGroup1 = StationGroup.from(station);
        StationGroup stationGroup2 = StationGroup.from(station);
        assertEquals(stationGroup1, stationGroup2);
        assertEquals(stationGroup1.hashCode(), stationGroup2.hashCode());
    }

}