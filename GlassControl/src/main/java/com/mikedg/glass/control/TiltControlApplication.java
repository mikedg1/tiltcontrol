package com.mikedg.glass.control;

import android.app.Application;
import android.content.Context;
import com.mikedg.glass.control.acra.SdCardReportSender;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Michael on 2/28/14.
 */
@ReportsCrashes(formKey = "")
public class TiltControlApplication extends Application {
    private static TiltControlApplication sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        setupAcra();

        sContext = this;
    }

    private void setupAcra() {
        ACRA.init(this);
        SdCardReportSender yourSender = new SdCardReportSender();
        ACRA.getErrorReporter().setReportSender(yourSender);

//        logcatArguments = { "-t", "100", "-v", "long", "ActivityManager:I", "MyApp:D", "*:S" }

    }

    public static Context getContext() {
        return sContext;
    }
}
