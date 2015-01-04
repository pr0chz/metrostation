package cz.prochy.metrostation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import cz.prochy.metrostation.tracking.Builder;
import cz.prochy.metrostation.tracking.CellListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {
	
	private static final PragueStations stations = new PragueStations();

	private final static String LOG_NAME = "MetroStation";
	private final ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
	
	private CellListener rootListener;

	private class StateListener extends PhoneStateListener {
		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			switch (serviceState.getState()) {
			case ServiceState.STATE_OUT_OF_SERVICE:
				Log.v(LOG_NAME, "Disconnected");
				rootListener.disconnected();
				break;
			case ServiceState.STATE_EMERGENCY_ONLY:
			case ServiceState.STATE_IN_SERVICE:
				TelephonyManager tm = getTelephonyManager();
				if (tm != null) {
					CellLocation cl = tm.getCellLocation();
				
					if (cl != null) {
						if (cl instanceof GsmCellLocation) {
							GsmCellLocation gcl = (GsmCellLocation) cl;
							rootListener.cellInfo(gcl.getCid(), gcl.getLac());
						} else if (cl instanceof CdmaCellLocation) {
							CdmaCellLocation ccl = (CdmaCellLocation) cl;
							rootListener.cellInfo(ccl.getBaseStationId(), -1);
						}
					}
				}
				break;
			default:
				Log.v(LOG_NAME, "Other state");
				rootListener.disconnected();
			}
			super.onServiceStateChanged(serviceState);
		}
	}

	private CellListener buildListeners() {
        return Builder.createListener(scheduledService, TimeUnit.SECONDS.toSeconds(300), stations,
                new NotificationsImpl(this));
	}
	
	private TelephonyManager getTelephonyManager() {
		return (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.i(LOG_NAME, "Starting service...");
    	TelephonyManager tm = getTelephonyManager();
    	if (tm != null) {
    		rootListener = buildListeners();
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
        Log.i(LOG_NAME, "Shutting down service...");
    	scheduledService.shutdown();
    	try {
			if (!scheduledService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                Log.e(LOG_NAME, "Failed to stop scheduled service executor!");
            }
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
    }

}
