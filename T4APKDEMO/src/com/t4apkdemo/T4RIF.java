package com.t4apkdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.acra.ACRA;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.t4apkdemo.connection.DatabaseConnection;
import com.t4apkdemo.utils.Utilities;

public class T4RIF extends ListActivity {

	DatabaseConnection connection;
	String[] ListItems = new String[] { "Copy Database to SDCARD", 			 
			"Backup an Android application",
			"Contacts with Only Email",};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		connection = new DatabaseConnection(this);

		try {
			connection.createDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("", "Unable to create Database....");
			ACRA.getErrorReporter().handleException(e);
			e.printStackTrace();
		}

		if (connection != null) {
			connection.close();			
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, ListItems);
		setListAdapter(adapter);

		/*Log.e("", "Calling Call list....");
		String[] projection = { CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE };
		String where = CallLog.Calls.IS_READ + "=0";
		Cursor c = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, where, null, null);
		c.moveToFirst();
		Log.e("CALL", ""+c.getCount());
		if(c.getCount() > 0 ){
			Log.e("CACHED_NAME", ""+c.getString(0));
			Log.e("CACHED_NUMBER_LABEL", ""+c.getString(1));
			Log.e("TYPE", ""+c.getString(2));
		}
		c.close();*/
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		switch (position) {
		case 0:
			copyDBtoSD();
			break;
		case 1:
			Intent apps_intent = new Intent(
					"android.intent.action.PICK_ACTIVITY");
			Intent apps_main = new Intent("android.intent.action.MAIN", null);
			apps_main.addCategory("android.intent.category.LAUNCHER");
			apps_intent.putExtra("android.intent.extra.INTENT", apps_main);
			startActivityForResult(apps_intent, 1);
			break;

		default:
			break;
		}
	}

	private void copyDBtoSD(){
		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if(isSDPresent){
			if (new File(DatabaseConnection.DATABASE_PATH).exists()){
				File to_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RIF_backup/");
				if (!to_dir.exists()) 
					to_dir.mkdirs();

				if (Utilities.backupDatabase(new File(to_dir, DatabaseConnection.DB_NAME)))
					Toast.makeText(getApplicationContext(), 
							String.format(getString(R.string.db_copied), to_dir.getAbsolutePath()), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext(), 
							getString(R.string.problem_copied_db), Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getApplicationContext(), getString(R.string.no_db), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.no_sd_card), Toast.LENGTH_SHORT).show();
		}
	}

	private void backupApp(String packagename){
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
		for (Object object : pkgAppsList) {
			ResolveInfo info = (ResolveInfo) object;
			if (info.activityInfo.applicationInfo.packageName.equals(packagename)){
				File file =new File(info.activityInfo.applicationInfo.publicSourceDir);
				File backup_appFile = new File(Environment.getExternalStorageDirectory() + "/RIF_backup/", info.activityInfo.applicationInfo.packageName +".apk");
				if (!new File(Environment.getExternalStorageDirectory() + "/RIF_backup/").exists())
					new File(Environment.getExternalStorageDirectory() + "/RIF_backup/").mkdirs();
				
				Log.v("", "package name"+info.activityInfo.applicationInfo.packageName);
				try {
					InputStream in = new FileInputStream(file);
					OutputStream out = new FileOutputStream(backup_appFile);

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.v("", "!!!!!!!!!!!!!! Completed app backup!!!!!!!!!!!!!!");				
				Toast.makeText(getApplicationContext(), 
						String.format(getString(R.string.app_copied), 
								"\t"+info.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString() +"\n", 
								backup_appFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
			switch (requestCode) {
			case 1:
				try {
					String packagename = String.valueOf(data.getComponent()
							.getPackageName());
					backupApp(packagename);
					return;
				} catch (Exception e) {
					// Toast.makeText(getApplicationContext(), "Nothing Selected",
					// Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.t4_ri, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (connection != null) 
			connection = null;		
	}
}