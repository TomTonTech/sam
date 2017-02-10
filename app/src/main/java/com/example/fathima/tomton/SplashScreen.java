package com.example.fathima.tomton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    protected ImageView iv;
    protected Button btn;
    protected TextView et_head,et_manager;
    protected Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        iv=(ImageView)findViewById(R.id.iv_splash);
        btn=(Button)findViewById(R.id.btn_start);
        ctx=this;
        et_head=(TextView)findViewById(R.id.textView17);
        et_manager=(TextView)findViewById(R.id.textView18);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/Quantify-Bold.ttf");
        et_head.setTypeface(face);
        et_head.setAllCaps(true);
        et_manager.setAllCaps(true);
        et_manager.setTypeface(face);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.clockwise);
        iv.startAnimation(animation1);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences=ctx.getSharedPreferences("Login",0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("firsttime", 1);
                        editor.apply();
                        editor.commit();
                        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

    }
}
