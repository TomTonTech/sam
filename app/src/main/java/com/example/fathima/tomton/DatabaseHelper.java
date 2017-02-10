package com.example.fathima.tomton;
import android.content.Context;
import  android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by fathima on
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME="Attendance.db";
    public static String TABLE_NAME="CSEA";
    public static String COL1="Name";
    public static String COL2="RollNo";


    public DatabaseHelper(Context context)
    {
        super(context,DATABASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
