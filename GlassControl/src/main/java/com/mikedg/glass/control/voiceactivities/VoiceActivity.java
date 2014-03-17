package com.mikedg.glass.control.voiceactivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.mikedg.glass.control.GlassControlService;
import com.mikedg.glass.control.L;
import com.mikedg.glass.control.MainPresentationModel;
import com.mikedg.glass.control.R;
import com.mikedg.glass.control.toberefactored.CancelableActivity;

/**
 * Created by Michael on 3/16/14.
 */
public abstract class VoiceActivity extends CancelableActivity {
    private BroadcastReceiver mWinkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GlassControlService.ACTION_WINK.equals(intent.getAction())) {
                L.d("We got a wink in the VoiceActivity, so let's try to cancel");
                dismiss();
            }
        }
    };

    private IntentFilter mWinkIntentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mWinkIntentFilter.setPriority(5000);
        mWinkIntentFilter.addAction(GlassControlService.ACTION_WINK);

        setInProgressSubtitle("Swipe down or WINK to cancel");
        setInProgressIcon(null);
        setCompletedTitle("Setting Changed");
        setCompletedSubtitle("");
        setCompletedIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_done_medium));

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mWinkReceiver, mWinkIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mWinkReceiver);
    }
}
