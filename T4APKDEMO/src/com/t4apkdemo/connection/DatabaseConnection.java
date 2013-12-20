package com.t4apkdemo.connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseConnection extends SQLiteOpenHelper{
	 
    public static String DB_PATH = "/data/data/com.t4apkdemo/databases/";
    public static String DB_NAME = "t4apkdemo_v1.0.db";
    public static String DATABASE_PATH = DB_PATH + DB_NAME;
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
 
    public String errorMsg;
    public static int DBVERSION = 2;
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseConnection(Context context) { 
    	super(context, DB_NAME, null, DBVERSION);
    	this.myContext = context;	
    }
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{ 
    	boolean dbExist = checkDataBase();
    	myDataBase = this.getWritableDatabase();
    	if(!dbExist){    			
    		try {
    			copyDataBase();
    		} catch (Exception e) {
    			e.printStackTrace();
    			throw new Error("Error creating database");
    		}    		
    	}
    	myDataBase.setVersion(DBVERSION);
		myDataBase.close();
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){
    	return new File(DATABASE_PATH).exists();
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    void copyDataBase() throws IOException{    	
 
    	String outFileName = DB_PATH + DB_NAME;
 
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	byte[] buffer = new byte[1024];
    	int length;
    	
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
    	myInput.close();
    	
    	myOutput.flush();
    	myOutput.close();
 
    }
    
   
    void createNewDataBase(){
    	try{
        	String MY_DATABASE_NAME = DB_NAME;	//DB_PATH + 
        	SQLiteDatabase myDB = null;
        	myDB = this.myContext.openOrCreateDatabase(MY_DATABASE_NAME, 0, null);
        	openDataBase();
        	executeUpdate("CREATE TABLE user(id INT(11), name varchar(50))");
        	Log.i("LOG", "DB Created!" );
        }catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    
    public Cursor executeQuery(String sql) throws SQLException{
    	 
    	Cursor c = null;
    	try{
    		this.errorMsg = "";
    		c = myDataBase.rawQuery(sql , null);    	
    	}catch(Exception e){
    		this.errorMsg = "" + e.toString();
    		e.printStackTrace();
    	}

    	return c;
    }
    public boolean executeUpdate(String sql) throws SQLException{
    	boolean qryExecuted = false; 	
    	Log.i("Update qry = ",sql);
    	try{
    		this.errorMsg = "";
    		myDataBase.execSQL(sql);
    		qryExecuted = true;
    	}catch(Exception e){
    		this.errorMsg = "" + e.toString();
    		e.printStackTrace();
    		qryExecuted = false;
    	}
    	return qryExecuted;
    }
    @Override
	public synchronized void close() {
 	    if(myDataBase != null)
		    myDataBase.close();
	    super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v("", "SQLiteDatabase onCreate");
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v("", "SQLiteDatabase onUpgrade");
	}
}
