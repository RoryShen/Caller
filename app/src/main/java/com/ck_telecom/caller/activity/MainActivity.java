package com.ck_telecom.caller.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ck_telecom.caller.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static Context mContext;
    private Button mContacts;
    private Button mAnswer;
    private Button mDialing;


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


        mContacts.setOnClickListener(this);
        mDialing.setOnClickListener(this);
        mAnswer.setOnClickListener(this);
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
                Intent CallIntent = new Intent(this, CallActivity.class);
                startActivityForResult(CallIntent, 0);
                finish();
        }

    }
}
