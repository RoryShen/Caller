package com.ck_telecom.caller.activity;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ck_telecom.caller.R;
import com.ck_telecom.caller.activity.AnswerActivity;
import com.ck_telecom.caller.activity.CallActivity;
import com.ck_telecom.caller.activity.ContactsActivity;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    public void answer(View view) {
        Intent intent = new Intent(this, AnswerActivity.class);
        startActivityForResult(intent, 1);
        finish();
    }

    public void Call(View view) {
        Intent CallIntent = new Intent(this, CallActivity.class);
        startActivityForResult(CallIntent, 0);
        finish();
    }


    public void contacts(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivityForResult(intent, 1);
        finish();
    }
}
