package com.mikedg.glass.control.inputhandler;

import com.mikedg.android.btcomm.connector.BluetoothServerConnector;
import com.mikedg.android.btcomm.messages.CommandMessage;
import com.mikedg.android.btcomm.messages.PTGCMessage;
import com.mikedg.glass.control.L;
import com.mikedg.glass.control.TiltControlApplication;

/**
 * Created by Michael on 7/1/2014.
 */
public class MyGlassLoopbackInputHandler extends BaseInputHandler {
    BluetoothServerConnector connector;
    public MyGlassLoopbackInputHandler() {
        L.d("Created MyGlassLoopbackInputHandler");
        onStateChanged(OnStateChangedListener.State.NOT_READY);
    }

    @Override
    public void select() {
        sendCommend(CommandMessage.Command.TAP);
    }

    @Override
    public void left() {
        sendCommend(CommandMessage.Command.LEFT);

    }

    @Override
    public void right() {
        sendCommend(CommandMessage.Command.RIGHT);

    }

    @Override
    public void back() {
        sendCommend(CommandMessage.Command.DOWN);

    }

    @Override
    public void start() {
//        we connect to the phone, which tells the myglass service to connect to us...
//        still runs into problems where something isnt running so might as well just sit on here
        connector = new BluetoothServerConnector();

        connector.start();
        onStateChanged(OnStateChangedListener.State.READY); //FIXME: not actually ready but should be good

    }

    @Override
    public void stop() {
        connector.stop();
    }

    private void sendCommend(CommandMessage.Command command) {
        PTGCMessage message = new CommandMessage(command);
        connector.write(message);
    }
}
