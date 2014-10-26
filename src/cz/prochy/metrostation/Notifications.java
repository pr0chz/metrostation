package cz.prochy.metrostation;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

public class Notifications {

	private static final int NOTIFICATION_ID = 0xba3dbeef;
	
	private final Context context;
	
	public Notifications(Context context) {
		this.context = Check.notNull(context);
	}
	
	public void toastIncomingStation(String station) {
		Toast.makeText(context, Check.notNull(station), Toast.LENGTH_SHORT).show();
	}
	
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
	
	public void notificationIncomingStation(String station) {
		showNotification(Check.notNull(station));
	}
	
	public void notificationLeavingStation(String station) {
		showNotification(Check.notNull(station) + " -> ???");		
	}
	
	public void hideNotification() {
		NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
	}
	
}
