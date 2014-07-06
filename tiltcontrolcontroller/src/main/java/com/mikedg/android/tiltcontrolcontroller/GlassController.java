package com.mikedg.android.tiltcontrolcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.google.glass.companion.GlassProtocol;
import com.google.glass.companion.Proto;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Michael on 6/23/2014.
 */
public class GlassController {
    public static final UUID SECURE_UUID = UUID.fromString("F15CC914-E4BC-45CE-9930-CB7695385850");
    private final BluetoothAdapter mBtAdapter;

    //Move subs here
    private OutputStream outputStream;
    private BluetoothSocket socket;

    public BluetoothDevice device; //FIXME: hack

    public GlassController() {

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> devices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            Log.d("BTD", "type:" + device.getType() + " " + device.getName() + "remove dev is not null for self address");

            if (device.getName().equals("Mike DiGiovanni's Glass")) {
                this.device = device;
                try {
                    socket = device.createRfcommSocketToServiceRecord(SECURE_UUID);
                    socket.connect();
                    outputStream = socket.getOutputStream();

//                    Thread t1 = new Thread() {
//                        public void run() {
//                            try {
//                                writeMessages(socket);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                    };
//                    t1.start();

                    Thread t2 = new Thread() {
                        public void run() {
                            try {
                                readMessages(socket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    t2.start();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
//See for better connection detalis https://github.com/wearscript/wearscript-android/blob/master/WearScript/src/main/java/com/dappervision/wearscript/glassbt/GlassDevice.java
//        https://github.com/wearscript/wearscript-android/blob/master/WearScript/src/main/java/com/dappervision/wearscript/glassbt/GlassMessagingUtil.java

    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages(BluetoothSocket socket) throws IOException {
//        Read incoming message as following. (You might want to do it in the dedicated thread.)

        InputStream inputStream = socket.getInputStream();
        while (inputStream.available() > -1) {
            Proto.Envelope envelope = (Proto.Envelope) GlassProtocol.readMessage(new Proto.Envelope(), inputStream);
            if (envelope.screenshot != null) {
                // screenshot response includes screenshot field in envelope
                // …do something…
            }
            Log.d("BTD", envelope.toString());
            //        envelope.motionC2G = GlassMessagingUtil.getSwipeRightEvents() ;
        }
    }


    private void writeMessage(List<Proto.Envelope> events) throws IOException {
        // Envelope is the root of the message hierarchy.
//        Proto.Envelope envelope = CompanionMessagingUtil.newEnvelope();
//// This example is for obtaining screenshot.
//        Proto.ScreenShot screenShot = new Proto.ScreenShot();
//        screenShot.startScreenshotRequestC2G = true;
//        envelope.screenshot = screenShot;
//        GlassProtocol.writeMessage(envelope, outputStream);
//        List<Proto.Envelope> evemts = GlassMessagingUtil.getTapEvents();
//        List<Proto.Envelope> evemts = GlassMessagingUtil.getSwipeLeftEvents();

        for (Proto.Envelope event : events) {
            GlassProtocol.writeMessage(event, outputStream);
        }
//        GlassProtocol.writeMessage(GlassMessagingUtil.createTimelineMessage("hell"), outputStream); //works
        outputStream.flush();
    }

    @Subscribe
    public void leftCommand(ControlEvents.LeftEvent event) {
        Log.d("BTD", "left");
        List<Proto.Envelope> evemts = GlassMessagingUtil.getSwipeLeftEvents();
        try {
            writeMessage(evemts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void rightCommand(ControlEvents.RightEvent event) {
        Log.d("BTD", "right");
        List<Proto.Envelope> evemts = GlassMessagingUtil.getSwipeRightEvents();
        try {
            writeMessage(evemts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void downCommand(ControlEvents.DownEvent event) {
        Log.d("BTD", "down");
        List<Proto.Envelope> evemts = GlassMessagingUtil.getSwipeDownEvents();
        try {
            writeMessage(evemts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void tapCommand(ControlEvents.TapEvent event) {
        Log.d("BTD", "tap");
        List<Proto.Envelope> evemts = GlassMessagingUtil.getTapEvents();
        try {
            writeMessage(evemts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
