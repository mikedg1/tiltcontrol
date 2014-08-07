package com.mikedg.android.tiltcontrolcontroller.events;

/**
 * Created by Michael on 8/6/2014.
 */
public class StatusMessageEvent {
    private final String mMessage;

    public StatusMessageEvent(String message) {
        mMessage = message;

    }

    public String getMessage() {
        return mMessage;
    }
}
