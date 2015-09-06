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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ThreadSafe
public class NotificationService extends Service {

    private final static String LOG_NAME = "MetroStation";

    private final static Logger logger = new Logger();

    private volatile ScheduledExecutorService scheduledService;
    private volatile StateListener stateListener;
    private volatile BoundedStringBuffer cellLogger;

    private volatile int instanceId;

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
                        cellLogger.addLine(disconnectMessage());
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
                                    cellLogger.addLine(cellMessage(gcl.getCid(), gcl.getLac()));
                                    listener.cellInfo(ts, gcl.getCid(), gcl.getLac());
                                } else if (cl instanceof CdmaCellLocation) {
                                    CdmaCellLocation ccl = (CdmaCellLocation) cl;
                                    cellLogger.addLine(cellMessage(ccl.getBaseStationId(), -1));
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
        return "{\"id\": " + instanceId +", \"ts\": " + System.currentTimeMillis() + "}";
    }

    private String cellMessage(int cid, int lac) {
        return "{\"id\": " + instanceId +", \"ts\": " + System.currentTimeMillis() + ", \"cid\": " + cid + ", \"lac\": " + lac + "}";
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
                scheduledService = Executors.newScheduledThreadPool(3);
                scheduledService.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        emitCellData();
                    }
                }, 5, 5, TimeUnit.HOURS);
                stateListener = new StateListener(buildListeners());
                cellLogger = new BoundedStringBuffer(100);
                setListenerStatus(PhoneStateListener.LISTEN_SERVICE_STATE);
                instanceId = new Random().nextInt();
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
        cellLogger.addLine(cellMessage(1000, 2000));
        cellLogger.addLine(disconnectMessage());
        cellLogger.addLine(cellMessage(1000, 2000));
        cellLogger.addLine(disconnectMessage());
        cellLogger.addLine(cellMessage(1000, 2000));
        cellLogger.addLine(disconnectMessage());
        scheduledService.submit(new Runnable() {
            @Override
            public void run() {
                emitCellData();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void emitCellData() {

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (cellLogger.getSize() > 5 && networkInfo != null && networkInfo.isConnected()) {
            try {
                byte [] content = cellLogger.getContent().getBytes(Charset.forName("utf-8"));
                URL url = new URL("http://52.25.112.0:48989/store");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty( "Content-Type", "text/plain");
                conn.setRequestProperty( "charset", "utf-8");
                conn.setRequestProperty( "Content-Length", Integer.toString(content.length));
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(false);
                conn.connect();
                OutputStream stream = null;
                try {
                    stream = conn.getOutputStream();
                    stream.write(content);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
                conn.getResponseCode();
                conn.disconnect();
                cellLogger.clear();
            } catch (Exception e) {
            }
        }
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
