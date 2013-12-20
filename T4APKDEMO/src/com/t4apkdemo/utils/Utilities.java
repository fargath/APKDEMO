package com.t4apkdemo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

import com.t4apkdemo.connection.DatabaseConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

public class Utilities {
	
	public static boolean haveInternet(Activity thisActivity) {
		NetworkInfo info = ((ConnectivityManager) thisActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			return true;
		}
		return true;
	}
	
	public static String getDeviceID(Context thisActivity) {
		TelephonyManager tManager = (TelephonyManager) thisActivity
				.getSystemService(Context.TELEPHONY_SERVICE);
		String id = tManager.getDeviceId();
		if (id == null || (id != null && id.equals("")) && id.equals("9774d56d682e549c"))
			id = getUniqueID(thisActivity, tManager);
				
		return id;
	}	
    
	private static String getUniqueID(Context thisActivity, TelephonyManager tm) {		

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						thisActivity.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		return deviceId;
	}
	
	public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	
	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	          "\\@" +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	          "(" +
	          "\\." +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	          ")+"
	 );
	
	public static Message getMessage(int handle_enum) {
		Message msg = new Message();
		msg.what = handle_enum;				
		return msg;
	}
	
	public static String getNodeValue(Document doc, String node) {
		String node_value = null;
		if (doc.getElementsByTagName(node).item(0) != null)
			node_value = doc.getElementsByTagName(node).item(0).getChildNodes().item(0).getNodeValue();
		
		return node_value;
	}
	
	public static void showActivity(Activity sourceScreen, Class<?> cls) {
		Intent i = new Intent(sourceScreen, cls);
		sourceScreen.startActivity(i);
		sourceScreen.finish();
	}
	
	public static void showNavigationAlert(final Activity thisActivity,
			String alertMsg, final Class<?> cls) {
		try {
			new AlertDialog.Builder(thisActivity)
					.setTitle("The RenoKing")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									showActivity(thisActivity, cls);
								}
							}).setMessage("" + alertMsg).create().show();
			return;
		} catch (Exception e) {
			Log.e("Alert error", "Alert Err: " + e.toString());
		}
	}
	
	public static void showAlert(final Activity thisActivity, String alertMsg) {
		try {
			new AlertDialog.Builder(thisActivity)
					.setTitle("The RenoKing")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							}).setMessage("" + alertMsg).create().show();
			return;
		} catch (Exception e) {
			Log.e("Alert error", "Alert Err: " + e.toString());
		}
	}
	
	@SuppressLint("NewApi")
	public static boolean getSmallScreen(Activity act){
		Display display = act.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		int width = 0;
		int height = 0;
		if (android.os.Build.VERSION.SDK_INT >= 13){
			display.getSize(size);
			width = size.x;
			height = size.y;
		}else{
			width = display.getWidth(); 
			height = display.getHeight();
		}
//		DisplayMetrics displaymetrics = new DisplayMetrics(); 
//		act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//		height = displaymetrics.heightPixels;
//		width = displaymetrics.widthPixels; 
		if (width <= 240 && height <= 320) 
		{ 
			return true;
		} else{
			return false;
		}
	}
	
	public static boolean backupDatabase(File to) {
	    File from = new File(DatabaseConnection.DATABASE_PATH);	    
	    try {
	        copyFile(from, to);
	        return true;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    return false;
	}
	
	public static void copyFile(File src, File dst) throws IOException {
	    FileInputStream in = new FileInputStream(src);
	    FileOutputStream out = new FileOutputStream(dst);
	    FileChannel fromChannel = null, toChannel = null;
	    try {
	        fromChannel = in.getChannel();
	        toChannel = out.getChannel();
	        fromChannel.transferTo(0, fromChannel.size(), toChannel); 
	    } finally {
	        if (fromChannel != null) 
	            fromChannel.close();
	        if (toChannel != null) 
	            toChannel.close();
	    }
	}

}
