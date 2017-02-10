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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.text.DecimalFormat;

public class ViewAtt extends AppCompatActivity {
    protected Context ctx;
    protected Activity avt;
    ListView listView;
    protected Spinner subjectS,sortS;
    protected TextView tv_error;
    protected LinearLayout llhead;
    protected RadioGroup rg;
    protected String subjectstr;
    SharedPreferences sharedPreferences;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_att);
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        String name=sharedPreferences.getString("username","");
        String[] sortSS={"Sort By Roll No","Sort By Hour","Sort By Name","Sort By Percentage"};
        sortS=(Spinner)findViewById(R.id.sort_spinner);
        btn=(Button)findViewById(R.id.view_btn);
        rg=(RadioGroup)findViewById(R.id.radiogroup);
        tv_error=(TextView)findViewById(R.id.tv_error);
        llhead=(LinearLayout)findViewById(R.id.linear);
        llhead.setVisibility(View.GONE);
        ctx=this;
        avt=(Activity)ctx;
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, sortSS);
        sortS.setAdapter(adapter1);
        StAysnc stdAysnc=new StAysnc();
        stdAysnc.execute(name);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String sub=subjectS.getSelectedItem().toString();
                        String sortor=sortS.getSelectedItem().toString();
                        int ord;
                        switch (sortor)
                        {
                            case "Sort By Roll No":
                                ord=1;
                                break;
                            case "Sort By Name":
                                ord=2;
                                break;
                            case "Sort By Hour":
                                ord=3;
                                break;
                            case "Sort By Percentage":
                                ord=4;
                                break;
                            default:ord=0;
                        }
                        subjectstr=sub;
                        int idrb=rg.getCheckedRadioButtonId();
                        char c=' ';
                        if(idrb==R.id.radioButton)
                        {
                            c='a';
                        }
                        else if(idrb==R.id.radioButton2)
                        {
                            c='b';
                        }
                        String div=String.valueOf(c);
                        viewAsync v=new viewAsync();
                        v.execute(sub,div,String.valueOf(ord));
                    }
                }
        );
    }
    public class StAysnc extends AsyncTask<String,Void,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ViewAtt.this);
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
            String user=params[0];
            try {
                String testUrl=MainActivity.URL_ADDR.concat("subselect.php");
                URL url=new URL(testUrl);
                URLConnection conn = url.openConnection();
                String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
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
                return sb.toString();
            }  catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onPostExecute(String result)
        {
            pdLoading.dismiss();
            try {
                JSONArray jsonArray=new JSONArray(result);
                JSONObject j=jsonArray.getJSONObject(0);
                String subjects = j.getString("subject");
                String[] subject = subjects.split(",");
                subjectS = (Spinner) findViewById(R.id.spinner_subject);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_layout, subject);
                subjectS.setAdapter(adapter1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class viewAsync extends AsyncTask<String,String,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ViewAtt.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String subject=strings[0];
            String division=strings[1];
            int order=Integer.parseInt(strings[2]);
            try {
                String testUrl=MainActivity.URL_ADDR.concat("viewatt.php");
                URL url=new URL(testUrl);
                URLConnection conn = url.openConnection();
                String data  = URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8")+"&"+
                        URLEncoder.encode("division", "UTF-8") + "=" + URLEncoder.encode(division, "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                in.close();
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onPostExecute(String result)
        {
            pdLoading.dismiss();
            JSONObject jsono,teaj;
            JSONArray jsonArray;
            listView = (ListView) findViewById(R.id.listView2);
            try {
                DecimalFormat df=new DecimalFormat("#.##");
                jsono=new JSONObject(result);
                teaj=jsono.getJSONObject("teacher");
                jsonArray = jsono.getJSONArray("students");
                String subjectTotal=teaj.getString("total");
                int len=jsonArray.length();
                String[] name=new String[len+1];
                String[] rollno=new String[len+1];
                String[] count=new String[len+1];
                String[] perc=new String[len+1];
                int tot=Integer.parseInt(subjectTotal);
                if(tot>0)
                {
                    int i;
                    for(i=0;i<len;i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);
                        name[i]=c.getString("name");
                        rollno[i]=c.getString("rollno");
                        count[i]=c.getString("absent");
                        int val=Integer.parseInt(count[i]);
                        int present=tot-val;
                        count[i]=String.valueOf(present);
                        double per=((double)present/(double)tot)*100;
                        perc[i]=df.format(per).concat("%");
                    }
                    name[i]="Total";
                    rollno[i]="";
                    count[i]=subjectTotal;
                    perc[i]="100%";
                    CustomList_View customList = new CustomList_View(avt, name,rollno,count,perc);
                    tv_error.setVisibility(View.GONE);
                    llhead.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    listView.setAdapter(customList);
                    listView.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    final String attname=((TextView)view.findViewById(R.id.attendance_name)).getText().toString();
                                    final String roll=((TextView)view.findViewById(R.id.attendance_roll)).getText().toString();
                                    String perc=((TextView)view.findViewById(R.id.attendance_perc)).getText().toString();
                                    final String divi=rg.getCheckedRadioButtonId()==R.id.radioButton?"a":"b";
                                    final String subject=subjectS.getSelectedItem().toString();
                                    String message="\t"+subject+"\nname = "+attname+"\n"+"Roll No ="+roll+"\n"+"Attendance Percentage = "+perc+"\nView Individual Performance";
                                    new AlertDialog.Builder(ctx)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Individual Progress")
                                            .setMessage(message)
                                            .setPositiveButton("View", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent1 = new Intent(ctx, IndividualPro.class);
                                                    intent1.putExtra("name",attname);
                                                    intent1.putExtra("roll",roll);
                                                    intent1.putExtra("division",divi);
                                                    intent1.putExtra("subject",subject);
                                                    startActivity(intent1);
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }
                            }
                    );
                }
                else
                {
                    String err="No Information Available Right Now.";
                    tv_error.setVisibility(View.VISIBLE);
                    llhead.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    tv_error.setText(err);
                }
            } catch (JSONException e) {
                String err="No Information Available";
                tv_error.setVisibility(View.VISIBLE);
                llhead.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                tv_error.setText(err);
            }
        }
    }
}
