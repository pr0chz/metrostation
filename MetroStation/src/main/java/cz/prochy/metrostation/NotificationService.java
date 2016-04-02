package cz.prochy.metrostation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import cz.prochy.metrostation.tracking.*;
import cz.prochy.metrostation.tracking.internal.PragueStations;
import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ThreadSafe
public class NotificationService extends Service {

    private final static String LOG_NAME = "MetroStation";

    private final static Logger logger = new Logger();

    private final AtomicBoolean emitTaskInProgress = new AtomicBoolean();

    private volatile ScheduledExecutorService scheduledService;
    private volatile StateListener stateListener;
    private volatile NotificationSettings notificationSettings;
    private volatile LoggingCellListener cellListener;

    public static String getStartAction() {
        return NotificationService.class.getName() + ".start";
    }

    public static String getMockAction() {
        return NotificationService.class.getName() + ".mock";
    }

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
                                    listener.cellInfo(ts, gcl.getCid(), gcl.getLac());
                                } else if (cl instanceof CdmaCellLocation) {
                                    CdmaCellLocation ccl = (CdmaCellLocation) cl;
                                    listener.cellInfo(ts, ccl.getBaseStationId(), -1);
                                }
                            }
                        }
                        break;
                    default:
                        Log.v(LOG_NAME, "Other state");
                        listener.disconnected(ts);
                }
                emitCellDataAsync();
                super.onServiceStateChanged(serviceState);
            } catch (Throwable e) {
                logger.log(e);
            }
        }
    }



    private CellListener buildListeners() {
        Timeout predictionTrigger = new Timeout(scheduledService, 35, TimeUnit.SECONDS);
        Notifier notifier = new NotifierImpl(this, notificationSettings, predictionTrigger);
        long stationTimeout = TimeUnit.SECONDS.toMillis(180);
        long transferTimeout = TimeUnit.SECONDS.toMillis(90);
        return Builder.createListener(new PragueStations(), notifier, stationTimeout, transferTimeout);
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

        if (intent != null && getMockAction().equals(intent.getAction())) {
            playbackMockEvents();
        } else {
            if (stateListener == null) {
                logger.log("Starting...\n");
                scheduledService = Executors.newScheduledThreadPool(3);
                notificationSettings = new NotificationSettings(this);
                notificationSettings.setDefaults();

                int instanceId = new Random().nextInt();
                cellListener = new LoggingCellListener(instanceId, 100, buildListeners());
                stateListener = new StateListener(cellListener);

                setListenerStatus(PhoneStateListener.LISTEN_SERVICE_STATE);
            }
        }
        return START_STICKY;
    }

    private void playbackMockEvents() {
        try {
            cellListener.cellInfo(1, 18807, 34300);
            emitCellDataAsync();
            Thread.sleep(100);
            cellListener.disconnected(2);
            emitCellDataAsync();
            Thread.sleep(100);
            cellListener.cellInfo(3, 18806, 34300);
            emitCellDataAsync();
            Thread.sleep(100);
            cellListener.disconnected(4);
            emitCellDataAsync();
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
    }

    public static String joinStrings(List<String> strings) {
        StringBuffer result = new StringBuffer();
        for (String s : strings) {
            result.append(s).append('\n');
        }
        return result.toString();
    }

    private boolean networkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void emitCellDataAsync() {
        final BoundedChainBuffer<String> cellLogger = cellListener.getCellLogger();

        if (notificationSettings.getCellLogging()
                && cellLogger.size() >= 20 && cellLogger.size() % 5 == 0 // try just once in a time
                && emitTaskInProgress.compareAndSet(false, true)) {

            scheduledService.submit(new Runnable() {
                @Override
                public void run() {
                    final List<String> cells = cellLogger.get();
                    try {
                        if (networkAvailable()) {
                            DataUploader.upload(joinStrings(cells));
                        } else {
                            cellLogger.putBack(cells);
                        }
                    } catch (Exception e) {
                        cellLogger.putBack(cells);
                        logger.log(e);
                    } finally {
                        emitTaskInProgress.set(false);
                    }
                }
            });

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public synchronized void onDestroy() {
        Log.i(LOG_NAME, "Shutting down service...");
        logger.log("Shutting down...\n");
        setListenerStatus(PhoneStateListener.LISTEN_NONE);
        scheduledService.shutdown();
        try {
            if (!scheduledService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                Log.e(LOG_NAME, "Failed to stop scheduled service executor!");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        stateListener = null;
    }

}
