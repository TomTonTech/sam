package com.example.fathima.tomton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
            new AsyncDB(ctx).execute();
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
                JSONArray ja=dbh.syncAttendance();
                if(ja.length()!=0)
                {
                    new AsyncAtt().execute(String.valueOf(ja));
                }
                else
                {
                    Toast.makeText(ctx,"EveryThing Is Synced.",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_download:
                // location found
                new AlertDialog.Builder(ctx)
                        .setIcon(R.drawable.ic_download)
                        .setTitle("Download Data")
                        .setMessage("There May Be No Changes On The Data After The Last Sync. Do You Really Wish To Download Data ?")
                        .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncDB(ctx).execute();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                Log.v("window","download clicked");
                return true;
            case R.id.action_update:
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;
                    new AsyncApp().execute(version);
                }catch (PackageManager.NameNotFoundException ne)
                {
                    ne.printStackTrace();
                }
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
    public class AsyncAtt extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            String data=strings[0];
            Log.v("window","data:"+data);
            String subUrl= MainActivity.URL_ADDR.concat("syncAtt.php");
            try {
                URL url=new URL(subUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(MainActivity.CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(MainActivity.READ_TIMEOUT);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                String urldata = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(data, "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(urldata);
                wr.flush();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                return bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
        }
        @Override
        public void onPostExecute(String result)
        {
            Log.v("window","result:"+result);
            if(result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(ctx,"No Internet Connection. Check Connection And Try Again Later.",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(result.equalsIgnoreCase("somerror"))
                {
                    dbh.deleteAttendance();
                    Toast.makeText(ctx,"There Was An Identical Insertion.Check If Your Insertion Is Correct.Successfully inserted data.",Toast.LENGTH_LONG).show();
                }
                else if(result.equalsIgnoreCase("success"))
                {
                    dbh.deleteAttendance();
                    Toast.makeText(ctx,"Successfully Synced Data.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ctx,"Something Went Wrong.Contact App Developer.",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public class AsyncApp extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String...strings) {
            String data=strings[0];
            Log.v("window","data:"+data);
            String subUrl= MainActivity.URL_ADDR.concat("getApkVersion.php");
            try {
                URL url=new URL(subUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(MainActivity.CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(MainActivity.READ_TIMEOUT);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                String urldata = URLEncoder.encode("version", "UTF-8") + "=" + URLEncoder.encode(data, "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(urldata);
                wr.flush();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                String result=bufferedReader.readLine();
                if(result.equalsIgnoreCase("same"))
                    return "same";

                else {
                    try {
                        URL url1 = new URL("http://tomtontech.in/apk/sam.apk");
                        HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        File sdcard = Environment.getExternalStorageDirectory();
                        File file = new File(sdcard, "sama.apk");
                        FileOutputStream fileOutput = new FileOutputStream(file);
                        InputStream inputStream1 = urlConnection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;

                        while ((bufferLength = inputStream1.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                        }
                        fileOutput.close();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                       return "exception";
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "exception";
                    }
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
        }
        @Override
        public void onPostExecute(String result)
        {
            if(result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(ctx,"No Internet Connection. Check Connection And Try Again Later.",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(result.equalsIgnoreCase("same"))
                {
                    dbh.deleteAttendance();
                    Toast.makeText(ctx,"Your Using The Latest Version",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(ctx,"Something Went Wrong.Contact App Developer.",Toast.LENGTH_SHORT).show();
                    installApk();
                }
            }
        }
    }
    private void downloadapk(){
        try {
            URL url = new URL("http://tomtontech.in/apk/sam.apk");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "sam.apk");
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void installApk(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File("/sdcard/sama.apk"));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
