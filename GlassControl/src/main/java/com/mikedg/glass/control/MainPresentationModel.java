package com.mikedg.glass.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Michael on 2/28/14.
 */
public class MainPresentationModel {
    private Context context;

    private boolean mServiceRunning = false;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleStatusbroadcast(intent);
        }
    };

    private BroadcastReceiver mPrefsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handlePrefsBroadcast(intent);
        }
    };

    private OnModelChangedListener mOnModelChangedListener = null;

    public MainPresentationModel() {
        context = TiltControlApplication.getContext();

        context.registerReceiver(mPrefsBroadcastReceiver, Prefs.getPrefsChangedReceiverIntent());

        Intent sticky = context.registerReceiver(mBroadcastReceiver, GlassControlService.getServiceStatusReceiverIntent());
        handleStatusbroadcast(sticky);
    }

    private void handleStatusbroadcast(Intent intent) {
        if (intent != null) {
            //FIXME: we don't care if anything changed, but thats probably smart, right?
            mServiceRunning = intent.getBooleanExtra(GlassControlService.EXTRA_SERVICE_RUNNING_STATUS, false);

            if (mOnModelChangedListener != null) {
                mOnModelChangedListener.onCommandsChanged();
            }
        }
    }

    private void handlePrefsBroadcast(Intent intent) {
        if (intent != null) {
            //FIXME: we don't care if anything changed, but thats probably smart, right?
            if (mOnModelChangedListener != null) {
                mOnModelChangedListener.onPrefsChanged();
            }
        }
    }

    public void tearDown() {
        //remove listeners
        mOnModelChangedListener = null;
        //remove receivers
        context.unregisterReceiver(mBroadcastReceiver);
        context.unregisterReceiver(mPrefsBroadcastReceiver);
    }

    public int getEnablerCommand() {
        return mServiceRunning ? R.string.disable : R.string.enable;
    }
    //Calibrate
    //Get screen timeout
    //get Set always on command

    public void setOnCommandsChangedListener(OnModelChangedListener onModelChangedListener) {
        mOnModelChangedListener = onModelChangedListener;
        mOnModelChangedListener.onCommandsChanged();
    }

    public int getTiltStartSettingText() {
        if (Prefs.getInstance().getTiltStartEnabled()) {
            return R.string.setting_tilt_start_on_text;
        } else {
            return R.string.setting_tilt_start_off_text;
        }
    }

    public static interface OnModelChangedListener {
        public void onCommandsChanged();
        public void onPrefsChanged();
    }
}
