package cz.prochy.metrostation;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class SettingsActivity extends Activity {

    private final Deque<Integer> keyBuffer = new ArrayDeque<>(4);

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notification_settings);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.notification_settings, false);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        ServiceRunner.runService(SettingsActivity.this);
    }

    private void addKeyToBuffer(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (keyBuffer.size() == 4) {
                keyBuffer.removeFirst();
            }
            keyBuffer.addLast(keyCode);
        } else {
            keyBuffer.clear();
        }
    }

    private boolean secretMatches() {
        if (keyBuffer.size() == 4) {
            Iterator<Integer> it = keyBuffer.iterator();
            return it.next() == KeyEvent.KEYCODE_VOLUME_UP
                    && it.next() == KeyEvent.KEYCODE_VOLUME_DOWN
                    && it.next() == KeyEvent.KEYCODE_VOLUME_UP
                    && it.next() == KeyEvent.KEYCODE_VOLUME_DOWN;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        addKeyToBuffer(keyCode);
        if (secretMatches()) {
            ServiceRunner.mockStations(SettingsActivity.this);
            keyBuffer.clear();
        }
        return super.onKeyDown(keyCode, event);
    }
}
