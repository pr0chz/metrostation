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

/**
 * General interface for listener observing station events. All events act as a state transition
 * so you can expect that each will be called just once in a row (e.g. you will not receive
 * the same event multiple times).
 */
public interface StationListener {

    public final static StationGroup NO_STATIONS = StationGroup.empty();

    /**
     * There has been a change in GSM cells and this set represents possible stations where we are. When empty
     * cell ids do not map to any known station.
     * @param stations Station.
     */
    void onStation(long ts, StationGroup stations, StationGroup predictions);

    /**
     * We have been disconnected from network. If last cell was a station this probably
     * means we are in a tunnel travelling to next station.
     */
    void onDisconnect(long ts);

}
