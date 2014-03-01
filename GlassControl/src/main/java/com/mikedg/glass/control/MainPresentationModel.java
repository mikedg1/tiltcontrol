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

    private OnCommandsChangedListener mOnCommandsChangedListener = null;

    public MainPresentationModel() {
        context = TiltControlApplication.getContext();

        Intent sticky = context.registerReceiver(mBroadcastReceiver, GlassControlService.getServiceStatusReceiverIntent());
        handleStatusbroadcast(sticky);
    }

    private void handleStatusbroadcast(Intent intent) {
        if (intent != null) {
            //FIXME: we don't care if anything changed, but thats probably smart, right?
            mServiceRunning = intent.getBooleanExtra(GlassControlService.EXTRA_SERVICE_RUNNING_STATUS, false);

            if (mOnCommandsChangedListener != null) {
                mOnCommandsChangedListener.onCommandsChanged();
            }
        }
    }

    public void tearDown() {
        //remove listeners
        mOnCommandsChangedListener = null;
        //remove receivers
        context.unregisterReceiver(mBroadcastReceiver);
    }

    public int getEnablerCommand() {
        return mServiceRunning ? R.string.disable : R.string.enable;
    }
    //Calibrate
    //Get screen timeout
    //get Set always on command

    public void setOnCommandsChangedListener(OnCommandsChangedListener onCommandsChangedListener) {
        mOnCommandsChangedListener = onCommandsChangedListener;
        mOnCommandsChangedListener.onCommandsChanged();
    }

    public static interface OnCommandsChangedListener {
        public void onCommandsChanged();
    }
}
