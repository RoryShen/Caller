package com.ck_telecom.caller.activity;

import com.ck_telecom.caller.R;
import com.ck_telecom.caller.utils.BaseUtils;
import com.ck_telecom.caller.utils.ContactsUtils;
import com.ck_telecom.caller.utils.PermissionUtils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class CallLogActivity extends AppCompatActivity implements View.OnClickListener {

    //Ui Element
    private static EditText etPphoneNumber;
    private static EditText etLogNum;
    private static Button btLogInsert;
    private static Button btLogDelete;
    private static TextView tvLogTotal;
    private static RadioButton rdAnswer;
    private static RadioButton rdMiss;
    private static RadioButton rdDial;
    private static RadioGroup rdgType;
    private static ProgressBar prLogProcess;


    // internet label.
    private int logType = 3;
    private String mPhoneNumber;
    private int mLogNumber;
    private PermissionUtils mPermissionsChecker; // permission checker.
    private static final int REQUEST_CODE = 0; // 请求码
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG
    };
    private static boolean hasUpdate = false;

    private Message message;
    private Handler mHandler;
    private SharedPreferences mCallerData;

    //Thread.
    private static insertLogThread mInsertLogThread;
    private static deleteLogThread mDeleteLogThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        mPermissionsChecker = new PermissionUtils(this);
        mCallerData = getSharedPreferences("CallerData", MODE_PRIVATE);

    }


    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (!mPermissionsChecker.hasPermissions(PERMISSIONS)) {
            AlertDialog.Builder mBuilderNotes = new AlertDialog.Builder(this);
            mBuilderNotes.setTitle("权限不足");
            mBuilderNotes.setMessage("进行通话记录填充，需要先开启通话记录读写权限的哦！");
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
                    Intent mainIntent = new Intent(CallLogActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            });
            mBuilderNotes.show();

        } else {
            initView();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (hasUpdate) {
            SharedPreferences.Editor logEditor = mCallerData.edit();

            logEditor.putString("logPhoneNumber", mPhoneNumber );

            logEditor.putString("logLogNumber", mLogNumber + "");
            logEditor.apply();
            hasUpdate = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (etPphoneNumber != null) {
            etPphoneNumber = null;
        }
        if (etLogNum != null) {
            etLogNum = null;
        }
        if (btLogInsert != null) {
            btLogInsert = null;
        }
        if (btLogDelete != null) {
            btLogDelete = null;
        }
        if (tvLogTotal != null) {
            tvLogTotal = null;
        }
        if (rdAnswer != null) {
            rdAnswer = null;
        }
        if (rdMiss != null) {
            rdMiss = null;
        }
        if (rdDial != null) {
            rdDial = null;
        }
        if (rdgType != null) {
            rdgType = null;
        }
        if (prLogProcess != null) {
            prLogProcess = null;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    private void initView() {

        etPphoneNumber = findViewById(R.id.et_log_phone_num);
        etLogNum = findViewById(R.id.et_log_num);

        btLogInsert = findViewById(R.id.bt_log_insert);
        btLogDelete = findViewById(R.id.bt_log_delete);
        tvLogTotal = findViewById(R.id.tv_log_total);
        rdAnswer = findViewById(R.id.rd_log_answer);
        rdDial = findViewById(R.id.rd_log_dial);
        rdMiss = findViewById(R.id.rd_log_miss);
        etLogNum = findViewById(R.id.et_log_num);
        etPphoneNumber = findViewById(R.id.et_log_phone_num);
        rdgType = findViewById(R.id.rd_log_type);
        tvLogTotal = findViewById(R.id.tv_log_total_num);
        prLogProcess = findViewById(R.id.pr_log_process);


        if (null != mInsertLogThread && mInsertLogThread.isAlive()) {
            disableUiElement();
        } else if (null != mDeleteLogThread && mDeleteLogThread.isAlive()) {
            disableUiElement();
        }

        etPphoneNumber.setText(mCallerData.getString("logPhoneNumber", ""));
        etLogNum.setText(mCallerData.getString("logLogNumber", ""));
        //Set click listener.
        btLogInsert.setOnClickListener(this);
        btLogDelete.setOnClickListener(this);
        rdMiss.setOnClickListener(this);
        rdAnswer.setOnClickListener(this);
        rdDial.setOnClickListener(this);
        tvLogTotal.setText(ContactsUtils.getCallLogNumber() + "");


    }

    @Override
    public void onClick(View v) {
        mHandler = new MyHandler();
        switch (v.getId()) {
            case R.id.rd_log_answer:
                logType = 1;
                break;
            case R.id.rd_log_dial:
                logType = 2;
                break;
            case R.id.rd_log_miss:
                logType = 3;
                break;
            case R.id.bt_log_insert:
                insertLog();
                break;
            case R.id.bt_log_delete:
                deleteLog();
        }


    }

    private void insertLog() {
        String phoneNumber = etPphoneNumber.getText().toString();
        String logNumber = etLogNum.getText().toString();
        if (TextUtils.isEmpty(logNumber)) {
            Toast.makeText(this, "请检查输入的数量是否为空！", Toast.LENGTH_SHORT).show();
            return;
        } else if (null != mInsertLogThread && mInsertLogThread.isAlive()) {
            Toast.makeText(this, "请等待当前插入操作结束！", Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(phoneNumber)) {

                mPhoneNumber = BaseUtils.getRandPhone(new Random().nextInt(20)) ;

                etPphoneNumber.setText(mPhoneNumber);

            } else {
                mPhoneNumber = phoneNumber;
            }

            mLogNumber = Integer.parseInt(logNumber);
            if(mLogNumber>2000){
                Toast.makeText(this, "当前限制插入单次插入个数为2000！", Toast.LENGTH_SHORT).show();
                return;
            }else{
                Toast.makeText(this, "请稍后，正在进行Log写入。", Toast.LENGTH_SHORT).show();
                mInsertLogThread = new insertLogThread();
                mInsertLogThread.start();
                disableUiElement();
                hasUpdate = true;
            }


        }


    }


    private void deleteLog() {
        if (null != mDeleteLogThread && mDeleteLogThread.isAlive()) {
            Toast.makeText(this, "请等待当前删除执行完毕！", Toast.LENGTH_SHORT).show();
        } else {
            mDeleteLogThread = new deleteLogThread();
            mDeleteLogThread.start();
            disableUiElement();
            hasUpdate = true;
        }

    }


    private class insertLogThread extends Thread {
        int currentLog = 0;
        Random random = new Random();
        int time = 0;

        @Override
        public void run() {
            super.run();

            while (currentLog < mLogNumber) {
                if (logType != 3) {
                    time = random.nextInt(65535);

                }

                ContactsUtils.insertCallLog(mPhoneNumber, logType, time);
                currentLog++;


            }
            mHandler.sendEmptyMessage(1);


        }
    }

    private class deleteLogThread extends Thread {
        @Override
        public void run() {
            super.run();
            int totalLogNum = ContactsUtils.getCallLogNumber();
            int deleteLogNum = ContactsUtils.deleteCallLog();
            if (deleteLogNum == totalLogNum) {
                mHandler.sendEmptyMessage(2);
            }


        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(CallLogActivity.this, "通话记录写入完毕！",
                            Toast.LENGTH_SHORT).show();

                    enableUiElement();
                    tvLogTotal.setText(ContactsUtils.getCallLogNumber() + "");

                    break;
                case 2:
                    Toast.makeText(CallLogActivity.this, "通话记录已清空！",
                            Toast.LENGTH_SHORT).show();
                    tvLogTotal.setText(ContactsUtils.getCallLogNumber() + "");
                    enableUiElement();

                    break;

            }
        }

    }


    private void disableUiElement() {
        btLogInsert.setEnabled(false);
        btLogDelete.setEnabled(false);
        etLogNum.setEnabled(false);
        etPphoneNumber.setEnabled(false);
        BaseUtils.disableRadioGroup(rdgType);
        prLogProcess.setVisibility(View.VISIBLE);
    }

    private void enableUiElement() {
        btLogInsert.setEnabled(true);
        btLogDelete.setEnabled(true);
        etLogNum.setEnabled(true);
        etPphoneNumber.setEnabled(true);
        BaseUtils.enableRadioGroup(rdgType);
        prLogProcess.setVisibility(View.GONE);
    }
}
