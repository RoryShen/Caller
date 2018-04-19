package com.ck.testassistant.activity;

import com.ck.testassistant.R;
import com.ck.testassistant.utils.BaseUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static Context mContext;
    private Button mContacts;
    private Button mAnswer;
    private Button mDialing;
    private Button mCallLog;
    private Button mSms;
    private TextView mtvVersionCode;
    private TextView mtvVersionName;
    private Button mbtMms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();

    }

    private void initView() {
        mContacts = findViewById(R.id.bt_main_contacts);
        mDialing = findViewById(R.id.bt_main_call);
        mAnswer = findViewById(R.id.bt_main_answer);
        mCallLog = findViewById(R.id.bt_main_log);
        mSms = findViewById(R.id.bt_main_sms);
        mtvVersionCode = findViewById(R.id.tv_main_versionCode);
        mtvVersionName = findViewById(R.id.tv_main_versionName);
        mbtMms=findViewById(R.id.bt_main_mms);
        try {
            mtvVersionCode.setText("Version:" + BaseUtils.getVersion(mContext));
            mtvVersionName.setText(BaseUtils.getVersionName(mContext));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        mContacts.setOnClickListener(this);
        mDialing.setOnClickListener(this);
        mAnswer.setOnClickListener(this);
        mCallLog.setOnClickListener(this);
        mSms.setOnClickListener(this);
        mbtMms.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_main_answer:
                Intent mAnswerIntent = new Intent(this, AnswerActivity.class);
                startActivityForResult(mAnswerIntent, 1);
                finish();
                break;
            case R.id.bt_main_contacts:
                Intent mContactsIntent = new Intent(this, ContactsActivity.class);
                startActivityForResult(mContactsIntent, 1);
                finish();
                break;
            case R.id.bt_main_call:
                Intent callIntent = new Intent(this, CallActivity.class);
                startActivityForResult(callIntent, 0);
                finish();
                break;
            case R.id.bt_main_log:
                Intent logIntent = new Intent(this, CallLogActivity.class);
                startActivityForResult(logIntent, 0);
                finish();
                break;
            case R.id.bt_main_sms:
                Intent smsIntent = new Intent(this, MessageActivity.class);
                startActivityForResult(smsIntent, 0);
                finish();
                break;
            case R.id.bt_main_mms:
                Intent mmsIntent = new Intent(this, MmsActivity.class);
                startActivityForResult(mmsIntent, 0);
                finish();
                break;
        }

    }

}
