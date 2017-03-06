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

public class Deduplicator implements Notifier {

    private static enum LastNotification {
        STATION, UNKNOWN_STATION, DISCONNECT;
    }

    private final Notifier notifier;

    private LastNotification lastNotification;
    private String lastApproachingStation;
    private String lastLeavingStation;

    public Deduplicator(Notifier notifier) {
        this.notifier = Check.notNull(notifier);
    }

    @Override
    public void onStation(String approachingStation) {
        Check.notNull(approachingStation);
        if (!approachingStation.equals(lastApproachingStation) || lastNotification != LastNotification.STATION) {
            lastNotification = LastNotification.STATION;
            lastApproachingStation = approachingStation;
            notifier.onStation(approachingStation);
        }
    }

    @Override
    public void onUnknownStation() {
        if (lastNotification != LastNotification.UNKNOWN_STATION) {
            lastNotification = LastNotification.UNKNOWN_STATION;
            notifier.onUnknownStation();
        }
    }

    @Override
    public void onDisconnect(String leavingStation, String nextStation) {
        Check.notNull(leavingStation);
        Check.notNull(nextStation);
        if (!leavingStation.equals(lastLeavingStation) || lastNotification != LastNotification.DISCONNECT) {
            lastNotification = LastNotification.DISCONNECT;
            lastLeavingStation = leavingStation;
            notifier.onDisconnect(leavingStation, nextStation);
        }

    }

    @Override
    public void onDisconnect(String leavingStation) {
        Check.notNull(leavingStation);
        if (!leavingStation.equals(lastLeavingStation) || lastNotification != LastNotification.DISCONNECT) {
            lastNotification = LastNotification.DISCONNECT;
            lastLeavingStation = leavingStation;
            notifier.onDisconnect(leavingStation);
        }
    }

}
