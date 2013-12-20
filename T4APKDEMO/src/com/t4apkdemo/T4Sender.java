package com.t4apkdemo;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import android.util.Log;

public class T4Sender implements ReportSender {

    public T4Sender(){
        // initialize your sender with needed parameters
    }

    @Override
    public void send(CrashReportData report) throws ReportSenderException {
        // Iterate over the CrashReportData instance and do whatever
        // you need with each pair of ReportField key / String value
    	Log.v("", "Manual Report Sending activated.!");
    }
}