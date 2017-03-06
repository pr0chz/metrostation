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

package cz.prochy.metrostation.tracking.internal.graph;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.internal.Station;
import cz.prochy.metrostation.tracking.internal.StationGroup;

import java.util.Map;

class StationBuilder implements cz.prochy.metrostation.tracking.graph.StationBuilder {

    private final Map<Long, StationGroup> cellMap;
    private final Station station;

    public StationBuilder(Map<Long, StationGroup> cellMap, Station station) {
        this.cellMap = Check.notNull(cellMap);
        this.station = Check.notNull(station);
    }

    private void addStation(long id) {
        if (!cellMap.containsKey(id)) {
            cellMap.put(id, new StationGroup());
        }
        cellMap.get(id).add(station);
    }

    @Override
    public StationBuilder id(String op, int cid, int lac) {
        addStation(TrackingGraphBuilder.longId(cid, lac));
        return this;
    }
}
