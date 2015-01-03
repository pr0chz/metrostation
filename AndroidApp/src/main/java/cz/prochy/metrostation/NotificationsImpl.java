package cz.prochy.metrostation;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifications;

public class NotificationsImpl implements Notifications {

	private static final int NOTIFICATION_ID = 0x6a3ab12f;
	
	private final Context context;
	
	public NotificationsImpl(Context context) {
        this.context = Check.notNull(context);
        hideNotification(); // if service was killed there can be something hanging around
	}

    @Override
	public void toastIncomingStation(String station) {
		Toast.makeText(context, Check.notNull(station), Toast.LENGTH_SHORT).show();
	}

    @Override
	public void toastLeavingStation(String station) {
		//Toast.makeText(context, Check.notNull(station) + " -> ???", Toast.LENGTH_SHORT).show();
	}

    private NotificationManager getNotificationManager() {
        return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

	private void showNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
    		.setSmallIcon(R.drawable.ic_stat_notify)
    		.setContentTitle("Metro station")
    		.setContentText(Check.notNull(message))
    		.setAutoCancel(true);

        NotificationManager notificationManager = getNotificationManager();
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
	}

    @Override
	public void notificationIncomingStation(String station) {
		showNotification(Check.notNull(station));
	}

    @Override
	public void notificationLeavingStation(String station) {
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
