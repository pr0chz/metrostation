package cz.prochy.metrostation;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service {
	
	private final static String LOG_NAME = "MetroNotifier";
	
	private int currentCellId;
	private AtomicBoolean notified = new AtomicBoolean();
	
	private ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
	private Future<?> scheduledTask;
	
	private static final Stations stations = new Stations();
	
	private static final int NOTIFICATION_ID = 0xbadbeef;
	
	private final Runnable cancelNotificationTask = new Runnable() {
		public void run() {
			NotificationManagerCompat.from(NotificationService.this).cancel(NOTIFICATION_ID);
			notified.set(false);
		};
	};
	
	private void rescheduleCancelNotification() {
		if (scheduledTask != null) {
			scheduledTask.cancel(false);
		}
		scheduledTask = scheduledService.schedule(cancelNotificationTask, 180, TimeUnit.SECONDS);
	}
	
	private void connected(int cellId) {
		if (stations.isStation(cellId) && notified.compareAndSet(false, true)) {
			String name = stations.getName(cellId);
			Log.v(LOG_NAME, "Matched! Notifying... " + name);
			notifyStation(name, !name.equals(stations.getName(currentCellId)), true);
		}
		currentCellId = cellId;
	}
	
	private void disconnected() {
		if (notified.compareAndSet(true, false) && stations.isStation(currentCellId)) {
			notifyStation(stations.getName(currentCellId), false, false);
		}
	}
	
	private void notifyStation(String stationName, boolean toast, boolean connected) {
		rescheduleCancelNotification();
		
		if (toast) {
			Toast.makeText(this, stationName, Toast.LENGTH_SHORT).show();
		}

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
        	.setSmallIcon(R.drawable.ic_launcher)
        	.setContentTitle("Metro station")
        	.setContentText(stationName + (connected ? "" : " -> ???"))
        	.setAutoCancel(true);
 
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
	}

	private class StateListener extends PhoneStateListener {
		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
	    	//Log.v(LOG_NAME, "Received state update " + serviceState);
			switch (serviceState.getState()) {
			case ServiceState.STATE_OUT_OF_SERVICE:
				Log.v(LOG_NAME, "Disconnected");
				disconnected();
				break;
			case ServiceState.STATE_EMERGENCY_ONLY:
			case ServiceState.STATE_IN_SERVICE:
				TelephonyManager tm = getTelephonyManager();
				if (tm != null) {
					CellLocation cl = tm.getCellLocation();
				
					if (cl != null) {
						int cellId = -1;
						if (cl instanceof GsmCellLocation) {
							GsmCellLocation gcl = (GsmCellLocation) cl;
							cellId = gcl.getCid();
						} else if (cl instanceof CdmaCellLocation) {
							CdmaCellLocation ccl = (CdmaCellLocation) cl;
							cellId = ccl.getBaseStationId();
						}
						
						Log.v(LOG_NAME, "Connected to " + cellId);
						if (cellId != -1) {
							connected(cellId);
						}
					}
				}
				break;
			default:
				Log.v(LOG_NAME, "Other state");
				disconnected();
			}
			super.onServiceStateChanged(serviceState);
		}
	}
	
	private TelephonyManager getTelephonyManager() {
		return (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.v(LOG_NAME, "Starting service...");
    	TelephonyManager tm = getTelephonyManager();
    	if (tm != null) {
    		tm.listen(new StateListener(), PhoneStateListener.LISTEN_SERVICE_STATE);
    		Log.v(LOG_NAME, "Listener registered");
    	} else {
    		Log.e(LOG_NAME, "Failed to run service, unable to obtain telephony manager!");
    	}
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
    	scheduledService.shutdown();
    	try {
			scheduledService.awaitTermination(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
    }

}
