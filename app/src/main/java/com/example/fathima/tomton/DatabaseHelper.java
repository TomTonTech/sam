package com.example.fathima.tomton;
import android.content.ContentValues;
import android.content.Context;
import  android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by general on 10/8/2016.
 * this is to add data to the sqlite database in users phone
 */
class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lbs.db";
    private static final String STUDENT_TABLE = "student_details";
    private static final String SUBJECT_TABLE = "subject";
    private static final String ATTENDANCE_TABLE = "attendance";
    private static final int DATABASE_VERSION = 4;
    private static final String DB_NAME = "NAME";
    private static final String DB_ROLLNO = "ROLLNO";
    private static final String DB_BRANCH = "BRANCH";
    private static final String DB_YEARIN = "YEARIN";
    private static final String DB_SEMESTER = "SEMESTER";
    private static final String DB_DIVISION = "DIVISION";
    private static final String CREATE_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS " + STUDENT_TABLE + "("+
                                            DB_NAME+" TEXT NOT NULL,"+
                                            DB_ROLLNO+" INT NOT NULL," +
                                            DB_BRANCH+" VARCHAR(7) NOT NULL,"+
                                            DB_YEARIN+" INT NOT NULL,"+
                                            DB_SEMESTER+" VARCHAR(5) NOT NULL,"+
                                            DB_DIVISION+" VARCHAR(2) NOT NULL)";
    private static final String DELETE_STUDENT_TABLE = "DROP TABLE IF EXISTS " + STUDENT_TABLE;
    //declaration of post table
    private static final String DB_SUBJECTCODE = "SUBJECTCODE";
    private static final String DB_SUBJECTNAME = "SUBJECTNAME";
    private static final String DB_SUBJECTABBR = "SUBJECTABBR";
    private static final String CREATE_SUBJECT_TABLE = "CREATE TABLE IF NOT EXISTS " + SUBJECT_TABLE + "("+
                                        DB_SUBJECTCODE+" VARCHAR(15) NOT NULL PRIMARY KEY,"+
                                        DB_SUBJECTNAME+" TEXT NOT NULL," +
                                        DB_SUBJECTABBR+ " VARCHAR(10) NOT NULL,"+
                                        DB_BRANCH+" VARCHAR(7) NOT NULL,"+
                                        DB_YEARIN+" INT NOT NULL,"+
                                        DB_SEMESTER+" VARCHAR(5) NOT NULL)";
    private static final String DELETE_SUBJECT_TABLE = "DROP TABLE IF EXISTS " + SUBJECT_TABLE;
    //declaration of subject table ends
    private static final String DB_USERNAME="USERNAME";
    private static final String DB_ABSENT="ABSENT";
    private static final String DB_DATE="DATE";
    private static final String DB_PERIOD="PERIOD";
    private static final String CREATE_ATTENDACE_TABLE = "CREATE TABLE IF NOT EXISTS " + ATTENDANCE_TABLE + "("+
            DB_SUBJECTCODE+" VARCHAR(15) NOT NULL,"+
            DB_DATE+" VARCHAR(12) NOT NULL," +
            DB_DIVISION+ " VARCHAR(3) NOT NULL,"+
            DB_YEARIN+" INT NOT NULL,"+
            DB_PERIOD+" INT NOT NULL,"+
            DB_BRANCH+" VARCHAR(7) NOT NULL,"+
            DB_USERNAME+" TEXT NOT NULL,"+
            DB_ABSENT+" TEXT NOT NULL,"+
            DB_SEMESTER+" VARCHAR(4) NOT NULL)";
    private static final String DELETE_ATTENDANCE_TABLE = "DROP TABLE IF EXISTS " + ATTENDANCE_TABLE;
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_SUBJECT_TABLE);
        db.execSQL(CREATE_ATTENDACE_TABLE);
        Log.v("databasehelper","created database");
    }
    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL(DELETE_STUDENT_TABLE);
        db.execSQL(DELETE_SUBJECT_TABLE);
        db.execSQL(DELETE_ATTENDANCE_TABLE);
        //Create tables again
        Log.v("datahelper","upgraded database.");
        onCreate(db);
    }
    //TODO:STUDENT DETAILS
    int getstudentcount()
    {
        String countQuery = "SELECT  * FROM " + STUDENT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    boolean syncStudentData(String data)
    {
        try
        {
            JSONObject jo=new JSONObject(data);
            String[] branches={"cse","ce","me","it","eee","ec"};
            SQLiteDatabase db = this.getWritableDatabase();
            // Start the transaction.
            db.beginTransaction();
            try {
                for (String branch : branches) {
                    try {
                        JSONArray ja = jo.getJSONArray(branch);

                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject singleJson = ja.getJSONObject(i);
                            String name, division, semester, rollno, year;
                            name = singleJson.getString("name");
                            rollno = singleJson.getString("rollno");
                            division = singleJson.getString("division");
                            year = singleJson.getString("year");
                            semester = singleJson.getString("semester");
                            String query = "SELECT " + DB_NAME + " FROM " + STUDENT_TABLE + " WHERE "
                                    + DB_YEARIN + "=" + year + " AND "
                                    + DB_BRANCH + " LIKE '" + branch + "' AND "
                                    + DB_DIVISION + " LIKE '" + division + "' AND "
                                    + DB_ROLLNO + "=" + rollno;
                            Cursor c = db.rawQuery(query, null);
                            if (c.getCount() <= 0) {
                                ContentValues values;
                                values = new ContentValues();
                                values.put(DB_NAME, name);
                                values.put(DB_SEMESTER, semester);
                                values.put(DB_YEARIN, year);
                                values.put(DB_DIVISION, division);
                                values.put(DB_ROLLNO, rollno);
                                values.put(DB_BRANCH, branch);
                                long ins = db.insert(STUDENT_TABLE, null, values);
                                Log.v("Insert in student", ins + "");
                                // Insert into database successfully.
                            } else {
                                Log.v("database", "already exist:" + rollno);
                            }
                            c.close();
                        }
                    } catch (JSONException je) {
                        Log.v("database", "nothing to worry");
                    }
                }
            }
            catch (SQLiteException e)
            {
                e.printStackTrace();
            }
            finally
            {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
            return true;
        }catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    JSONArray getStudentDetails(String subjectcode,String division)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        JSONArray ja=new JSONArray();
        Log.v("database","subject:"+subjectcode+";division:"+division);
        division=division.toUpperCase();
        String subjectQuery="Select "+DB_BRANCH+","+DB_YEARIN+","+DB_SUBJECTCODE+" FROM "+SUBJECT_TABLE+" WHERE "+DB_SUBJECTCODE+" LIKE '"+subjectcode+"'";
        Cursor c=db.rawQuery(subjectQuery,null);
        String branch="",yearin="";
        Log.v("databasehelper","inside if:c.getCount"+c.getCount());
        while(c.moveToNext())
        {
            branch=c.getString(c.getColumnIndex(DB_BRANCH));
            branch=branch.toLowerCase();
            yearin=c.getString(c.getColumnIndex(DB_YEARIN));
            Log.v("databasehelper","branch:"+branch+";year:"+yearin+";subjectcode:"+c.getString(c.getColumnIndex(DB_SUBJECTCODE)));
        }
        c.close();
        String studentQuery="SELECT "+DB_NAME+","+DB_ROLLNO+" FROM "+STUDENT_TABLE+" WHERE "
                +DB_YEARIN+"="+yearin+" AND "+DB_BRANCH+"='"+branch+"' AND "+DB_DIVISION+"='"+division+"'";
        Cursor c1=db.rawQuery(studentQuery,null);
        try {
            while (c1.moveToNext()) {
                JSONObject jo = new JSONObject();
                jo.put("name", c1.getString(c1.getColumnIndex(DB_NAME)));
                jo.put("rollno", c1.getInt(c1.getColumnIndex(DB_ROLLNO)));
                ja.put(jo);
            }
            Log.v("database","jsonarray:"+ja);
            return ja;
        }catch (JSONException je)
        {
            je.printStackTrace();
        }
        c1.close();
        return null;
    }
    //TODO:attendance Marker
    Boolean addAttendance(String[] data)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Start the transaction.
        db.beginTransaction();
        ContentValues values;
        String username=data[0];
        String subjectcode=data[1].toUpperCase();
        String division=data[2];
        String period=data[3];
        String absent=data[4];
        String date=data[5];
        Log.v("database","date:"+date);
        String branch="",yearin="",semester="";
        String getSubDetail="SELECT "+DB_BRANCH+","+DB_YEARIN+","+DB_SEMESTER+" FROM "+SUBJECT_TABLE
                                                    + " WHERE "+DB_SUBJECTCODE+" LIKE '"+subjectcode+"'";
        Cursor c=db.rawQuery(getSubDetail,null);
        if(c.moveToNext())
        {
            branch=c.getString(c.getColumnIndex(DB_BRANCH));
            yearin=c.getString(c.getColumnIndex(DB_YEARIN));
            semester=c.getString(c.getColumnIndex(DB_SEMESTER));
        }
        try {
            values = new ContentValues();
            values.put(DB_USERNAME, username);
            values.put(DB_SUBJECTCODE, subjectcode);
            values.put(DB_DIVISION, division);
            values.put(DB_PERIOD, period);
            values.put(DB_BRANCH, branch);
            values.put(DB_SEMESTER, semester);
            values.put(DB_ABSENT, absent);
            values.put(DB_YEARIN, yearin);
            values.put(DB_DATE,date);
            long i = db.insert(ATTENDANCE_TABLE, null, values);
            Log.i("Insert", i + "");
            db.setTransactionSuccessful();
            return true;
        }catch (SQLiteException se)
        {
            se.printStackTrace();
            return false;
        }
        finally {
            c.close();
            db.endTransaction();
            db.close();
        }
    }
    JSONArray syncAttendance()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+ATTENDANCE_TABLE;
        Cursor c=db.rawQuery(query,null);
        Log.v("test","length:"+c.getCount());
        db.beginTransaction();
        try
        {
            JSONArray ja=new JSONArray();
            while(c.moveToNext())
            {
                JSONObject jo=new JSONObject();
                jo.put("branch",c.getString(c.getColumnIndex(DB_BRANCH)));
                jo.put("year",c.getString(c.getColumnIndex(DB_YEARIN)));
                jo.put("division",c.getString(c.getColumnIndex(DB_DIVISION)));
                jo.put("username",c.getString(c.getColumnIndex(DB_USERNAME)));
                jo.put("subjectcode",c.getString(c.getColumnIndex(DB_SUBJECTCODE)));
                jo.put("period",c.getString(c.getColumnIndex(DB_PERIOD)));
                jo.put("date",c.getString(c.getColumnIndex(DB_DATE)));
                jo.put("absent",c.getString(c.getColumnIndex(DB_ABSENT)));
                jo.put("semester",c.getString(c.getColumnIndex(DB_SEMESTER)));
                ja.put(jo);
            }
            c.close();
            return ja;
        }catch (JSONException je)
        {
            return null;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    Boolean deleteAttendance()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(ATTENDANCE_TABLE,null,null)>0;
    }
    //TODO:Subject details.
    int getSubjectCount()
    {
        String countQuery = "SELECT  * FROM " + SUBJECT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    Boolean syncSubjectData(String data)
    {
        try
        {
            JSONArray jo=new JSONArray(data);
            SQLiteDatabase db = this.getWritableDatabase();
            // Start the transaction.
            db.beginTransaction();
            try
            {
                for(int i=0;i<jo.length();i++)
                {
                    JSONObject singleJson=jo.getJSONObject(i);
                    String subcode,subname,subabbr,branch,year,semester;
                    subcode=singleJson.getString("subjectcode");
                    subname=singleJson.getString("subjectname");
                    subabbr=singleJson.getString("subjectabbr");
                    year=singleJson.getString("yearin");
                    branch=singleJson.getString("branch");
                    semester=singleJson.getString("semester");
                    ContentValues values;
                        values = new ContentValues();
                        values.put(DB_SUBJECTCODE, subcode);
                        values.put(DB_SUBJECTNAME,subname);
                        values.put(DB_SUBJECTABBR,subabbr);
                        values.put(DB_YEARIN,year);
                        values.put(DB_SEMESTER,semester);
                        values.put(DB_BRANCH,branch);
                        try {
                            long ins = db.insert(SUBJECT_TABLE, null, values);
                            Log.v("Insert in subject", ins + "");
                        }
                        catch(SQLiteConstraintException sc)
                        {
                            sc.printStackTrace();
                        }
                        // Insert into database successfully.
                }
                return true;
            }
            catch (SQLiteException e)
            {
                e.printStackTrace();
                return false;
            }
            finally
            {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    String[] getTutorSubject(int year,String branch)
    {
        String countQuery = "SELECT "+DB_SUBJECTCODE+" FROM " + SUBJECT_TABLE+" WHERE "+DB_BRANCH+"='"+branch+"' AND "+DB_YEARIN+"='"+year+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        String[] subject=new String[cursor.getCount()];
        int i=0;
        while(cursor.moveToNext())
        {
            subject[i]=cursor.getString(cursor.getColumnIndex("SUBJECTCODE"));
            i++;
        }
        cursor.close();
        if(subject.length>0)
            return subject;
        else
            return null;
    }
    String[] getHodSubject(String branch)
    {
        List<String> subject = new ArrayList<String>();
        String[] branches=branch.split(",");
        int k=0;
        while(k<branches.length) {
            String countQuery = "SELECT " + DB_SUBJECTCODE + " FROM " + SUBJECT_TABLE + " WHERE " + DB_BRANCH + "='" + branches[k] + "'";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            while (cursor.moveToNext()) {
                subject.add(cursor.getString(cursor.getColumnIndex("SUBJECTCODE")));
            }
            k++;
            cursor.close();
        }
        if(subject.size()>0) {
            String[] sSubject=new String[subject.size()];
            return subject.toArray(sSubject);
        }
        else
            return null;
    }
    String[] getAllSubject()
    {
        List<String> subject = new ArrayList<String>();
        String countQuery = "SELECT " + DB_SUBJECTCODE + " FROM " + SUBJECT_TABLE ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        while (cursor.moveToNext()) {
            subject.add(cursor.getString(cursor.getColumnIndex("SUBJECTCODE")));
        }
            cursor.close();
        if(subject.size()>0) {
            String[] sSubject=new String[subject.size()];
            return subject.toArray(sSubject);
        }
        else
            return null;
    }
}
