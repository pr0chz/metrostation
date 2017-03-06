package cz.prochy.metrostation;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import cz.prochy.metrostation.tracking.BoundedChainBuffer;
import cz.prochy.metrostation.tracking.Builder;
import cz.prochy.metrostation.tracking.CellListener;
import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.DataUploader;
import cz.prochy.metrostation.tracking.LoggingCellListener;
import cz.prochy.metrostation.tracking.Notifier;
import cz.prochy.metrostation.tracking.PragueStations;
import cz.prochy.metrostation.tracking.Timeout;
import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ThreadSafe
class EventProcessor {

    private final static String LOG_NAME = "MSEventProcessor";

    private final NotificationService context;
    private final ScheduledExecutorService scheduledExecutor;
    private final StateListener stateListener;
    private final Settings settings;
    private final LoggingCellListener cellListener;

    private final static int UPLOAD_MIN_EVENTS = 20; // threshold for minimum events in buffer
    private final static int UPLOAD_SKIP_EVENTS = 5; // upload every nth event

    private class StateListener extends PhoneStateListener {

        private final CellListener listener;
        private final String uploadUrl;
        private final AtomicBoolean emitTaskInProgress = new AtomicBoolean();

        private StateListener(CellListener listener, String uploadUrl) {
            this.listener = Check.notNull(listener);
            this.uploadUrl = uploadUrl;
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
                        CellLocation cl = context.getLocation();

                        if (cl != null) {
                            if (cl instanceof GsmCellLocation) {
                                GsmCellLocation gcl = (GsmCellLocation) cl;
                                listener.cellInfo(ts, gcl.getCid(), gcl.getLac());
                            } else if (cl instanceof CdmaCellLocation) {
                                CdmaCellLocation ccl = (CdmaCellLocation) cl;
                                listener.cellInfo(ts, ccl.getBaseStationId(), -1);
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
                Log.e(LOG_NAME, "Failed to process state change", e);
            }
        }

        private boolean hasUploadUrl() {
            return uploadUrl != null && !uploadUrl.isEmpty();
        }

        private Runnable uploadTask(final BoundedChainBuffer<String> cellLogger) {
            return new Runnable() {
                @Override
                public void run() {
                    final List<String> cells = cellLogger.get();
                    try {
                        if (context.networkAvailable()) {
                            DataUploader.upload(uploadUrl, joinStrings(cells));
                        } else {
                            cellLogger.putBack(cells);
                        }
                    } catch (Exception e) {
                        cellLogger.putBack(cells);
                        Log.e(LOG_NAME, "Failed to upload data");
                    } finally {
                        emitTaskInProgress.set(false);
                    }
                }
            };
        }

        public void emitCellDataAsync() {
            final BoundedChainBuffer<String> cellLogger = cellListener.getCellLogger();

            if (settings.getCellLoggingEnabled()
                    && hasUploadUrl()
                    && cellLogger.size() >= UPLOAD_MIN_EVENTS && cellLogger.size() % UPLOAD_SKIP_EVENTS == 0
                    && emitTaskInProgress.compareAndSet(false, true)) {

                scheduledExecutor.submit(uploadTask(cellLogger));

            }
        }

    }

    public EventProcessor(NotificationService context) {
        this.context = Check.notNull(context);

        scheduledExecutor = Executors.newScheduledThreadPool(3);
        settings = new Settings(context);
        settings.setDefaults();

        int instanceId = new Random().nextInt();
        cellListener = new LoggingCellListener(instanceId, 100, buildListeners());
        stateListener = new StateListener(cellListener, context.getUploadUrl());

        context.setListenerStatus(stateListener, PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    private CellListener buildListeners() {
        Timeout predictionTrigger = new Timeout(scheduledExecutor, 35, TimeUnit.SECONDS);
        Notifier notifier = new NotifierImpl(context, settings, predictionTrigger);
        long stationTimeout = TimeUnit.SECONDS.toMillis(180);
        long transferTimeout = TimeUnit.SECONDS.toMillis(90);
        return Builder.createListener(PragueStations.newGraph(), notifier, stationTimeout, transferTimeout);
    }

    public void playbackMockEvents() {
        try {
            cellListener.cellInfo(1, 18807, 34300);
            stateListener.emitCellDataAsync();
            Thread.sleep(100);
            cellListener.disconnected(2);
            stateListener.emitCellDataAsync();
            Thread.sleep(100);
            cellListener.cellInfo(3, 18806, 34300);
            stateListener.emitCellDataAsync();
            Thread.sleep(100);
            cellListener.disconnected(4);
            stateListener.emitCellDataAsync();
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
    }

    private static String joinStrings(List<String> strings) {
        StringBuilder result = new StringBuilder();
        for (String s : strings) {
            result.append(s).append('\n');
        }
        return result.toString();
    }


    public void shutdown() {
        context.setListenerStatus(stateListener, PhoneStateListener.LISTEN_NONE);
        scheduledExecutor.shutdown();
        try {
            if (!scheduledExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                Log.e(LOG_NAME, "Failed to stop scheduled service executor!");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
