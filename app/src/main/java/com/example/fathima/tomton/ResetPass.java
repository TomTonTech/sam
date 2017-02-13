package com.example.fathima.tomton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 2/10/2017.
 */

public class ResetPass extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        context=this;
    }
    public void onBackPressed() {
        Intent myIntent = new Intent(ResetPass.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
        startActivity(myIntent);
        finish();
        return;
    }
}
