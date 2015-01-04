package cz.prochy.metrostation.tracking;

public interface Notifications {
        void toastStationArrival(String station);
        void toastStationDeparture(String station);
        void notifyStationArrival(String station);
        void notifyStationDeparture(String station);
        void hideNotification();
}
