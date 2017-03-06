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

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifier;

public class NotifyListener implements StationListener {

    private final Notifier notifier;

    private StationGroup lastStations = StationGroup.empty();
    private StationGroup lastPredictions = StationGroup.empty();

    public NotifyListener(Notifier notifier) {
        this.notifier = Check.notNull(notifier);
    }

    @Override
    public void onStation(long ts, StationGroup stations, StationGroup predictions) {
        Check.notNull(stations);
        Check.notNull(predictions);
        lastStations = stations;
        lastPredictions = predictions;
        if (stations.hasSingleValue()) {
            notifier.onStation(stations.getStation().getName());
        } else if (stations.isEmpty()) {
            notifier.onUnknownStation();
        }
    }

    @Override
    public void onDisconnect(long ts) {
        if (lastStations.hasSingleValue()) {
            if (lastPredictions.hasSingleValue()) {
                notifier.onDisconnect(lastStations.getStation().getName(), lastPredictions.getStation().getName());
            } else {
                notifier.onDisconnect(lastStations.getStation().getName());
            }
        }
    }
}
