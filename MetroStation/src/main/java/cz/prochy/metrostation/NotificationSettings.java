package cz.prochy.metrostation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationSettings {

    private static final String PREF_TOAST_ON_ARRIVAL = "toast_on_arrival";
    private static final String PREF_TOAST_ON_LEAVE = "toast_on_leave";
    private static final String PREF_TRAY_NOTIFICATION = "tray_notification";
    private static final String PREF_OVERLAY = "overlay";
    private static final String PREF_STATION_PREDICTIONS = "station_predictions";
    private static final String PREF_CELL_LOGGING = "cell_logging";

    private static final boolean PREF_TOAST_ON_ARRIVAL_DEFAULT = false;
    private static final boolean PREF_TOAST_ON_LEAVE_DEFAULT = false;
    private static final boolean PREF_TRAY_NOTIFICATION_DEFAULT = true;
    private static final boolean PREF_OVERLAY_DEFAULT = true;
    private static final boolean PREF_STATION_PREDICTIONS_DEFAULT = true;
    private static final boolean PREF_CELL_LOGGING_DEFAULT = true;


    private final Context context;

    public NotificationSettings(Context context) {
        this.context = context;
    }

    private SharedPreferences prefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getToastOnArrival() {
        return prefs().getBoolean(PREF_TOAST_ON_ARRIVAL, PREF_TOAST_ON_ARRIVAL_DEFAULT);
    }

    public boolean getToastOnDeparture() {
        return prefs().getBoolean(PREF_TOAST_ON_LEAVE, PREF_TOAST_ON_LEAVE_DEFAULT);
    }

    public boolean getTrayNotification() {
        return prefs().getBoolean(PREF_TRAY_NOTIFICATION, PREF_TRAY_NOTIFICATION_DEFAULT);
    }

    public boolean getPredictions() {
        return prefs().getBoolean(PREF_STATION_PREDICTIONS, PREF_STATION_PREDICTIONS_DEFAULT);
    }

    public boolean getOverlay() {
        return prefs().getBoolean(PREF_OVERLAY, PREF_OVERLAY_DEFAULT);
    }

    public boolean getCellLogging() {
        return prefs().getBoolean(PREF_CELL_LOGGING, PREF_CELL_LOGGING_DEFAULT);
    }

    private void setDefaultBoolean(String key, boolean value) {
        SharedPreferences prefs = prefs();
        if (!prefs.contains(key)) {
            prefs.edit().putBoolean(key, value).commit();
        }
    }

    /**
     * Ensures all preferences are actually stored into the storage to make upgrades easier.
     */
    public void setDefaults() {
        setDefaultBoolean(PREF_TOAST_ON_ARRIVAL, PREF_TOAST_ON_ARRIVAL_DEFAULT);
        setDefaultBoolean(PREF_TOAST_ON_LEAVE, PREF_TOAST_ON_LEAVE_DEFAULT);
        setDefaultBoolean(PREF_TRAY_NOTIFICATION, PREF_TRAY_NOTIFICATION_DEFAULT);
        setDefaultBoolean(PREF_OVERLAY, PREF_OVERLAY_DEFAULT);
        setDefaultBoolean(PREF_STATION_PREDICTIONS, PREF_STATION_PREDICTIONS_DEFAULT);
        setDefaultBoolean(PREF_CELL_LOGGING, PREF_CELL_LOGGING_DEFAULT);
    }

}
