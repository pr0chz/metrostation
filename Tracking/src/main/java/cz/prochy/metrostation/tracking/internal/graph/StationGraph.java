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
import cz.prochy.metrostation.tracking.internal.StationGroup;

import java.util.HashMap;
import java.util.Map;

public class StationGraph {

    private final Map<Long, StationGroup> cellMap = new HashMap<>();

    public StationGraph(Map<Long, StationGroup> cellMap) {
        Check.notNull(cellMap);
        for (Long id : cellMap.keySet()) {
            this.cellMap.put(id, cellMap.get(id).immutable());
        }
    }

    public StationGroup getStations(int cellId, int lac) {
        if (cellMap.containsKey(TrackingGraphBuilder.longId(cellId, lac))) {
            return cellMap.get(TrackingGraphBuilder.longId(cellId, lac));
        } else {
            return StationGroup.empty();
        }
    }

}
