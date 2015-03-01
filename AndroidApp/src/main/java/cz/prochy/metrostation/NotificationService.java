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
import cz.prochy.metrostation.tracking.Notifier;
import cz.prochy.metrostation.tracking.Timeout;
import cz.prochy.metrostation.tracking.internal.PragueStations;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ThreadSafe
public class NotificationService extends Service {

    private final static String LOG_NAME = "MetroStation";

    private final static Logger logger = new Logger();

    private ScheduledExecutorService scheduledService;
    private StateListener stateListener;

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
                        logger.log(disconnectMessage());
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
                                    logger.log(cellMessage(gcl.getCid(), gcl.getLac()));
                                    listener.cellInfo(ts, gcl.getCid(), gcl.getLac());
                                } else if (cl instanceof CdmaCellLocation) {
                                    CdmaCellLocation ccl = (CdmaCellLocation) cl;
                                    logger.log(cellMessage(ccl.getBaseStationId(), -1));
                                    listener.cellInfo(ts, ccl.getBaseStationId(), -1);
                                }
                            }
                        }
                        break;
                    default:
                        Log.v(LOG_NAME, "Other state");
                        logger.log(disconnectMessage());
                        listener.disconnected(ts);
                }
                super.onServiceStateChanged(serviceState);
            } catch (Throwable e) {
                logger.log(e);
            }
        }
    }

    private String disconnectMessage() {
        return "{\"ts\": " + System.currentTimeMillis() + "}\n";
    }

    private String cellMessage(int cid, int lac) {
        return "{\"ts\": " + System.currentTimeMillis() + ", \"cid\": " + cid + ", \"lac\": " + lac + "}\n";
    }

    private CellListener buildListeners() {
        Timeout predictionTrigger = new Timeout(scheduledService, 35, TimeUnit.SECONDS);
        Notifier notifier = new NotifierImpl(this, new NotificationSettings(this), predictionTrigger);
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
                scheduledService = Executors.newSingleThreadScheduledExecutor();
                stateListener = new StateListener(buildListeners());
                setListenerStatus(PhoneStateListener.LISTEN_SERVICE_STATE);
            }
        }
        return START_STICKY;
    }

    private void playbackMockEvents() {
        CellListener cellListener = buildListeners();
        cellListener.cellInfo(1000, 18807, 34300);
        cellListener.disconnected(2000);
        cellListener.cellInfo(3000, 18806, 34300);
        cellListener.disconnected(4000);
        cellListener.cellInfo(5000, 18805, 34300);
        cellListener.cellInfo(6000, 1, 1);
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
