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

import cz.prochy.metrostation.tracking.Notifier;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class NotifyListenerTest {

    private final static String STATION1 = "1432STATION#21";
    private final static String STATION2 = "1432STAT32ION#22";

    private NotifyListener notifyListener;
    private Notifier notifier;
    private StepVerifier verifier;

    private static final StationGroup EMPTY = StationGroup.empty();
    private static final StationGroup SINGLE = StationGroup.from(new Station(STATION1));
    private static final StationGroup SINGLE2 = StationGroup.from(new Station(STATION2));
    private static final StationGroup MULTIPLE = StationGroup.from(new Station(STATION1), new Station(STATION2));

    @Before
    public void setUp() throws Exception {
        notifier = createStrictMock(Notifier.class);
        notifyListener = new NotifyListener(notifier);
        verifier = new StepVerifier(notifier);
    }

    @Test(expected = NullPointerException.class)
    public void testNullNotifierThrows() throws Exception {
        new NotifyListener(null);
    }

    private Runnable expectOnStation(String name) {
        return () -> notifier.onStation(eq(name));
    }

    private Runnable expectOnUnknown() {
        return () -> notifier.onUnknownStation();
    }

    private Runnable expectNothing() {
        return () -> {};
    }

    private Runnable expectDisconnect(String name) {
        return () -> notifier.onDisconnect(eq(name));
    }

    private Runnable expectDisconnect(String name, String next) {
        return () -> notifier.onDisconnect(eq(name), eq(next));
    }

    private Runnable onStation(StationGroup stations, StationGroup predictions) {
        return () -> notifyListener.onStation(0, stations, predictions);
    }

    private Runnable onDisconnect() {
        return () -> notifyListener.onDisconnect(0);
    }

    @Test
    public void testSingleStation() throws Exception {
        verifier.step(onStation(SINGLE, MULTIPLE), expectOnStation(STATION1));
    }

    @Test
    public void testEmptyStation() throws Exception {
        verifier.step(onStation(EMPTY, EMPTY), expectOnUnknown());
    }

    @Test
    public void testMultipleStations() throws Exception {
        verifier.step(onStation(MULTIPLE, EMPTY), expectNothing());
    }

    @Test
    public void testDisconnectWithPredictionStation() throws Exception {
        verifier.step(onStation(SINGLE, SINGLE2), expectOnStation(STATION1));
        verifier.step(onDisconnect(), expectDisconnect(STATION1, STATION2));
    }

    @Test
    public void testDisconnectWithoutPredictionStation() throws Exception {
        verifier.step(onStation(SINGLE, EMPTY), expectOnStation(STATION1));
        verifier.step(onDisconnect(), expectDisconnect(STATION1));
    }

    @Test
    public void testDisconnectWithoutStation() throws Exception {
        verifier.step(onStation(EMPTY, EMPTY), expectOnUnknown());
        verifier.step(onDisconnect(), expectNothing());
    }

    @Test
    public void testDisconnectWithMultiplePredictions() throws Exception {
        verifier.step(onStation(SINGLE, MULTIPLE), expectOnStation(STATION1));
        verifier.step(onDisconnect(), expectDisconnect(STATION1));
    }

    @Test
    public void testDisconnectWithMultipleStations() throws Exception {
        verifier.step(onStation(MULTIPLE, MULTIPLE), expectNothing());
        verifier.step(onDisconnect(), expectNothing());
    }



}