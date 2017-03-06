package cz.prochy.metrostation;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class NotificationService extends Service {

    private final static String LOG_NAME = "MSNotificationService";
    private final static String UPLOAD_URL_METADATA = "uploadUrl";

    public final static String START_ACTION = NotificationService.class.getName() + ".start";
    public final static String MOCK_ACTION = NotificationService.class.getName() + ".mock";

    private volatile EventProcessor eventProcessor;

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_NAME, "Starting service...");

        if (intent != null && MOCK_ACTION.equals(intent.getAction())) {
            if (eventProcessor != null) {
                eventProcessor.playbackMockEvents();
            }
        } else {
            if (eventProcessor == null) {
                eventProcessor = new EventProcessor(this);
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public synchronized void onDestroy() {
        Log.i(LOG_NAME, "Shutting down service...");
        eventProcessor.shutdown();
        eventProcessor = null;
    }

    public boolean networkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public String getUploadUrl() {
        String url = null;
        try {
            ComponentName myService = new ComponentName(this, this.getClass());
            Bundle data = getPackageManager().getServiceInfo(myService, PackageManager.GET_META_DATA).metaData;
            url = data.getString(UPLOAD_URL_METADATA);
        } catch (Exception e) {
            Log.e(LOG_NAME, "Failed to retrieve URL", e);
        }
        return url;
    }

    private TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    public void setListenerStatus(PhoneStateListener stateListener, int mask) {
        if (stateListener != null) {
            TelephonyManager tm = getTelephonyManager();
            if (tm != null) {
                tm.listen(stateListener, mask);
                Log.v(LOG_NAME, "Listener registered");
            } else {
                Log.e(LOG_NAME, "Failed to set listener state, unable to obtain telephony manager!");
            }
        } else {
            Log.e(LOG_NAME, "Cannot set status, state listener is null!");
        }
    }

    public CellLocation getLocation() {
        CellLocation cellLocation = null;
        TelephonyManager tm = getTelephonyManager();
        if (tm != null) {
            cellLocation = tm.getCellLocation();
        }
        return cellLocation;
    }

}
