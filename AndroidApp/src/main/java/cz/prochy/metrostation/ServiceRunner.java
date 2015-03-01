package cz.prochy.metrostation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceRunner extends BroadcastReceiver {

    private static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(NotificationService.getStartAction());
        return intent;
    }

    private static Intent getMockIntent(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(NotificationService.getMockAction());
        return intent;
    }

    static void runService(Context context) {
        context.startService(getStartIntent(context));
    }

    static void mockStations(Context context) {
        context.startService(getMockIntent(context));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            runService(context);
            Log.v("TEST", "Service loaded at start");
        }
    }

}
