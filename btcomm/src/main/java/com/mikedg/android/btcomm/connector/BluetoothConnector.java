/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mikedg.android.btcomm.connector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mikedg.android.btcomm.messages.PTGCMessage;

import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public abstract class BluetoothConnector {
    // Debugging
    protected static final String TAG = "BluetoothServerConnector";
    protected static final boolean D = true;

    // PTGCMessage types sent from the BluetoothServerConnector Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothServerConnector Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("d0c722b0-7e15-11e1-b0c4-0800200c9a66");

    // Member fields
    protected final BluetoothAdapter mAdapter;
//    private final Handler mHandler;
    protected ConnectedThread mConnectedThread;
    protected int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    private BluetoothConnector mConnectorHelper;

    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param context The UI Activity Context
     */
    public BluetoothConnector(Context context) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
//        mHandler = handler;
    }


    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    protected synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        //FIXME: Give the new state to the Handler so the UI Activity can update
//        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        cancelThreads();
        // if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        // if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        //FIXME: Send the name of the connected device back to the UI Activity
//        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Any thread that was started should be canceled
     */
    protected abstract void cancelThreads();

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        cancelThreads();
        // if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see com.mikedg.android.btcomm.BluetoothConnector.ConnectedThread#write(byte[])
     */
    private void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }

        // Perform the write unsynchronized
        r.write(out);
    }

//    public void write(Intent intent) {
////        Parcel parcel = Parcel.obtain();
////        intent.writeToParcel(parcel, 0);
////
////        write(parcel.marshall());
//        write(intent.toUri(0).getBytes()); //FIXME: this discards extras... wtf
//    }

    public void write(PTGCMessage message) {
//        Parcel parcel = Parcel.obtain();
//        intent.writeToParcel(parcel, 0);
//
//        write(parcel.marshall());
        try {
            write(message.getTypedBytes());
        } catch (JSONException e) {
            e.printStackTrace();
            //FIXME: alert
        }
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    protected void connectionFailed() {
        setState(STATE_LISTEN); //FIXME: this gets hit even if we are doing an outgoing connection

        //FIXME: Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(TOAST, "Unable to connect device");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    protected void connectionLost() {
        setState(STATE_LISTEN); //FIXME: this gets hit even if we are doing an outgoing connection

        //FIXME: Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(TOAST, "Device connection was lost");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    protected class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final DataInputStream mmInStream;
        private final DataOutputStream mmOutStream;
        private boolean mRestartServiceOnConnectionLost = true;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            DataInputStream tmpIn = null;
            DataOutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = new DataInputStream(socket.getInputStream());
                tmpOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer;
            int size;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    //bytes = mmInStream.read(buffer);
                    size = mmInStream.readInt();
                    if (size > 0) {
                        buffer = new byte[size];
                        int i = 0;
                        while (i != size) {
                            int count = mmInStream.read(buffer, i, size - i);
                            System.out.println("Count: " + count);
                            i += count;
                            System.out.println("Total i: " + i);
                        }
                        //FIXME: new method, read size, read until we fill size... continue
                        //FIXME: above, write change to write size then write actual message... for now keep using intents though, just to see shit get through!!!

                        // Send the obtained bytes to the UI Activity

                        messageReceived(buffer);

                    } else {
                        System.out.println("****Wtf size is:" + size);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }

            // FIXME: Reset connection stages and restart listening mode if
            // connection lost is not user triggered.
            if (mRestartServiceOnConnectionLost) {
                restart();
            }

        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.writeInt(buffer.length);
                mmOutStream.write(buffer);
                mmOutStream.flush();

                //FIXME: Share the sent message back to the UI Activity
//                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            mRestartServiceOnConnectionLost = false;
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * If we need ot restart everything then this should do that
     */
    protected abstract void restart();

    /**
     * @param buffer The bytes received for an entire message
     */
    public abstract void messageReceived(byte[] buffer);

    public PTGCMessage getPtgcFromBytes(BluetoothConnector connector, byte[] buffer) {
        return mConnectorHelper.getPtgcFromBytes(connector, buffer);
    }
}