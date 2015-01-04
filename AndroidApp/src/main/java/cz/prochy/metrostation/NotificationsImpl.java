package cz.prochy.metrostation;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;
import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifications;

public class NotificationsImpl implements Notifications {

	private static final int NOTIFICATION_ID = 0x6a3ab12f;
	
	private final Context context;
    private final NotificationSettings settings;
	
	public NotificationsImpl(Context context, NotificationSettings settings) {
        this.context = Check.notNull(context);
        this.settings = Check.notNull(settings);
        hideNotification(); // if service was killed there can be something hanging around
	}

    @Override
	public void toastStationArrival(String station) {
        if (settings.getToastOnArrival()) {
            Toast.makeText(context, Check.notNull(station), Toast.LENGTH_SHORT).show();
        }
	}

    @Override
	public void toastStationDeparture(String station) {
        if (settings.getToastOnDeparture()) {
            Toast.makeText(context, Check.notNull(station) + " -> ???", Toast.LENGTH_SHORT).show();
        }
	}

    private NotificationManager getNotificationManager() {
        return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

	private void showNotification(String message) {
        if (settings.getTrayNotification()) {
            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setContentTitle("Metro station")
                    .setContentText(Check.notNull(message))
                    .setAutoCancel(true);

            NotificationManager notificationManager = getNotificationManager();
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, builder.getNotification());
            }
        }
	}

    @Override
	public void notifyStationArrival(String station) {
        showNotification(Check.notNull(station));
	}

    @Override
	public void notifyStationDeparture(String station) {
		showNotification(Check.notNull(station) + " -> ???");		
	}

    @Override
	public void hideNotification() {
        NotificationManager notificationManager = getNotificationManager();
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
	}
	
}
