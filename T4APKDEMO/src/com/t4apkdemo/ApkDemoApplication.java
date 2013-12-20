package com.t4apkdemo;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(
		formKey = "", // This is required for backward compatibility but not used
		formUri = "http://www.demo.rifluxyss.com/T4/sample/getting.php"
		)

public class ApkDemoApplication extends Application {
	@Override
	public void onCreate() {
		ACRA.init(this);
		T4Sender yourSender = new T4Sender();
		ACRA.getErrorReporter().setReportSender(yourSender);
		super.onCreate();

		// The following line triggers the initialization of ACRA

	}

}
