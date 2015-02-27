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
