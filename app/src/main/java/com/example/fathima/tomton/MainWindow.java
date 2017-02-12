package com.example.fathima.tomton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainWindow extends AppCompatActivity {
    protected Context ctx;
    protected SharedPreferences sp;
    protected RelativeLayout rlassign,rlupdate;
    protected DatabaseHelper dbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        sp=this.getSharedPreferences("Login", 0);
        int prio=sp.getInt("priority",0);
        rlassign=(RelativeLayout)findViewById(R.id.rlassign);
        rlupdate=(RelativeLayout)findViewById(R.id.rlupdate);
        if(prio==1)
        {
            rlupdate.setVisibility(View.GONE);
            rlassign.setVisibility(View.VISIBLE);
        }
        else
        {
            rlassign.setVisibility(View.GONE);
            rlupdate.setVisibility(View.VISIBLE);
        }
        ctx=this;
        dbh=new DatabaseHelper(ctx);
        int count=dbh.getstudentcount();
        int subcount=dbh.getSubjectCount();
        Log.v("mainwindow","got count:"+subcount+";get student count: "+count);
        if(count==0||subcount==0)
        {
            new AsyncDB().execute();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_sync:
                // search action
                Log.v("window","sync clicked");
                return true;
            case R.id.action_download:
                // location found
                Log.v("window","download clicked");
                return true;
            case R.id.action_settings:
                // refresh
                Log.v("window","settings clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void markFunction(View view)
    {
        Intent i=new Intent(ctx,teacherLogin.class);
        startActivity(i);
    }
    public void viewFunction(View view)
    {
        Intent i=new Intent(ctx,ViewAtt.class);
        startActivity(i);
    }
    public void updateFunction(View view)
    {
        Intent i=new Intent(ctx,UpdateActivity.class);
        startActivity(i);
    }
    public void indiFunction(View view)
    {
        Intent i=new Intent(ctx,IndividualPro.class);
        startActivity(i);
    }
    public void messageFunction(View view)
    {
        Toast.makeText(ctx,"Service Unavailable",Toast.LENGTH_SHORT).show();
    }
    public void passwordFunction(View view)
    {
        Intent i=new Intent(ctx,ChangePass.class);
        startActivity(i);
    }
    public void contactFunction(View view)
    {
        Intent i=new Intent(ctx,ContactActivity.class);
        startActivity(i);
    }
    public void logoutFunction(View view)
    {
        new AlertDialog.Builder(ctx)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Do You Really Want To Logout ?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences=getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        editor.apply();
                        finish();
                        Intent intent1 = new Intent(MainWindow.this, MainActivity.class);
                        startActivity(intent1);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private class AsyncDB extends AsyncTask<String,Void,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute()
        {
            pd=ProgressDialog.show(ctx,"First Time","Downloading Student Details",true);
        }
        @Override
        protected String doInBackground(String... strings) {
            String subUrl=MainActivity.URL_ADDR.concat("sync.php");
            try {
                URL url=new URL(subUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(MainActivity.CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(MainActivity.READ_TIMEOUT);
                httpURLConnection.setDoInput(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                return bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
        }
        @Override
        protected void onPostExecute(String result)
        {
            try {
                Boolean syncB;
                JSONObject jo=new JSONObject(result);
                JSONObject jStudent=jo.getJSONObject("students");
                syncB=dbh.syncStudentData(jStudent.toString());
                JSONArray jSubject=jo.getJSONArray("subject");
                Boolean syncSubB=dbh.syncSubjectData(jSubject.toString());
                if(syncB&&syncSubB)
                {
                    Toast.makeText(ctx,"Successfully Downloaded Data From Server.",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(ctx,"There Was An Error In Inserting.Press Sync Again To Download Again.",Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();
        }
    }
}
