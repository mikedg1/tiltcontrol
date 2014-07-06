package com.mikedg.android.tiltcontrolcontroller;

import com.mikedg.android.btcomm.Configuration;
import com.squareup.otto.Bus;

/**
 * Created by Michael on 6/22/2014.
 */
public class Application extends android.app.Application {
    private static Bus sBus;

    public static Bus getBus() {
        return sBus;
    }

    public Application() {
        super();

        sBus = new Bus();
        Configuration.bus = sBus;

    }
}
