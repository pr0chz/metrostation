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
import cz.prochy.metrostation.tracking.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {
	
	private static final PragueStations stations = new PragueStations();

	private final static String LOG_NAME = "MetroNotifier";
	private final ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
	
	private CellListener rootListener;

	private class StateListener extends PhoneStateListener {
		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
	    	//Log.v(LOG_NAME, "Received state update " + serviceState);
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
						int cellId = -1;
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
		CompositeStationListener compositeStationListener = new CompositeStationListener();
		StationsCellListener stationsCellListener = new StationsCellListener(stations, compositeStationListener);
		CellListener rootListener = new CellListenerFilter(stationsCellListener);
		
		NotificationsImpl notificationsImpl = new NotificationsImpl(this);
		Timeout timeout = new Timeout(scheduledService, 300, TimeUnit.SECONDS);
		
		compositeStationListener.addListener(new ToastStationListener(notificationsImpl));
		compositeStationListener.addListener(new NotificationStationListener(notificationsImpl, timeout));
		
		return rootListener;
	}
	
	private TelephonyManager getTelephonyManager() {
		return (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.v(LOG_NAME, "Starting service...");
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
    	scheduledService.shutdown();
    	try {
			scheduledService.awaitTermination(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
    }

}
