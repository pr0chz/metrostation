package cz.prochy.metrostation;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;
import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.Notifications;

public class NotificationsImpl implements Notifications {

	private static final int NOTIFICATION_ID = 0xba3dbeef;
	
	private final Context context;
	
	public NotificationsImpl(Context context) {
		this.context = Check.notNull(context);
	}

    @Override
	public void toastIncomingStation(String station) {
		Toast.makeText(context, Check.notNull(station), Toast.LENGTH_SHORT).show();
	}

    @Override
	public void toastLeavingStation(String station) {
		Toast.makeText(context, Check.notNull(station) + " -> ???", Toast.LENGTH_SHORT).show();		
	}
	
	private void showNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
    		.setSmallIcon(R.drawable.ic_stat_notify)
    		.setContentTitle("Metro station")
    		.setContentText(Check.notNull(message))
    		.setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
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
		NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
	}
	
}
