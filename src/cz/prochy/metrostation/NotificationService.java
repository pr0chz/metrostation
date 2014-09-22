package cz.prochy.metrostation;

import java.util.HashMap;
import java.util.Map;

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
	private int lastCellId;
	private boolean notified;

	private static final Map<Integer, String> cellMap = new HashMap<Integer, String>();
	static {
		cellMap.put(18812, ">A< Skalka");
		cellMap.put(18809, ">A< Strasnicka");
		cellMap.put(18811, ">A< Zelivskeho");
		cellMap.put(18808, ">A< Flora");
		cellMap.put(18810, ">A< Jiriho z Podebrad");
		cellMap.put(18807, ">A< Namesti Miru");
		cellMap.put(18806, ">A< Muzeum");
		cellMap.put(18853, ">C< Muzeum");
		cellMap.put(18839, ">C< Hlavni nadrazi");
		cellMap.put(116348, ">X< Test");
	}
	
	private static final int NOTIFICATION_ID = 0xbadbeef;
	
	private boolean isMetroStation(int cellId) {
		return cellMap.containsKey(cellId);
	}
	
	private void connected(int cellId) {
		currentCellId = cellId;
		if (isMetroStation(lastCellId) && isMetroStation(currentCellId)) {
			if (!notified) {
				notifyStation();
				notified = true;
			}
		}
	}
	
	private void disconnected() {
		lastCellId = currentCellId;
		notified = false;
	}
	
	private void notifyStation() {
		Log.v(LOG_NAME, "Matched! Notifying... " + cellMap.get(currentCellId));
		
		Toast toast = Toast.makeText(this, cellMap.get(currentCellId), Toast.LENGTH_SHORT);
		toast.show();
		
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
        	.setSmallIcon(R.drawable.ic_launcher)
        	.setContentTitle("Metro station")
        	.setContentText(cellMap.get(currentCellId))
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
			case ServiceState.STATE_IN_SERVICE:
				TelephonyManager tm = getTelephonyManager();
				CellLocation cl = tm.getCellLocation();
			
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
				break;
			default:
				Log.v(LOG_NAME, "Other state");
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
    	TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(new StateListener(), PhoneStateListener.LISTEN_SERVICE_STATE);
    	Log.v(LOG_NAME, "Listener registered");
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
