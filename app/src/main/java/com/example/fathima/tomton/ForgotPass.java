package com.example.fathima.tomton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by user on 2/9/2017.
 */

public class ForgotPass extends AppCompatActivity{
    private Context context;
    private EditText etUsername;
    private EditText etEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        context=this;
        etUsername=(EditText)findViewById(R.id.Username);
        etEmail=(EditText)findViewById(R.id.email);
    }
    public void onBackPressed() {
        Intent myIntent = new Intent(ForgotPass.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
        startActivity(myIntent);
        finish();
        return;
    }
    public void resetPass(View arg0)
    {
        final String user= etUsername.getText().toString();
        final String mail= etEmail.getText().toString();

        new AsyncReset().execute(user,mail);


    }
    private  class AsyncReset extends AsyncTask<String,String,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ForgotPass.this);
        HttpURLConnection conn;
        URL url = null;

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
            try {
                String addr=MainActivity.URL_ADDR.concat("getcode.php");
                url = new URL(addr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(MainActivity.READ_TIMEOUT);
                conn.setConnectTimeout(MainActivity.CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("email", params[1]);
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }
            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return(result.toString());
                }else{
                    return("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            if(result.equalsIgnoreCase("unsuccess"))
            {
                Toast.makeText(context,"Incorrect Username Or Password ",Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("exception")||result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(ForgotPass.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }else if(result.equalsIgnoreCase("success"))
            {
                Toast.makeText(context,"Verification Code has been sent to EMail ",Toast.LENGTH_LONG).show();
                Intent inte=new Intent(context,CheckPassCode.class);
                startActivity(inte);
                finish();
            }
        }

        }
}
