package com.example.fathima.tomton;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class AsyncDB extends AsyncTask<String,Void,String>
{
    Context ctx;
    private ProgressDialog pd;
    private DatabaseHelper dbh;
    AsyncDB(Context ctx)
    {
        this.ctx=ctx;
        dbh=new DatabaseHelper(ctx);
    }
    @Override
    protected void onPreExecute()
    {
        pd=ProgressDialog.show(ctx,"First Time","Downloading Student Details",true);
    }
    @Override
    protected String doInBackground(String... strings) {
        String subUrl= MainActivity.URL_ADDR.concat("sync.php");
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
        if(result.equalsIgnoreCase("exception"))
        {
            Toast.makeText(ctx,"Cant Connect to Internet.Check Network Connection And Try Again", Toast.LENGTH_LONG).show();
        }
        else
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
                    Toast.makeText(ctx,"There Was An Error In Inserting. Press Sync Again To Download Again.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(ctx,"There Was An Error In Inserting. Press Sync Again To Download Again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        pd.dismiss();
    }
}