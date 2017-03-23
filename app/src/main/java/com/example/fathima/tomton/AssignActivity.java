package com.example.fathima.tomton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by user on 3/23/2017.
 */

public class AssignActivity extends AppCompatActivity {
    private Context ctx;
    private SharedPreferences sharedPreferences;
    private Spinner subjectS,teacherS;
    private String[] teacher,teacherUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);
        ctx=this;
        SqliteSelect ss = new SqliteSelect(ctx);
        String[] subject = ss.selectSubject();
        sharedPreferences=ctx.getSharedPreferences("Login",0);
        int prio=sharedPreferences.getInt("priority",0);
        subjectS = (Spinner) findViewById(R.id.spinner_subject);
        teacherS = (Spinner) findViewById(R.id.spinner_teacher);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ctx, R.layout.spinner_layout, subject);
        subjectS.setAdapter(adapter2);
        AsyncTeacher as=new AsyncTeacher();
        as.execute(String.valueOf(prio));
    }
    public class AsyncTeacher extends AsyncTask<String,Void,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ctx);
        @Override
        protected void onPreExecute()
        {
            pdLoading.setMessage("Checking Teacher Data");
            pdLoading.setCancelable(false);
            pdLoading.setProgress(0);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String prio=params[0];
            try {
                String stdUrl=MainActivity.URL_ADDR.concat("teacherselect.php");
                Log.v("markatt","url:"+stdUrl);
                URL url = new URL(stdUrl);
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                String urldata = URLEncoder.encode("priority", "UTF-8") + "=" + URLEncoder.encode(prio, "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(urldata);
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "exception";
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "exception";
                    }
                }
                String result = sb.toString();
                Log.v("HAHAHA", result);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";

            }
        }
        @Override
        protected void onPostExecute(String result)
        {
            pdLoading.dismiss();
            if(result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(ctx,"Error While Adding Teacher",Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    JSONObject jo=new JSONObject(result);
                    JSONArray ja=jo.getJSONArray("teacher");
                    teacher=new String[ja.length()];
                    teacherUser=new String[ja.length()];
                    int i=0;
                    while(i<teacher.length)
                    {
                        JSONObject jo1=ja.getJSONObject(i);
                        teacher[i]=jo1.getString("name");
                        teacherUser[i]=jo1.getString("username");
                        i++;
                    }
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<>(ctx, R.layout.spinner_layout, teacher);
                    teacherS.setAdapter(adapter3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v("assign",result);
            }
        }
    }
    public void clkAssing(View view)
    {
        String teacher=teacherS.getSelectedItem().toString();
        String subject=subjectS.getSelectedItem().toString();
        AsyncAssign aa=new AsyncAssign();
        Log.v("teacher",teacher+"\n"+subject);
        aa.execute(teacher,subject);
    }
    public class AsyncAssign extends AsyncTask<String,Void,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ctx);
        @Override
        protected void onPreExecute()
        {
            pdLoading.setMessage("Assigning Subject To Teacher");
            pdLoading.setCancelable(false);
            pdLoading.setProgress(0);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String teacher=params[0];
            String subject=params[1];
            String username=sharedPreferences.getString("username","");
            try {
                String stdUrl=MainActivity.URL_ADDR.concat("assignteacher.php");
                Log.v("markatt","url:"+stdUrl);
                URL url = new URL(stdUrl);
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Log.v("teacher","username:"+username);
                String urldata = URLEncoder.encode("teacher", "UTF-8") + "=" + URLEncoder.encode(teacher, "UTF-8") + "&"
                                +URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8") + "&"
                                +URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(urldata);
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";

            }
        }
        @Override
        protected void onPostExecute(String result)
        {
            pdLoading.dismiss();
            Log.v("assign","result:"+result);
            try {
                if (result.equalsIgnoreCase("exception")) {
                    Toast.makeText(ctx, "Error While Assigning", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(result) == 1) {
                    Toast.makeText(ctx, "Success On Assigning", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "Error On Assigning", Toast.LENGTH_SHORT).show();
                }
            }catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        }
    }
}
