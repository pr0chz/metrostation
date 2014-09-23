package cz.prochy.metrostation;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends ActionBarActivity {

	private ProcessedCellInfo [] getCellInfo() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		List<ProcessedCellInfo> processedCells = new ArrayList<ProcessedCellInfo>();
		for (CellInfo cell : tm.getAllCellInfo()) {
			Log.v("activity", cell.toString());
			ProcessedCellInfo cellInfo = null;
			if (cell instanceof CellInfoGsm) {
				cellInfo = new ProcessedCellInfo((CellInfoGsm)cell);
			} else if (cell instanceof CellInfoCdma) {
				cellInfo = new ProcessedCellInfo((CellInfoCdma)cell);
			} else if (cell instanceof CellInfoWcdma) {
				cellInfo = new ProcessedCellInfo((CellInfoWcdma)cell);
			} else if (cell instanceof CellInfoLte) {
				cellInfo = new ProcessedCellInfo((CellInfoLte)cell);
			} else {
				Log.e("activity", "Unknown cell info class!");
			}
			if (cellInfo != null && cellInfo.hasValidId()) {
				processedCells.add(cellInfo);
			}
		}
		return processedCells.toArray(new ProcessedCellInfo[0]);
	}
	
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
            }
        });

		final Button clearDbButton = (Button) findViewById(R.id.clearDb);
        clearDbButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DbHelper(SettingsActivity.this).clearDb();
				Log.v("activity", "Deleted data from database...");
				toast("Deleted data from database...");
            }
        });

        
        final Button newPlaceButton = (Button) findViewById(R.id.createPlaceButton);
        newPlaceButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final EditText placeName = (EditText) findViewById(R.id.placeNameEdit);
				String name = placeName.getText().toString();
				if (!"".equals(name)) {
					placeName.clearFocus();
					ProcessedCellInfo [] cells = getCellInfo();
					if (cells.length > 0) {
						try {
							new DbHelper(SettingsActivity.this).addPlace(name, cells);
							toast("New place " + name + " containing "  + cells.length + " cells");
							Log.v("activity", "New place " + name + " containing " 
									+ cells.length + " cells");
						} catch (Exception e) {
							toast("Error!");
							Log.e("activity", e.getMessage());
						}
					} else {
						toast("No cells to add!");
					}
				} else {
					toast("Enter place name!");
				}
            }
        }

        		
        );
		
	}

	private void toast(String message) {
		Toast toast = Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG);
		toast.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
