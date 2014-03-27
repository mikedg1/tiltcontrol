package com.mikedg.glass.control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.mikedg.glass.control.inputhandler.OnStateChangedListener;

/**
 * Created by Michael on 3/17/14.
 */
public class CalibrationActivity extends Activity {
    private SensorManager mSensorManager;
    private Handler mHandler;
    private Thread looperThread;
    private ControlSensorEventListener mSensorEventListener;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           if (GlassControlService.ACTION_WINK.equals(intent.getAction())) {
                L.d("We got a wink");
               onCalibrationConfirmed();
               abortBroadcast();
               //FIXME: add success sound
           }
        }

    };
    private LevelView mMinLevelView;
    private LevelView mMaxLevelView;
    private LevelView mCurrentLevelView;

    @Override
    protected void onResume() {
        super.onResume();
        mSensorEventListener = new ControlSensorEventListener();
        setupSensors(mSensorEventListener);
        registerWink();
    }

    private void registerWink() {
        IntentFilter filter = new IntentFilter(GlassControlService.ACTION_WINK);

        filter.setPriority(3000); //FIXME: do this more intelligently, we just want to make sure we guarantee our Wink's are received to this app

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensors();
        unregisterWink();
    }

    private void unregisterSensors() {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void unregisterWink() {
        unregisterReceiver(mReceiver);
    }

    private void onCalibrationConfirmed() {
        //FIXME: save this
//        pitchMin = Math.min(pitchMin, pitch);
//        pitchMax = Math.max(pitchMax, pitch);
//        rollMin = Math.min(rollMin, roll);
//        rollMax = Math.max(rollMax, pitch);
//        Prefs.getInstance().setCalibrationData(mCalibrationData);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration);
        mCurrentLevelView = (LevelView) findViewById(R.id.currentLevelView);
        mMaxLevelView = (LevelView) findViewById(R.id.maxLevelView);
        mMinLevelView = (LevelView) findViewById(R.id.minLevelView);
    }

    //FIXME: DON'T TOUCH THIS AND TRY TO GENERALIZE IT WITH GLASSCONTROLSERVICES USE
    //Pulled sensoreventlistener to an argument
    private void setupSensors(final SensorEventListener sensorEventListener) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        looperThread = new Thread() {
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                //Added gravity in to support the level, should be able to use this for the accelerometer though, no?
                //Proobably can remove this when level is gone
                mSensorManager.registerListener(sensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                        SensorManager.SENSOR_DELAY_NORMAL, mHandler);

                mSensorManager.registerListener(sensorEventListener,
                        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL, mHandler);

                //Added gyro below
                mSensorManager.registerListener(sensorEventListener,
                        mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_NORMAL, mHandler);

                //FIXME: tried adding magnetic field back to see if t afffects not getting stuff, getrotationmatrix
                //FIXME: I think the below might be necessary, not sure, try removing it and see if we never can get rotaiton matrix again
                mSensorManager.registerListener(sensorEventListener,
                        mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        SensorManager.SENSOR_DELAY_NORMAL, mHandler);
                Looper.loop();
            }
        };
        looperThread.start();
    }


    private class ControlSensorEventListener implements SensorEventListener {
        float[] inR = new float[16];
        float[] outR = new float[16];
        float[] I = new float[16];
        float[] gravity = new float[3];
        float[] geomag = new float[3];
        float[] orientVals = new float[3];

        final float pi = (float) Math.PI;
        final float rad2deg = 180 / pi;

        final float[] pitchRoll = new float[2];

//        private boolean dealerIsSetup; //Did we actually create a new dealer and set it after getting the right amount of samples
//        private int allSensorFlag = 0;
        private double pitchMin = Double.MIN_VALUE;
        private double pitchMax = Double.MIN_VALUE;
        private double rollMin = Double.MAX_VALUE;
        private double rollMax = Double.MIN_VALUE;

//        public void reset() {
//            allSensorFlag = 0;
//            dealerIsSetup = false;
//        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // Gets the value of the sensor that has been changed
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    gravity = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    geomag = event.values.clone();
                    break;
                case Sensor.TYPE_GRAVITY:
                    //Added this in to support the floating overlay
//                    if (mFloatingOverlay instanceof LevelView) {
//                        computeOrientation(event);
//                    }
                    break;
                default: //Don't know what to do?
                    break;
            }

            // If gravity and geomag have values then find rotation matrix
            if (gravity != null && geomag != null) {

                // checks that the rotation matrix is found
                //FIXME: looks like we never get gravity?
                boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
                if (success) { //FIXME: BAD BAD
//                    if(dealerIsSetup) {
                        getPitchRoll(pitchRoll);
                        handleStuff(pitchRoll[0], pitchRoll[1]);
//                    } else if (!dealerIsSetup) {
//                        switch (event.sensor.getType()) {
//                            case Sensor.TYPE_GRAVITY:
//                                allSensorFlag |= 0xF00;
//                                break;
//                            case Sensor.TYPE_MAGNETIC_FIELD:
//                                allSensorFlag |= 0x0F0;
//                                break;
//                            case  Sensor.TYPE_ACCELEROMETER:
//                                allSensorFlag |= 0x00F;
//                                break;
//                        }
//                        if (allSensorFlag == 0xFFF) {
//                            getPitchRoll(pitchRoll);
//                            L.d("Got enough data to setup our start zone for gestures: pitch:" + pitchRoll[0] + " roll:" + pitchRoll[1]);
//                            setupDealer(pitchRoll);
//                            dealerIsSetup = true;
//                        }
                    }

            }
        }
private static final float rad = 0.0174532925f;
    private void handleStuff(float pitch, final float roll) {
        pitchMin = Math.min(pitchMin, pitch);
        pitchMax = Math.max(pitchMax, pitch);
        rollMin = Math.min(rollMin, roll);
        rollMax = Math.max(rollMax, roll);
       // L.d("pm:"+pitchMin + " px:" + pitchMax + " rm:"+rollMin + " rx:" + rollMax);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                L.d("roll:" + roll);
                mCurrentLevelView.setAngle(roll * rad);

                mMaxLevelView.setAngle((float) rollMax * rad);
                mMinLevelView.setAngle((float) rollMin * rad);
            }
        });
    }

    private void getPitchRoll(float[] pitchRoll) {
            // Re-map coordinates so y-axis comes out of camera
            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X,
                    SensorManager.AXIS_Z, outR);

            SensorManager.getOrientation(outR, orientVals);
            //float azimuth = orientVals[0] * rad2deg;
            pitchRoll[0] = orientVals[1] * rad2deg;
            pitchRoll[1] = orientVals[2] * rad2deg;
        }
    }
}
