package cz.prochy.metrostation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationSettings {

    private static final String PREF_TOAST_ON_ARRIVAL = "toast_on_arrival";
    private static final String PREF_TOAST_ON_LEAVE = "toast_on_leave";
    private static final String PREF_TRAY_NOTIFICATION = "tray_notification";
    private static final String PREF_STATION_PREDICTIONS = "station_predictions";

    private final Context context;

    public NotificationSettings(Context context) {
        this.context = context;
    }

    private SharedPreferences prefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getToastOnArrival() {
        return prefs().getBoolean(PREF_TOAST_ON_ARRIVAL, true);
    }

    public boolean getToastOnDeparture() {
        return prefs().getBoolean(PREF_TOAST_ON_LEAVE, false);
    }

    public boolean getTrayNotification() {
        return prefs().getBoolean(PREF_TRAY_NOTIFICATION, true);
    }

    public boolean getPredictions() {
        return prefs().getBoolean(PREF_STATION_PREDICTIONS, true);
    }


}
