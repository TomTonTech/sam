package com.example.fathima.tomton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
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
public class teacherLogin extends AppCompatActivity {
    public Button btn;
    public TextView wel;
    protected Spinner dropdown1,periodS;
    protected Context ctx;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        ctx = this;
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        String name=sharedPreferences.getString("user","");
        wel=(TextView)findViewById(R.id.welcometext);
        String weltext="Hi, ".concat(name);
        String[] period = {"1","2","3","4","5","6"};
        periodS = (Spinner) findViewById(R.id.spinner_period);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ctx,R.layout.spinner_layout, period);
        periodS.setAdapter(adapter2);
        wel.setText(weltext);
        new AsyncSub().execute();
        final RadioGroup rb=(RadioGroup) findViewById(R.id.radiogroup);
        btn = (Button) findViewById(R.id.btn_teacher);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int idrb=rb.getCheckedRadioButtonId();
                        Log.v("teacher","checked:"+idrb);
                        String sub = dropdown1.getSelectedItem().toString();
                        String period=periodS.getSelectedItem().toString();
                        Intent intent = new Intent(teacherLogin.this,MarkAtt.class);
                        intent.putExtra("subject", sub);
                        char c=' ';
                        if(idrb==R.id.radioButton)
                        {
                            c='a';
                        }
                        else if(idrb==R.id.radioButton2)
                        {
                            c='b';
                        }
                        intent.putExtra("division",c);
                        intent.putExtra("period",period);
                        Toast.makeText(teacherLogin.this, "Selection Success", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(intent);
                    }
                }
        );
    }
    private class AsyncSub extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(teacherLogin.this);
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
            String username=sharedPreferences.getString("username","");
            String subUrl=MainActivity.URL_ADDR.concat("subselect.php");
            try {
                URL url=new URL(subUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(MainActivity.CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(MainActivity.READ_TIMEOUT);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
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
            pdLoading.dismiss();
            Log.v("teacher",result);
            if(result.equalsIgnoreCase("exception"))
            {
                Log.v("teacherlogin","there is a exception. find from sqlite");
            }
            else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject j = jsonArray.getJSONObject(0);
                    Log.v("teacher", "json element:" + j);
                    String subjects = j.getString("subject");
                    Log.v("main", subjects);
                    String[] subject = subjects.split(",");
                    dropdown1 = (Spinner) findViewById(R.id.subject);
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_layout, subject);
                    dropdown1.setAdapter(adapter1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}