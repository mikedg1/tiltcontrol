package com.mikedg.glass.control.voiceactivities;

import android.os.Bundle;
import com.mikedg.glass.control.MainPresentationModel;
import com.mikedg.glass.control.Prefs;
import com.mikedg.glass.control.R;

/**
 * Created by Michael on 3/16/14.
 */
public class TiltStartActivity extends VoiceActivity {
    private int mInitialSetting;
    private MainPresentationModel mPresentationModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPresentationModel = new MainPresentationModel();

        mInitialSetting = mPresentationModel.getTiltStartSettingText();

        String changeToSetting = getString(mInitialSetting == R.string.setting_tilt_start_on_text ? R.string.setting_tilt_start_off_text: R.string.setting_tilt_start_on_text);

        setInProgressTitle("Changing to " + changeToSetting);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresentationModel.tearDown();
    }

    @Override
    public void onConfirmed() {
        Prefs.getInstance().setTiltStartEnabled(!Prefs.getInstance().getTiltStartEnabled());
    }

    @Override
    public void onDismissed() {
        //Nothing
    }
}
