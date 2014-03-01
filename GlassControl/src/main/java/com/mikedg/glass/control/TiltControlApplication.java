package com.mikedg.glass.control;

import android.app.Application;
import android.content.Context;

/**
 * Created by Michael on 2/28/14.
 */
public class TiltControlApplication extends Application {
    private static TiltControlApplication sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}
