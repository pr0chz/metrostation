package cz.prochy.metrostation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ServiceRunner extends BroadcastReceiver {

    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    private static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(NotificationService.START_ACTION);
        return intent;
    }

    private static Intent getMockIntent(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(NotificationService.MOCK_ACTION);
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
        if (BOOT_COMPLETED.equals(intent.getAction())) {
            runService(context);
            Log.i("MSServiceRunner", "Service loaded at start");
        }
    }

}
