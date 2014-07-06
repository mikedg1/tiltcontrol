package com.mikedg.android.tiltcontrolcontroller;

import com.mikedg.android.btcomm.messages.CommandMessage;
import com.mikedg.android.btcomm.messages.PTGCMessage;
import com.squareup.otto.Subscribe;

/**
 * Created by Michael on 6/30/2014.
 */
public class CommandReceiver {
    @Subscribe
    public void gotCommand(PTGCMessage event) {
        //This gets hit still!
    }

    @Subscribe
    public void gotSpecificCommand(CommandMessage event) {
        //Translate to other sender
        switch (event.getCommand()) {
            case DOWN:
                Application.getBus().post(new ControlEvents.DownEvent());
                break;
            case LEFT:
                Application.getBus().post(new ControlEvents.LeftEvent());
                break;
            case RIGHT:
                Application.getBus().post(new ControlEvents.RightEvent());
                break;
            case TAP:
                Application.getBus().post(new ControlEvents.TapEvent());
                break;
        }
    }
}
