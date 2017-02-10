package com.example.fathima.tomton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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

public class IndividualPro extends AppCompatActivity {
    protected SharedPreferences sharedPreferences;
    protected Context ctx;
    protected EditText et;
    protected ListView listView;
    protected TextView tv;
    protected Activity avt;
    protected LinearLayout ll;
    protected Spinner dropdown1;
    protected RadioButton rb;
    protected Button btn;
    protected String[] date,period,status;
    protected String nameS,subjectS,rollS,divisionS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_pro);
        ctx = this;
        avt=(Activity)ctx;
        tv=(TextView)findViewById(R.id.tv_error);
        tv.setText("");
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        et=(EditText)findViewById(R.id.et_name);
        rb=(RadioButton)findViewById(R.id.radioButton2);
        ll=(LinearLayout)findViewById(R.id.linear);
        ll.setVisibility(LinearLayout.GONE);
        btn=(Button)findViewById(R.id.btn_view);
        nameS=getIntent().getStringExtra("name");
        subjectS=getIntent().getStringExtra("subject");
        rollS=getIntent().getStringExtra("roll");
        divisionS=getIntent().getStringExtra("division");
        if(nameS!=null && !nameS.isEmpty())
        {
            Log.v("individual",nameS);
            et.setText(nameS);
            if(divisionS.equals("b"))
            {
                rb.setChecked(true);
            }
            AsyncIndi i=new AsyncIndi();
            i.execute(nameS,divisionS,subjectS);
        }
        AsyncSub a=new AsyncSub();
        a.execute();
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(et.getText()==null || !isAlpha(et.getText().toString()))
                        {
                            Toast.makeText(ctx,"Provide Relevant Data",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            tv.setText("");
                            nameS=et.getText().toString();
                            divisionS=rb.isChecked()?"b":"a";
                            subjectS=dropdown1.getSelectedItem().toString();
                            Log.v("individual","reached here");
                            AsyncIndi i=new AsyncIndi();
                            i.execute(nameS,divisionS,subjectS);
                        }
                    }
                }
        );
    }
    private class AsyncSub extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(IndividualPro.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            String name=sharedPreferences.getString("username","");
            String subUrl=MainActivity.URL_ADDR.concat("subselect.php");
            try {
                URL url=new URL(subUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                return bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onPostExecute(String result)
        {
            pdLoading.dismiss();
            Log.v("teacher",result);
            try {
                JSONArray jsonArray=new JSONArray(result);
                JSONObject j=jsonArray.getJSONObject(0);
                Log.v("teacher","json element:"+j);
                String subjects = j.getString("subject");
                Log.v("main", subjects);
                String[] subject = subjects.split(",");
                if(subjectS!=null && !subjectS.isEmpty())
                {
                    for (int i=0;i<subject.length;i++) {
                        if(subject[i].equals(subjectS))
                        {
                            String temp=subject[0];
                            subject[0]=subject[i];
                            subject[i]=temp;
                            break;
                        }
                    }
                }
                dropdown1 = (Spinner) findViewById(R.id.spinner_subject);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_layout, subject);
                dropdown1.setAdapter(adapter1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class AsyncIndi extends AsyncTask<String,String,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(IndividualPro.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            String name=strings[0];
            String division=strings[1];
            String subject=strings[2];
            String subUrl=MainActivity.URL_ADDR.concat("individualpro.php");
            try {
                URL url=new URL(subUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")+"&"+
                        URLEncoder.encode("division", "UTF-8") + "=" + URLEncoder.encode(division, "UTF-8")+"&"+
                        URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                return bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result)
        {
            pdLoading.dismiss();
            Log.v("individual",result);
            try {
                listView = (ListView) findViewById(R.id.lv_individual);
                JSONObject jo=new JSONObject(result);
                JSONArray ja=jo.getJSONArray("student");
                date=new String[ja.length()];
                period=new String[ja.length()];
                status=new String[ja.length()];
                for(int i=0;i<ja.length();i++)
                {
                    JSONObject njo=ja.getJSONObject(i);
                    date[i]=njo.getString("date");
                    period[i]=njo.getString("period");
                    if(njo.getInt("status")==0)
                    {
                        status[i]="absent";
                    }
                    else
                    {
                        status[i]="present";
                    }
                    Log.v("individual","date:"+date[i]+"\t period:"+period[i]+"\t status:"+status[i]);
                }
                ll.setVisibility(LinearLayout.VISIBLE);
                CustomList_Individual customList = new CustomList_Individual(avt, date,period,status);
                listView.setAdapter(customList);

            } catch (JSONException e) {
                ll.setVisibility(LinearLayout.GONE);
                listView.setAdapter(null);
                String errorS="no student named ".concat(nameS).concat(" in the class.try again");
                tv.setText(errorS);
            }
        }
    }

    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                Log.v("isalpha","here");
                return false;
            }
        }

        return true;
    }
}
