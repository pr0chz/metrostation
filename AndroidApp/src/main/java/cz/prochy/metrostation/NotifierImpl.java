package cz.prochy.metrostation;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;
import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifier;
import cz.prochy.metrostation.tracking.Timeout;

public class NotifierImpl implements Notifier {

    private static final int NOTIFICATION_ID = 0x6a3ab12f;
    private static final String UNKNOWN_STATION = "???";

    private final Context context;
    private final NotificationSettings settings;
    private final Timeout predictionTrigger;

    public NotifierImpl(Context context, NotificationSettings settings, Timeout predictionTrigger) {
        this.context = Check.notNull(context);
        this.settings = Check.notNull(settings);
        this.predictionTrigger = Check.notNull(predictionTrigger);
        hideNotification(); // if service was killed there can be something hanging around
    }

    @Override
    public void onStation(String approachingStation) {
        cancelPrediction();
        notifyArrival(approachingStation);
    }

    private void notifyArrival(String approachingStation) {
        toastArrival(approachingStation);
        showNotification(approachingStation);
    }

    @Override
    public void onUnknownStation() {
        cancelPrediction();
        hideNotification();
    }

    @Override
    public void onDisconnect(String leavingStation, String nextStation) {
        if (settings.getPredictions()) {
            toastDeparture(direction(leavingStation, nextStation));
            showNotification(direction(leavingStation, nextStation));
            schedulePrediction(nextStation);
        } else {
            onDisconnect(leavingStation);
        }
    }

    @Override
    public void onDisconnect(String leavingStation) {
        cancelPrediction();
        toastDeparture(direction(leavingStation, UNKNOWN_STATION));
        showNotification(direction(leavingStation, UNKNOWN_STATION));
    }

    private void cancelPrediction() {
        predictionTrigger.cancel();
    }

    private void schedulePrediction(final String nextStation) {
        predictionTrigger.reset(new Runnable() {
            @Override
            public void run() {
                notifyArrival(nextStation);
            }
        });
    }

    private String direction(String from, String to) {
        return Check.notNull(from) + " -> " + Check.notNull(to);
    }

    private void toastArrival(String message) {
        Check.notNull(message);
        if (settings.getToastOnArrival()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void toastDeparture(String message) {
        Check.notNull(message);
        if (settings.getToastOnDeparture()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void showNotification(String message) {
        Check.notNull(message);
        if (settings.getTrayNotification()) {
            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setContentTitle("Metro station")
                    .setContentText(Check.notNull(message))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = getNotificationManager();
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, builder.getNotification());
            }
        }
    }

    private void hideNotification() {
        NotificationManager notificationManager = getNotificationManager();
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

}
