package com.example.fathima.tomton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainWindow extends AppCompatActivity {
    protected Context ctx;
    protected SharedPreferences sp;
    protected RelativeLayout rlassign,rlupdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        sp=this.getSharedPreferences("Login", 0);
        int prio=sp.getInt("priority",0);
        rlassign=(RelativeLayout)findViewById(R.id.rlassign);
        rlupdate=(RelativeLayout)findViewById(R.id.rlupdate);
        if(prio==1)
        {
            rlupdate.setVisibility(View.GONE);
            rlassign.setVisibility(View.VISIBLE);
        }
        else
        {
            rlassign.setVisibility(View.GONE);
            rlupdate.setVisibility(View.VISIBLE);
        }
        ctx=this;
    }
    public void markFunction(View view)
    {
        Intent i=new Intent(ctx,teacherLogin.class);
        startActivity(i);
    }
    public void viewFunction(View view)
    {
        Intent i=new Intent(ctx,ViewAtt.class);
        startActivity(i);
    }
    public void updateFunction(View view)
    {
        Intent i=new Intent(ctx,UpdateActivity.class);
        startActivity(i);
    }
    public void indiFunction(View view)
    {
        Intent i=new Intent(ctx,IndividualPro.class);
        startActivity(i);
    }
    public void messageFunction(View view)
    {
        Toast.makeText(ctx,"Service Unavailable",Toast.LENGTH_SHORT).show();
    }
    public void passwordFunction(View view)
    {
        Intent i=new Intent(ctx,ChangePass.class);
        startActivity(i);
    }
    public void contactFunction(View view)
    {
        Intent i=new Intent(ctx,ContactActivity.class);
        startActivity(i);
    }
    public void logoutFunction(View view)
    {
        new AlertDialog.Builder(ctx)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Do You Really Want To Logout ?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences=getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        editor.apply();
                        finish();
                        Intent intent1 = new Intent(MainWindow.this, MainActivity.class);
                        startActivity(intent1);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
