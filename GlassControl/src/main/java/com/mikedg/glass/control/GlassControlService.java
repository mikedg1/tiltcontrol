/*
Copyright 2013 Michael DiGiovanni glass@mikedg.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.mikedg.glass.control;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.WindowManager;
import com.mikedg.glass.control.inputhandler.AdbTcpInputHandler;
import com.mikedg.glass.control.inputhandler.InputHandler;
import com.mikedg.glass.control.inputhandler.OnStateChangedListener;
import com.mikedg.glass.control.inputhandler.TestVoiceInputHandler;

public class GlassControlService extends Service {
    public static final boolean SHOULD_SIM_KEYS = true;
    public static final boolean SHOULD_SHOW_OVERLAY = false;
    public static final boolean SHOULD_SENSOR = true;

    public static final String ACTION_WINK = "com.google.glass.action.EYE_GESTURE";
    public static final String EXTRA_SERVICE_RUNNING_STATUS = "EXTRA_SERVICE_RUNNING_STATUS";
    private static String ACTION_STATUS_BROADCAST = GlassControlService.class.getName() + ".ACTION_STATUS_BROADCAST";

    private Dealer mDealer; //Deals out commands as they come in
    private LevelView mFloatingOverlay;
    private Handler mHandler;
    private Thread looperThread;
    private Handler mainHandler;
    private InputHandler mInputHandler;
    private PowerManager mPowerManager;
    private boolean mTiltControlListening;
    private OnStateChangedListener.State mInputHandlerState = OnStateChangedListener.State.NOT_READY;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                L.d("In Tilt Control's receiver");

                if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    //Disable sensors no matter what
                    L.d("The screen just went off, so let's disable listening for sensors");
                    disableSensors();
                } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                    //Unless we woke up with a wink, we don't want to enable anything
                    L.d("The screen just came on, but we do things off other triggers");
                } else if (ACTION_WINK.equals(intent.getAction())) {
                    L.d("We got a wink");
                    if (mInputHandlerState == OnStateChangedListener.State.READY) {
                        L.d("And our input handler state is ready, so let's check some more things");
                        if (mPowerManager.isScreenOn()) { //Could be that this is a race condition? and mTiltControlListening is false
                            L.d("The screen is on");
                            if (mTiltControlListening) {
                                L.d("We are listening for controls, so we want to ack and select now");
                                if (SHOULD_SIM_KEYS) {
                                    mInputHandler.select();
                                }
                                ack(); //Works just harder to hear
                                abortBroadcast();
                            } else {
                                //Let the standard handler grab the wink
                                L.d("But we are not listening for controls");
                                return;
                            }
                        } else {
                            L.d("The screen isn't on yet, so this week should be turning the screen on, so let's enable the sensors and abort the wink broadcast");
                            //L.d("Since that's the case, we want to enable the sensors if everything is connected");
                            //If via wink, enable sensors
//                            if (mInputHandler.isConnected) {
//                                L.d("Input handler is connected so let's enable sensors and abort the wink broadcast");
                            enableSensors();
                            abortBroadcast();
//                            } else {
//                                L.d("Input handler is not connected");
//                            }
                        }
                    } else {
                        L.d("But our input handler is not ready:" + mInputHandlerState);
                    }
                }
            }

        private void enableSensors() {
            L.d("Trying to register sensor listeners");
            if (!mTiltControlListening) {
                mTiltControlListening = true;
                setupSensors();
            } else {
                L.d("but we are already listening");
            }
        }

        private void disableSensors() {
            if (mTiltControlListening) {
                mTiltControlListening = false;

                L.d("Unregistering sensor listeners");
                mSM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                mSM.unregisterListener(mSEL);
            }
        }
    };

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //FIXME: we will actually want this to restart automatically once we stop using hacks to determine whether we connected or not
        return Service.START_NOT_STICKY; //When this crashes make sure we don't restart it, so we can actually connect after using our crash hack
    }

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastStarted();

        mainHandler = new Handler(getMainLooper());
        mPowerManager = (PowerManager)getSystemService(POWER_SERVICE);
        if (SHOULD_SIM_KEYS) {
            setupInputHandler();
        }
        setupDealer();
        if (SHOULD_SENSOR) {
            //setupSensors(); //FIXME: removed so we don't initially tell the sensors to start sensing when we run it for the first time
        }
        if (SHOULD_SHOW_OVERLAY) {
            //For debug purposes, shows a line on the screen that measures tilt continuously
            setupOverlay();
        }

        setupReceivers();
    }

    public void broadcastStarted() {
        Intent intent = new Intent(ACTION_STATUS_BROADCAST);
        intent.putExtra(EXTRA_SERVICE_RUNNING_STATUS, true);
        sendStickyBroadcast(intent);
    }

    private void setupInputHandler() {
//        mInputHandler = new TestVoiceInputHandler();
        mInputHandler = new AdbTcpInputHandler();
        mInputHandler.setOnStateChangedListener(new OnStateChangedListener() {
            @Override
            public void onStateChanged(State state) {
                switch (state) {
                    case CATASTROPHIC_FAILURE:
                        L.d("Uh oh, catastrophic failure, turning off service");
                        //System.exit(0); //FIXME: for now it's better than crashing right :)
                        turnOff(GlassControlService.this);
                        break;
                    case NOT_READY:
                        if (mInputHandlerState ==  State.READY) {
                            L.d("input handler went from READY to NOT_READY, that's only expected when we disable the service");
                        }
                        break;
                    case READY:
                        L.d("state changed to ready");
                        break;
                }
                mInputHandlerState = state;
            }
        });
        mInputHandler.start();
    }

    /*
    For debugging purposes, we've repurposed the GDK level sample from https://github.com/googleglass/gdk-level-sample
     */
    private void setupOverlay() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = 0;
        params.y = 0;
        mFloatingOverlay = new LevelView(this);

        windowManager.addView(mFloatingOverlay, params);
    }

    private void setupDealer() {
        mDealer = new Dealer(0,0); //FIXME: should pick something based off when we start, but for now this is fine
    }

    private SensorManager mSM;
    private mSensorEventListener mSEL;

    private void setupSensors() {
        mSM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSEL = new mSensorEventListener();

        looperThread = new Thread() {
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                //Added gravity in to support the level, should be able to use this for the accelerometer though, no?
                //Proobably can remove this when level is gone
                mSM.registerListener(mSEL, mSM.getDefaultSensor(Sensor.TYPE_GRAVITY),
                        SensorManager.SENSOR_DELAY_NORMAL, mHandler);

                mSM.registerListener(mSEL,
                        mSM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL, mHandler);

                //Added gyro below
                mSM.registerListener(mSEL,
                        mSM.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_NORMAL, mHandler);

                //FIXME: tried adding magnetic field back to see if t afffects not getting stuff, getrotationmatrix
                //FIXME: I think the below might be necessary, not sure, try removing it and see if we never can get rotaiton matrix again
                mSM.registerListener(mSEL,
                        mSM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        SensorManager.SENSOR_DELAY_NORMAL, mHandler);
                Looper.loop();
            }
        };
        looperThread.start();
    }

    private void setupReceivers() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_WINK);

        filter.setPriority(3000); //FIXME: do this more intelligently, we just want to make sure we guarantee our Wink's are received to this app

        registerReceiver(mReceiver, filter);
    }

    public void ack() {
        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
    }

    private static final Intent getServiceIntent(Context context) {
        return new Intent(context, GlassControlService.class);

    }

    public static void launch(Context context) {
        context.startService(getServiceIntent(context));
    }

    public static void turnOff(Context context) {
        context.stopService(getServiceIntent(context));
    }

    @Override
    public void onDestroy() {
        broadcastStopped();
        mInputHandler.stop();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void broadcastStopped() {
        Intent intent = new Intent(ACTION_STATUS_BROADCAST);
        intent.putExtra(EXTRA_SERVICE_RUNNING_STATUS, false);
        sendStickyBroadcast(intent); //FIXME: should not be static and should be using this context
    }

    private static IntentFilter sServiceStatusIntent = null;

    public static IntentFilter getServiceStatusReceiverIntent() {
        if (sServiceStatusIntent == null) {
            sServiceStatusIntent = new IntentFilter();
            sServiceStatusIntent.addAction(ACTION_STATUS_BROADCAST);
        }
        return sServiceStatusIntent;
    }

    private class mSensorEventListener implements SensorEventListener {
        float[] inR = new float[16];
        float[] outR = new float[16];
        float[] I = new float[16];
        float[] gravity = new float[3];
        float[] geomag = new float[3];
        float[] orientVals = new float[3];

        final float pi = (float) Math.PI;
        final float rad2deg = 180 / pi;

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            // If the sensor data is unreliable return
//            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
//                switch (event.sensor.getType()) { //Just to get a handle on unreliable data, why?
//                    case Sensor.TYPE_ACCELEROMETER:
//                        gravity = event.values.clone();
//                        break;
//                    case Sensor.TYPE_MAGNETIC_FIELD:
//                        geomag = event.values.clone();
//                        break;
//                    default: //Don't know what to do?
//                        break;
//                }
//                return;
//            }

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
                    if (mFloatingOverlay instanceof LevelView) {
                        computeOrientation(event);
                    }
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
                    // Re-map coordinates so y-axis comes out of camera
                    SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X,
                            SensorManager.AXIS_Z, outR);

                    SensorManager.getOrientation(outR, orientVals);
                    //float azimuth = orientVals[0] * rad2deg;
                    float pitch = orientVals[1] * rad2deg;
                    float roll = orientVals[2] * rad2deg;

                    mDealer.handleStuff(pitch, roll);
                }
            }
        }

    }


    /**
     * Compute the orientation angle.
     */
    private void computeOrientation(SensorEvent event) {
        final float angle = (float) -Math.atan(event.values[0]
                / Math.sqrt(event.values[1] * event.values[1] + event.values[2] * event.values[2]));
        //FIXME: is all this slow?

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mFloatingOverlay.setAngle(-angle); //Changed to minus so it basically turns with head
            }
        });
    }

    private class AxisDetails {
        private final Trigger lowTrigger;
        private final Trigger highTrigger;
        private final float hysterisisLow;
        private final float hysterisisHigh;
        private final float neutralHigh;
        private final float neutralLow;
        private final float variance;

        float lastCount = 0;

        public AxisDetails(float neutralHigh, float neutralLow, float variance, float base, Trigger lowTrigger, Trigger highTrigger, float hysterisisLow, float hysterisisHigh) {
            this.neutralHigh = neutralHigh;
            this.neutralLow = neutralLow;
            this.hysterisisLow = hysterisisLow;
            this.hysterisisHigh = hysterisisHigh;
            this.variance = variance;
            this.lowTrigger = lowTrigger;
            this.highTrigger = highTrigger;
        }
    }

    private interface Trigger {
        public void trigger();
    }

    private class Dealer {

        private static final float ROLL_NEUTRAL_HIGH = 15; //We hit outside the neutral then variance is each slot
        private static final float ROLL_NEUTRAL_LOW = -15;
        private static final float ROLL_VARIANCE = 15; //Distance between each slot

        private static final float PITCH_NEUTRAL_HIGH = 10; //Nothing going up
        private static final float PITCH_NEUTRAL_LOW = -1000; //FIXME: not sure positive is down...
        private static final float PITCH_VARIANCE = 30; //Feels good at 30
        //FIXME: need a max count in a direction
        AxisDetails pitchAxisDetails;
        AxisDetails rollAxisDetails;

        public Dealer(float initialPitch, float initialRoll) {
            //FIXME: should we always reset to 0, or what?
            //FIXME: need to normalize any values around 0!!! or the handleMovement breaks
            pitchAxisDetails = new AxisDetails(PITCH_NEUTRAL_HIGH, PITCH_NEUTRAL_LOW, PITCH_VARIANCE, initialPitch, new Trigger() {
                @Override
                public void trigger() {

                }
            }, new Trigger() {
                @Override
                public void trigger() {
                    if (SHOULD_SIM_KEYS) {
                        mInputHandler.back();
                    }
                }
            }, 0, 8);

            rollAxisDetails = new AxisDetails(ROLL_NEUTRAL_HIGH, ROLL_NEUTRAL_LOW, ROLL_VARIANCE, initialRoll, new Trigger() {
                @Override
                public void trigger() {
                    if (SHOULD_SIM_KEYS) {
                        mInputHandler.left();
                    }
                }
            }, new Trigger() {
                @Override
                public void trigger() {
                    if (SHOULD_SIM_KEYS) {
                        mInputHandler.right();
                    }
                }
            }, (float)((ROLL_NEUTRAL_HIGH - ROLL_NEUTRAL_LOW) * .33), (float)((ROLL_NEUTRAL_HIGH - ROLL_NEUTRAL_LOW) * .33));
        }

        public void handleStuff(float pitch, float roll) {
            handleMovement(pitch, pitchAxisDetails);
            handleMovement(roll, rollAxisDetails);
        }

        //FIXME: Tapping Glass causes issues, need to be safer
        private void handleMovement(float detail, AxisDetails details) {
            //
            //Shift this so we can abs these! and they are 0 based!

            //This is going in one direction, the positive direction
            if (detail < details.neutralLow) {
                double magicNumber = Math.floor((detail - (details.neutralLow)) / details.variance);

                if (magicNumber < details.lastCount) {
                    L.d("we triggered the next step in the negative direction" + detail);
                    details.lastCount = (float) magicNumber;
                    if (mFloatingOverlay instanceof LevelView) {
                        mFloatingOverlay.setColor((int) Math.abs(magicNumber)); //FIXME: color should only be for head tilt!
                    }

                    details.lowTrigger.trigger();
                    ack();
                }
            } else if (detail > details.neutralHigh) {
                double magicNumber = Math.ceil((detail - (details.neutralHigh)) / details.variance);

                if (magicNumber > details.lastCount) {
                    L.d("we triggered the next step in the positive direction"  + detail);
                    details.lastCount = (float) magicNumber;

                    if (mFloatingOverlay instanceof LevelView) {
                        mFloatingOverlay.setColor((int) Math.abs(magicNumber)); //FIXME: color should only be for head tilt!
                    }
                    details.highTrigger.trigger();

                    ack();
                }
            } else {
                //Maybe do a quarter? of neutral?

                //if (magicNumber == 0) { //Implicit now I think
                //Only reset when 0 so we have to recenter
                //FIXME: hysterisis here doesn't care about the range so it's broken on things that don't center around 0
                if (detail > (details.neutralLow + details.hysterisisLow) && detail < (details.neutralHigh - details.hysterisisHigh)) { //FIXME: move hysterisis to a constant
                    if (details.lastCount != 0) {
                        L.d("reset lastcount");
                    }
                    if (mFloatingOverlay instanceof LevelView) {
                        mFloatingOverlay.setColor(0); //FIXME: color should only be for head tilt!
                    }
                    details.lastCount = 0;
                }
            }
        }
    }
}
