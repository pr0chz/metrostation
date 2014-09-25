package cz.prochy.metrostation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		final Button button = (Button) findViewById(R.id.startServiceButton);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("activity", "Running service...");
				new ServiceRunner().runService(SettingsActivity.this);
				toast("Service started...");
            }
        });

	}

	private void toast(String message) {
		Toast toast = Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG);
		toast.show();
	}
	
}
