package com.mikedg.glass.control;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Michael on 3/5/14.
 */
public class Prefs {
    private static Prefs sInstance;

    private static final String KEY_TILT_START_ENABLED = "tiltStartEnabled";
    private static final boolean DEFAULT_TILT_START_SEEING = true;
    private static IntentFilter sPrefsChangedIntent;
    private static String ACTION_PREFS_CHANGED_BROADCAST = "prefsChanged";

    private final SharedPreferences mManager;
    private String EXTRA_KEY = "key";
    private String EXTRA_VALUE = "value";

    public static Prefs getInstance() {
        if (sInstance == null) {
            sInstance = new Prefs();
        }
        return sInstance;
    }

    private Prefs() {
        mManager = PreferenceManager.getDefaultSharedPreferences(TiltControlApplication.getContext());
    }

    public void setTiltStartEnabled(boolean value) {
        mManager.edit().putBoolean(KEY_TILT_START_ENABLED, value).commit();
        broadcastChange(KEY_TILT_START_ENABLED, value);
    }

    private void broadcastChange(String key, boolean value) {
        Intent intent = new Intent(ACTION_PREFS_CHANGED_BROADCAST);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_VALUE, value);

        TiltControlApplication.getContext().sendBroadcast(intent);
    }

    public boolean getTiltStartEnabled() {
       return mManager.getBoolean(KEY_TILT_START_ENABLED, DEFAULT_TILT_START_SEEING);
    }

    public static IntentFilter getPrefsChangedReceiverIntent() {
        if (sPrefsChangedIntent == null) {
            sPrefsChangedIntent = new IntentFilter();
            sPrefsChangedIntent.addAction(ACTION_PREFS_CHANGED_BROADCAST);
        }
        return sPrefsChangedIntent;
    }
}
