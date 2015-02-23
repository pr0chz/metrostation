package cz.prochy.metrostation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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
import cz.prochy.metrostation.tracking.Notifier;
import cz.prochy.metrostation.tracking.Timeout;
import cz.prochy.metrostation.tracking.internal.PragueStations;
import net.jcip.annotations.ThreadSafe;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ThreadSafe
public class NotificationService extends Service {

    private final static PragueStations stations = new PragueStations();
    private final static String LOG_NAME = "MetroStation";
    private final File logFile = new File(Environment.getExternalStorageDirectory(), "ms.log");

    private final ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();

    private StateListener stateListener;

    private class StateListener extends PhoneStateListener {

        private final CellListener listener;

        private StateListener(CellListener listener) {
            this.listener = listener;
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            try {
                long ts = System.currentTimeMillis();
                switch (serviceState.getState()) {
                    case ServiceState.STATE_OUT_OF_SERVICE:
                        Log.v(LOG_NAME, "Disconnected");
                        log(disconnectMessage());
                        listener.disconnected(ts);
                        break;
                    case ServiceState.STATE_EMERGENCY_ONLY:
                    case ServiceState.STATE_IN_SERVICE:
                        TelephonyManager tm = getTelephonyManager();
                        if (tm != null) {
                            CellLocation cl = tm.getCellLocation();

                            if (cl != null) {
                                if (cl instanceof GsmCellLocation) {
                                    GsmCellLocation gcl = (GsmCellLocation) cl;
                                    log(cellMessage(gcl.getCid(), gcl.getLac()));
                                    listener.cellInfo(ts, gcl.getCid(), gcl.getLac());
                                } else if (cl instanceof CdmaCellLocation) {
                                    CdmaCellLocation ccl = (CdmaCellLocation) cl;
                                    log(cellMessage(ccl.getBaseStationId(), -1));
                                    listener.cellInfo(ts, ccl.getBaseStationId(), -1);
                                }
                            }
                        }
                        break;
                    default:
                        Log.v(LOG_NAME, "Other state");
                        log(disconnectMessage());
                        listener.disconnected(ts);
                }
                super.onServiceStateChanged(serviceState);
            } catch (Throwable e) {
                log(getStackTrace(e));
            }
        }
    }

    private String disconnectMessage() {
        return "{\"ts\": " + System.currentTimeMillis() + "}\n";
    }

    private String cellMessage(int cid, int lac) {
        return "{\"ts\": " + System.currentTimeMillis() + ", \"cid\": " + cid + ", \"lac\": " + lac + "}\n";
    }

    public void log(String message) {
        try {
            OutputStream str = null;
            try {
                str = new FileOutputStream(logFile, true);
                OutputStreamWriter writer = new OutputStreamWriter(str);
                writer.write(message);
                writer.close();
            } finally {
                str.close();
            }
        } catch (IOException e) {
            Log.e(LOG_NAME, e + getStackTrace(e));
        }
    }

    public static String getStackTrace(Throwable aThrowable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        printWriter.write(aThrowable.toString() + ":\n");
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    private CellListener buildListeners() {
        Timeout predictionTrigger = new Timeout(scheduledService, 25, TimeUnit.SECONDS);
        Notifier notifier = new NotifierImpl(this, new NotificationSettings(this), predictionTrigger);
        long stationTimeout = TimeUnit.SECONDS.toMillis(180);
        long transferTimeout = TimeUnit.SECONDS.toMillis(90);
        return Builder.createListener(stations, notifier, stationTimeout, transferTimeout);
    }

    private TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    private void setListenerStatus(int mask) {
        if (stateListener != null) {
            TelephonyManager tm = getTelephonyManager();
            if (tm != null) {
                tm.listen(stateListener, mask);
                Log.v(LOG_NAME, "Listener registered");
            } else {
                Log.e(LOG_NAME, "Failed to set listener state, unable to obtain telephony manager!");
            }
        } else {
            Log.e(LOG_NAME, "Failed to set listener state, listener is not initialized!");
        }
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_NAME, "Starting service...");
        log("Starting...\n");
        if (stateListener == null) {
            stateListener = new StateListener(buildListeners());
        }
        setListenerStatus(PhoneStateListener.LISTEN_SERVICE_STATE);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public synchronized void onDestroy() {
        Log.i(LOG_NAME, "Shutting down service...");
        log("Shutting down...\n");
        setListenerStatus(PhoneStateListener.LISTEN_NONE);
        stateListener = null;
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
