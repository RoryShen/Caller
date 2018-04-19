package com.ck.testassistant.activity;



import com.ck.testassistant.R;
import com.ck.testassistant.service.InComingCallService;
import com.ck.testassistant.utils.PermissionUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;




/**
 * Created by Rory on 2017/11/08   .
 */

public class AnswerActivity extends Activity {
    private SharedPreferences callSharePreferences;
    TextView number;
    String phoneNumber;
    TextView waitTime;
    Button start;
    private static Activity answerActivity;
    private static final int REQUEST_CODE = 0; // 请求码
    private PermissionUtils mPermissionsChecker; // 权限检测器
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CALL_PHONE
    };

    public static Activity getAnswerActivity() {
        return answerActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        answerActivity = this;
        super.onCreate(savedInstanceState);
        mPermissionsChecker = new PermissionUtils(this);
        setContentView(R.layout.activity_answer);
        callSharePreferences = getSharedPreferences("config", MODE_PRIVATE);

        number = findViewById(R.id.et_an_phone_ref_num);

        waitTime = findViewById(R.id.et_an_wait);
        start = findViewById(R.id.bt_an_start);
        number.setText(callSharePreferences.getString("anNumber", ""));
        waitTime.setText(callSharePreferences.getInt("anTimes", 10) + "");
        boolean anOn = callSharePreferences.getBoolean("anOn", false);
        if (anOn) {
            start.setEnabled(false);
            waitTime.setEnabled(false);
            number.setEnabled(false);
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (!mPermissionsChecker.hasPermissions(PERMISSIONS)) {
            AlertDialog.Builder mBuilderNotes = new AlertDialog.Builder(this);
            mBuilderNotes.setTitle("权限缺失");
            mBuilderNotes.setMessage("需要开启电话权限才能正常使用功能哦！");
            mBuilderNotes.setPositiveButton("现在开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startPermissionsActivity();
                }
            });
            mBuilderNotes.setNegativeButton("我放弃！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent mainIntent = new Intent(AnswerActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            });
            mBuilderNotes.show();

        }
        boolean anOn = callSharePreferences.getBoolean("anOn", false);
        if (anOn) {
            start.setEnabled(false);
            waitTime.setEnabled(false);
            number.setEnabled(false);
        }

    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    public void Start(View view) {

        phoneNumber = number.getText().toString();
        int time = Integer.parseInt(waitTime.getText().toString());
        Intent inComingCall = new Intent(AnswerActivity.this, InComingCallService.class);
        inComingCall.putExtra("phone", phoneNumber);
        inComingCall.putExtra("frequency", time);
        callSharePreferences.edit().putString("anNumber", phoneNumber).apply();
        callSharePreferences.edit().putInt("anTimes", time).apply();
        callSharePreferences.edit().putBoolean("anOn", true).apply();
        number.setEnabled(false);
        waitTime.setEnabled(false);
        start.setEnabled(false);
        startService(inComingCall);
    }

    public void Stop(View view) {
        Intent inComingCall = new Intent(AnswerActivity.this, InComingCallService.class);
        stopService(inComingCall);
        number.setEnabled(true);
        waitTime.setEnabled(true);
        start.setEnabled(true);
        callSharePreferences.edit().putBoolean("anON", false).apply();
    }
}
