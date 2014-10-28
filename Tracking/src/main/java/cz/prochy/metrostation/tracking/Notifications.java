package cz.prochy.metrostation.tracking;

public interface Notifications {
        void toastIncomingStation(String station);
        void toastLeavingStation(String station);
        void notificationIncomingStation(String station);
        void notificationLeavingStation(String station);
        void hideNotification();
}
