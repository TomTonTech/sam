package com.example.fathima.tomton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;

public class MarkAtt extends AppCompatActivity {
    protected Context ctx;
    protected Activity avt;
    protected TextView tv_name,tv_roll,tv_stat;
    protected String sub,div,period,user;
    protected ArrayList<String> attended=new ArrayList<>();
    protected ArrayList<String> absent=new ArrayList<>();
    protected ListView listView;
    protected SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_att);
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        user=sharedPreferences.getString("user","");
        ctx = this;
        avt = (Activity) ctx;
        tv_name=(TextView)findViewById(R.id.tv_name);
        tv_roll=(TextView)findViewById(R.id.tv_roll);
        tv_stat=(TextView)findViewById(R.id.tv_stat);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/FallingSky.otf");
        tv_name.setTypeface(face);
        tv_roll.setTypeface(face);
        tv_stat.setTypeface(face);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            sub = extra.getString("subject");
            char div = extra.getChar("division");
            period=extra.getString("period");
            this.div=String.valueOf(div);
        }
        listView = (ListView) findViewById(R.id.listView);
        StdAysnc stdAysnc = new StdAysnc();
        stdAysnc.execute(sub);
    }
    public  void checkSubmit(View arg0)
    {
        new AlertDialog.Builder(MarkAtt.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Attendance Result")
                .setMessage("absentees are:"+absent)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v("hahaha","reach here");
                        UpAsync upAsync=new UpAsync();
                        upAsync.execute(sub);
                    }

                })
                .setNegativeButton("Make Correction", null)
                .show();

    }
    public class StdAysnc extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String subject = params[0];
            try {
                String stdUrl=MainActivity.URL_ADDR.concat("stdselect.php");
                URL url = new URL(stdUrl);
                URLConnection conn = url.openConnection();
                String data = URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8")+"&"+
                        URLEncoder.encode("division", "UTF-8") + "=" + URLEncoder.encode(div, "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
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
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String result = sb.toString();
                Log.v("HAHAHA", result);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(result);
                int len = jsonArray.length();
                String[] name = new String[len];
                String[] roll = new String[len];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    name[i] = c.getString("name");
                    roll[i] = c.getString("roll");
                    if(Integer.parseInt(roll[i])<10)
                    {
                        roll[i]="0"+roll[i];
                        Log.v("hahaha",roll[i]);
                    }

                    attended.add(roll[i]);
                    Log.v("HAHAHA", "i:" + i + "\tname:" + name[i]);
                    Log.v("HAHAHA", "i:" + i + "\troll:" + roll[i]);
                }
                final CustomList customList = new CustomList(avt, name, roll);
                listView.setAdapter(customList);
                listView.setItemsCanFocus(true);
                Log.v("hahaha","last of post");
                listView.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Log.v("hahaa","eede ethi");
                                String roll=((TextView)view.findViewById(R.id.attendance_roll)).getText().toString();
                                CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkBox);
                                if(checkBox.isChecked())
                                {
                                    String chk="Absent";
                                    checkBox.setChecked(false);
                                    checkBox.setText(chk);
                                    Log.v("hahah","eedyum etti");
                                    attended.remove(roll);
                                    absent.add(roll);
                                }
                                else{

                                    String chk="Present";
                                    checkBox.setChecked(true);
                                    checkBox.setText(chk);
                                    attended.add(roll);
                                    absent.remove(roll);
                                }
                                Log.v("hehe","absent"+absent);
                            }
                        }
                );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class UpAsync extends AsyncTask<String, Void, String> {
        ProgressDialog pdLoading = new ProgressDialog(MarkAtt.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            String sub = params[0];

            try {
                String str="";
                if(!absent.isEmpty()) {
                    str = absent.get(0);
                    for (int i = 1; i < absent.size(); i++)
                        str = str + "," + absent.get(i);
                }
                Log.v("hehe","subject"+sub+"\nabsent:"+str);
                String upUrl=MainActivity.URL_ADDR.concat("update_student_att.php");
                URL url = new URL(upUrl);
                URLConnection conn = url.openConnection();
                String data = URLEncoder.encode("subcode", "UTF-8") + "=" + URLEncoder.encode(sub, "UTF-8") + "&"
                        + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(str, "UTF-8")+"&"
                        + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")+"&"
                        + URLEncoder.encode("division", "UTF-8") + "=" + URLEncoder.encode(div, "UTF-8")+"&"
                        + URLEncoder.encode("period", "UTF-8") + "=" + URLEncoder.encode(period, "UTF-8");

                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.v("teacher",result);
            pdLoading.dismiss();
            try
            {
                JSONArray ja=new JSONArray(result);
                JSONObject jo=ja.getJSONObject(0);
                String msg="";
                if(jo.has("extra"))
                {
                    msg+=jo.getString("extra");
                    jo=ja.getJSONObject(1);
                }
                msg+=jo.getString("message");
                Toast.makeText(ctx,msg, Toast.LENGTH_LONG).show();

            }catch (Exception e)
            {
                Toast.makeText(ctx,"error while updating please try again after sometime",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
