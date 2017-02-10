package com.example.fathima.tomton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class Teacher extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected Context ctx;
    protected Activity avt;
    protected String sem, sub,stat,div,period;
    protected ArrayList<String> attended=new ArrayList<>();
    protected ArrayList<String> absent=new ArrayList<>();
    protected ListView listView;
    protected SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        ctx = this;
        avt = (Activity) ctx;
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            sub = extra.getString("subject");
            char div = extra.getChar("division");
            period=extra.getString("period");
            this.div=String.valueOf(div);
            if(sub!=null && !sub.isEmpty())
                sem = sub.substring(5,6);
        }
        int yr=Integer.parseInt(sem);
        yr=(int)Math.floor(yr/2)+1;
        String y=String.valueOf(yr);
        sem = "s".concat(sem);
        Log.v("teacher","semester:"+sem);
        Log.v("teacher", "\n subject:" + sub+"\n division:"+div);
        listView = (ListView) findViewById(R.id.listView);
        StdAysnc stdAysnc = new StdAysnc();
        stdAysnc.execute(y);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    public  void checkSubmit(View arg0)
    {
        Log.v("hahaha","reach here");
        UpAsync upAsync=new UpAsync();
        upAsync.execute(sub);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_update) {

        } else if (id == R.id.nav_view) {
            Intent intent = new Intent(Teacher.this, ViewAtt.class);
            intent.putExtra("semester", sem);
            intent.putExtra("subject", sub);
            startActivity(intent);
        } else if (id == R.id.nav_new) {
            Intent intent = new Intent("com.example.fathima.tomton.teacher_login");
            startActivity(intent);

        }else if ( id==R.id.nav_indi_progress){
            Intent intent=new Intent("IndividualPro");
            startActivity(intent);
        }
        else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to Logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();
                            editor.apply();
                            finish();
                            Intent intent1 = new Intent(Teacher.this, MainActivity.class);
                            startActivity(intent1);

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class StdAysnc extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String yr1 = params[0];
            try {
                String stdUrl=MainActivity.URL_ADDR.concat("stdselect.php");
                URL url = new URL(stdUrl);
                URLConnection conn = url.openConnection();
                String data = URLEncoder.encode("year", "UTF-8") + "=" + URLEncoder.encode(yr1, "UTF-8")+"&"+
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
                                    checkBox.setChecked(false);
                                    Log.v("hahah","eedyum etti");
                                    attended.remove(roll);
                                    absent.add(roll);
                                }
                                else{
                                    checkBox.setChecked(true);
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
        ProgressDialog pdLoading = new ProgressDialog(Teacher.this);
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
                String str=absent.get(0);
                for(int i=1;i<absent.size();i++)
                    str=str+","+absent.get(i);
                Log.v("hehe","subject"+sub+"\nabsent:"+str);
                String upUrl=MainActivity.URL_ADDR.concat("update_student_att.php");
                URL url = new URL(upUrl);
                URLConnection conn = url.openConnection();
                String data = URLEncoder.encode("subcode", "UTF-8") + "=" + URLEncoder.encode(sub, "UTF-8") + "&"
                                + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(str, "UTF-8")+"&"
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
            if(Integer.parseInt(result)==1)
            {
                Toast.makeText(ctx, "Updated Successfully", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(Teacher.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Attendance Result")
                        .setMessage("absentees are:"+absent)
                        .setPositiveButton("Make Correction", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }

                        })
                        .setNegativeButton("Ok", null)
                        .show();
            }
        }

    }
}