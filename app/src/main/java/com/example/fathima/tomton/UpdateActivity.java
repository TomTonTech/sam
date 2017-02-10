package com.example.fathima.tomton;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class UpdateActivity extends AppCompatActivity {
    protected static Button btnDate,btnAtt,btnOth,btnMsg,btnView,btnAttView;
    protected static EditText et_remark,et_cDate,et_cPeriod,et_nDate,et_nPeriod,etAttDate,etAttPeriod,etAttSubject,etAttClass,etAttAttendance;
    protected static TextView tv_remark;
    protected static RelativeLayout rld,rla,rlo,rrl;
    protected static Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ctx=this;
        btnDate=(Button)findViewById(R.id.btn_date_correction);
        btnAtt=(Button)findViewById(R.id.btn_att_correction);
        btnView=(Button)findViewById(R.id.btn_view_message);
        et_remark=(EditText)findViewById(R.id.et_remarks);
        tv_remark=(TextView)findViewById(R.id.tv_remarks);
        et_cDate=(EditText)findViewById(R.id.et_corr_date);
        et_cPeriod=(EditText)findViewById(R.id.et_corr_period);
        et_nDate=(EditText)findViewById(R.id.et_new_date);
        et_nPeriod=(EditText)findViewById(R.id.et_new_period);
        rld=(RelativeLayout)findViewById(R.id.dateCRL);
        rrl=(RelativeLayout)findViewById(R.id.remarkRL);
        /*Att correction view button and edittext declaration*/
        rla=(RelativeLayout)findViewById(R.id.attCRL);
        etAttDate=(EditText)findViewById(R.id.et_corr_att_date);
        etAttPeriod=(EditText)findViewById(R.id.et_corr_att_period);
        etAttSubject=(EditText)findViewById(R.id.et_att_subject);
        etAttClass=(EditText)findViewById(R.id.et_att_class);
        etAttAttendance=(EditText)findViewById(R.id.et_att_attendance);
        btnAttView=(Button) findViewById(R.id.btn_att_view_message);


        /*btn click listeners*/
        btnDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rla.setVisibility(View.GONE);
                        rrl.setVisibility(View.GONE);
                        rld.setVisibility(View.VISIBLE);
                        et_remark.setText("");
                    }
                }
        );
        btnAtt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rla.setVisibility(View.VISIBLE);
                        rld.setVisibility(View.GONE);
                        rrl.setVisibility(View.GONE);
                        et_remark.setText("");
                    }
                }
        );
        btnView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String cDate=et_cDate.getText().toString();
                        String cPeriod=et_cPeriod.getText().toString();
                        String nDate=et_nDate.getText().toString();
                        String nPeriod=et_nPeriod.getText().toString();
                        if(!cDate.isEmpty()&&!cPeriod.isEmpty()&&!nDate.isEmpty()&&!nPeriod.isEmpty())
                        {
                            rrl.setVisibility(View.VISIBLE);
                            et_remark.setText("");
                            String msg=getString(R.string.message_for_date_correction,cPeriod,cDate,nPeriod,nDate);
                            et_remark.setText(msg);
                        }
                        else
                        {
                            Toast.makeText(ctx,"Give Information In All Fields",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        btnAttView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strAttDate= etAttDate.getText().toString();
                        String strAttPeriod=etAttPeriod.getText().toString();
                        String strAttSubject=etAttSubject.getText().toString();
                        String strAttClass=etAttClass.getText().toString();
                        String strAttAttend=etAttAttendance.getText().toString();
                        if(!strAttDate.isEmpty()&&!strAttPeriod.isEmpty()&&!strAttAttend.isEmpty()&&!strAttClass.isEmpty()&&!strAttSubject.isEmpty())
                        {
                            String strSem=strAttClass.split(":")[0];
                            String strDiv=strAttClass.split(":")[1];
                            rrl.setVisibility(View.VISIBLE);
                            et_remark.setText("");
                            String msg=getString(R.string.message_for_attendance_correction,strAttDate,strAttPeriod,strSem,strDiv,strAttSubject,strAttAttend);
                            et_remark.setText(msg);
                        }
                        else
                        {
                            Toast.makeText(ctx,"Give All The Information To Continue",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
