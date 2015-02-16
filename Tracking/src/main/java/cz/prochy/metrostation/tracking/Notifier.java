package cz.prochy.metrostation.tracking;

public interface Notifier {
    void onStation(String approachingStation);
    void onUnknownStation();
    void onDisconnect(String leavingStation, String nextStation);
    void onDisconnect(String leavingStation);
}
