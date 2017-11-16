package com.ck_telecom.caller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Rory on 2017/11/08   .
 */

public class AnswerActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    public void Start(View view) {
        TextView textView = findViewById(R.id.et_phone_ref_num);
        String phoneNumber = textView.getText().toString();
        Intent inComingCall = new Intent(AnswerActivity.this, InComingCallService.class);
        inComingCall.putExtra("phone", phoneNumber);
        startService(inComingCall);
    }

    public void Stop(View view) {
        Intent inComingCall = new Intent(AnswerActivity.this, InComingCallService.class);
        stopService(inComingCall);
    }
}
