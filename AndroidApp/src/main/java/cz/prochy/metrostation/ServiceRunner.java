package cz.prochy.metrostation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceRunner extends BroadcastReceiver {

    private Intent getIntent(Context context) {
        return new Intent(context, NotificationService.class);
    }

	void runService(Context context) {
        context.startService(getIntent(context));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			runService(context);
	        Log.v("TEST", "Service loaded at start");		
	    }	
	}

}
