package com.mikedg.glass.control.acra;

import android.content.Context;
import android.util.Log;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Michael on 3/17/14.
 */
public class SdCardReportSender implements ReportSender {
    private final Map<ReportField, String> mMapping = new HashMap<ReportField, String>() ;

    //Mostly from LocalReportSender at http://stackoverflow.com/questions/8970810/acra-how-can-i-write-acra-report-to-file-in-sd-card
    public SdCardReportSender() {
    }
    @Override
    public void send(CrashReportData report) throws ReportSenderException {
        FileOutputStream crashReport = null;

        try {
            crashReport = new FileOutputStream(new File("/sdcard/crashReport"+System.currentTimeMillis()+".txt"));
        } catch (FileNotFoundException e) {
            Log.e("TAG", "IO ERROR", e);
        }
        final Map<String, String> finalReport = remap(report);

        try {
            OutputStreamWriter osw = new OutputStreamWriter(crashReport);

            Set set = finalReport.entrySet();
            Iterator i = set.iterator();

            while (i.hasNext()) {
                Map.Entry<String,String> me = (Map.Entry) i.next();
                osw.write("[" + me.getKey() + "]=" + me.getValue());
            }

            osw.flush();
            osw.close();
        } catch (IOException e) {
            Log.e("TAG", "IO ERROR",e);
        }
    }

//    private static boolean isNull(String aString) {
//        return aString == null || ACRA.NULL_VALUE.equals(aString);
//    }

    private Map<String, String> remap(Map<ReportField, String> report) {

        ReportField[] fields = ACRA.getConfig().customReportContent();
        if (fields.length == 0) {
            fields = new ReportField[]{ReportField.STACK_TRACE, ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.EVENTSLOG, ReportField.LOGCAT, ReportField.REPORT_ID, ReportField.BUILD};
        }

        final Map<String, String> finalReport = new HashMap<String, String>(
                report.size());
        for (ReportField field : fields) {
            if (mMapping == null || mMapping.get(field) == null) {
                finalReport.put(field.toString(), report.get(field));
            } else {
                finalReport.put(mMapping.get(field), report.get(field));
            }
        }
        return finalReport;
    }
}