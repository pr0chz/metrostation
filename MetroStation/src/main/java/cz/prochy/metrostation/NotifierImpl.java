package cz.prochy.metrostation;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifier;
import cz.prochy.metrostation.tracking.Timeout;

public class NotifierImpl implements Notifier {

    private static final int NOTIFICATION_ID = 0x6a3ab12f;
    private static final String UNKNOWN_STATION = "???";

    private static final Logger logger = new Logger();

    private final Context context;
    private final NotificationSettings settings;
    private final Timeout predictionTrigger;

    private volatile String predictedStation = null;

    private WindowManager windowManager;
    private TextView overlay;

    public NotifierImpl(Context context, NotificationSettings settings, Timeout predictionTrigger) {
        this.context = Check.notNull(context);
        this.settings = Check.notNull(settings);
        this.predictionTrigger = Check.notNull(predictionTrigger);
        hideNotification(); // if service was killed there can be something hanging around
    }

    @Override
    public void onStation(String approachingStation) {
        cancelPrediction();
        toastArrival(approachingStation);
        showNotification(approachingStation);
    }

    @Override
    public void onUnknownStation() {
        resetPredictedStation();
        cancelPrediction();
        hideNotification();
    }

    @Override
    public void onDisconnect(String leavingStation, String nextStation) {
        resetPredictedStation();
        toastDeparture(direction(leavingStation, nextStation));
        showNotification(direction(leavingStation, nextStation));
        schedulePrediction(nextStation);
    }

    @Override
    public void onDisconnect(String leavingStation) {
        resetPredictedStation();
        cancelPrediction();
        toastDeparture(direction(leavingStation, UNKNOWN_STATION));
        showNotification(direction(leavingStation, UNKNOWN_STATION));
    }

    private void resetPredictedStation() {
        predictedStation = null;
    }

    private void cancelPrediction() {
        predictionTrigger.cancel();
    }

    private void schedulePrediction(final String nextStation) {
        Check.notNull(nextStation);
        if (settings.getPredictions()) {
            predictionTrigger.reset(new Runnable() {
                @Override
                public void run() {
                    try {
                        Handler handler = new Handler(context.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                toastArrival(nextStation);
                                predictedStation = nextStation;
                                showNotification(nextStation);
                            }
                        });
                    } catch (Exception e) {
                        logger.log(e);
                    }
                }
            });
        }
    }

    private String direction(String from, String to) {
        return Check.notNull(from) + " -> " + Check.notNull(to);
    }

    private void toastArrival(String message) {
        Check.notNull(message);
        if (settings.getToastOnArrival()) {
            if (!message.equals(predictedStation)) { // do not notify again if station was successfully predicted
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } else {
                resetPredictedStation();
            }
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

    synchronized private void showOverlay(String message) {
        Check.notNull(message);
        if (overlay == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            if (windowManager != null) {
                overlay = new TextView(context);
                overlay.setBackgroundColor(0x88000000);
                overlay.setTextColor(0xffffffff);

                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);

                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                windowManager.addView(overlay, params);
            }
            overlay.setText(message);
        }
    }

    synchronized private void hideOverlay() {
        if (overlay != null) {
            windowManager.removeView(overlay);
            overlay = null;
        }
    }

    private void showNotification(String message) {
        Check.notNull(message);
        if (settings.getOverlay()) {
            showOverlay(message);
        }
        if (settings.getTrayNotification()) {
            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setContentTitle(Check.notNull(message))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true);

            NotificationManager notificationManager = getNotificationManager();
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, builder.getNotification());
            }
        }
    }

    private void hideNotification() {
        hideOverlay();
        NotificationManager notificationManager = getNotificationManager();
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

}
