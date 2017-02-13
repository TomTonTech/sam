package com.example.fathima.tomton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ChangePass extends AppCompatActivity {
    protected EditText eCurrent,eNew,eConf;
    protected TextView tv;
    protected Context ctx;
    protected SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        ctx=this;
        sp=ctx.getSharedPreferences("Login",0);
        eCurrent=(EditText)findViewById(R.id.et_curPass);
        eNew=(EditText)findViewById(R.id.password);
        eConf=(EditText)findViewById(R.id.et_confPass);
        tv=(TextView)findViewById(R.id.tv_cpass);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/FallingSky.otf");
        eCurrent.setTypeface(face);
        eConf.setTypeface(face);
        eNew.setTypeface(face);
        tv.setTypeface(face);
    }
    protected void changePass(View view)
    {
        String str_cur,str_new,str_conf,str_user;
        str_user=sp.getString("username","");
        str_cur=eCurrent.getText().toString();
        str_new=eNew.getText().toString();
        str_conf=eConf.getText().toString();
        if(str_cur.isEmpty()||str_new.isEmpty()||str_conf.isEmpty()||str_cur.equals("")||str_new.equals("")||str_conf.equals(""))
        {
            Toast.makeText(ctx,"Fields Must Not Be Empty",Toast.LENGTH_SHORT).show();
        }
        else if(!str_new.equals(str_conf))
        {
            Toast.makeText(ctx,"Password Mismatch",Toast.LENGTH_SHORT).show();
        }
        else if(str_cur.equals(str_new))
        {
            Toast.makeText(ctx,"Can't Give Same Password Again.Give Another Password.",Toast.LENGTH_SHORT).show();
        }
        else if(str_new.length()<6 || str_new.length()>20)
        {
            Toast.makeText(ctx,"Password Must Contain Atleast 6 Character And Atmost 20 Character.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            AsyncCPass a=new AsyncCPass();
            a.execute(str_cur,str_new,str_user);
        }
    }
    protected class AsyncCPass extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd=new ProgressDialog(ctx);
            //this method will be running on UI thread
            pd.setMessage("\tUpdating Password...");
            pd.setCancelable(false);
            pd.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            String str_cur=strings[0];
            String str_new=strings[1];
            String str_user=strings[2];
            HttpURLConnection conn;
            URL url;
            String url_S;
            url_S=MainActivity.URL_ADDR.concat("changepass.php");
            try {
                url=new URL(url_S);
                conn=(HttpURLConnection)url.openConnection();
                conn.setReadTimeout(MainActivity.READ_TIMEOUT);
                conn.setConnectTimeout(MainActivity.CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                String data= URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(str_user,"UTF-8")+"&"
                        +URLEncoder.encode("currentP","UTF-8")+"="+URLEncoder.encode(str_cur,"UTF-8")+"&"
                        +URLEncoder.encode("newP","UTF-8")+"="+URLEncoder.encode(str_new,"UTF-8");
                OutputStreamWriter wr=new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                return reader.readLine();
            }catch (Exception e)
            {
                e.printStackTrace();
                return "exception";
            }
        }
        @Override
        protected void onPostExecute(String result)
        {
            Log.v("cpass",result);
            pd.dismiss();
            if(result.equals("exception"))
            {
                Toast.makeText(ctx,"Some Error While Updating.Try Again Later.",Toast.LENGTH_SHORT).show();
            }
            else if(Integer.parseInt(result)==0)
            {
                Toast.makeText(ctx,"Dewtails Are Incorrect.",Toast.LENGTH_SHORT).show();
            }
            else if(Integer.parseInt(result)==2)
            {
                Toast.makeText(ctx,"Cant update right now.",Toast.LENGTH_SHORT).show();
            }
            else if(Integer.parseInt(result)==1)
            {
                Toast.makeText(ctx,"successfully updated.",Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor ed=sp.edit();
                ed.remove("user");
                ed.remove("priority");
                ed.apply();
                finish();
                Intent intent1 = new Intent(ctx, MainActivity.class);
                startActivity(intent1);
            }
        }
    }
}
