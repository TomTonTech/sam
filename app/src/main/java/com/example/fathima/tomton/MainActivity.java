package com.example.fathima.tomton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final String URL_ADDR="http://tomtontech.in/android/";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etUser;
    private EditText etPassword;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        SharedPreferences sharedpreferences = context.getSharedPreferences("Login", 0);
        String user=sharedpreferences.getString("user","");
        int firsttime=sharedpreferences.getInt("firsttime",0);
        if(firsttime==0)
        {
            Intent intent=new Intent(MainActivity.this,SplashScreen.class);
            startActivity(intent);
        }
        if(!user.isEmpty())
        {
            Intent intent=new Intent(MainActivity.this,MainWindow.class);
            startActivity(intent);
        }
        // Get Reference to variables
        etUser = (EditText) findViewById(R.id.Username);
        etPassword = (EditText) findViewById(R.id.password);

    }

    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0) {

        // Get text from Username and passord field
        final String user = etUser.getText().toString();
        final String password = etPassword.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(user,password);

    }

    //forgot password button
    public void forgotPassword(View arg0)
    {
        Intent inte=new Intent(context,ForgotPass.class);
        startActivity(inte);
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
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
                String loginaddr=URL_ADDR.concat("loginan.php");
                url = new URL(loginaddr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
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
            String name="",auth="",username="",branch="",designation="",subject="";
            int prio=0;
            pdLoading.dismiss();
            if(result.equalsIgnoreCase("unsuccess"))
            {
                Toast.makeText(context,"Incorrect Username Or Password ",Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("exception")||result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(MainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }else
            {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    name=jsonObject.getString("name");
                    prio=jsonObject.getInt("priority");
                    username=jsonObject.getString("username");
                    auth=jsonObject.getString("authentication");
                    branch=jsonObject.getString("branch");
                    designation=jsonObject.getString("designation");
                    subject=jsonObject.getString("subject");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(auth.equalsIgnoreCase("success"))
                {
                        SharedPreferences sharedpreferences = context.getSharedPreferences("Login", 0);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("user", name);
                        editor.putInt("priority",prio);
                        editor.putString("username",username);
                        editor.putString("branch",branch);
                        editor.putString("designation",designation);
                        editor.putString("subject",subject);
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this,MainWindow.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                }
            }
        }
    }
}
