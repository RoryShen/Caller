package com.ck.testassistant.activity;



import com.ck.testassistant.R;
import com.ck.testassistant.utils.BaseUtils;
import com.ck.testassistant.utils.MessageUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private SharedPreferences mSharedPreferences;
    private boolean isNotices = true;
    private static EditText etPhoneNumber;
    private static EditText etNumber;
    private static Button btInsert;
    private static Button btDelete;
    private static TextView tvSmsNumber;
    private static RadioGroup mrdSmsType;
    private static RadioGroup mrdSmsStatus;
    private static RadioGroup mrdSmsIsRandom;
    private static EditText etSmsBody;
    private static ProgressBar sProgressBar;
    private Handler mHandler;
    private boolean display = false;


    private static int messageNumber;
    private static String messageAddress;
    private static String messageBody;
    private static insertMessageThread insertMessageThread;
    private static deleteMessageThread deleteMessageThread;

    private String smsType = "1";
    private String smsStatus = "1";
    private boolean isRandom = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);



    }

    @Override
    protected void onResume() {
        super.onResume();

        String currentSmsApp = getPackageName();
        String defaultSmsApp;
        defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        boolean isDefaultSms = defaultSmsApp.equals(currentSmsApp);
        if (isNotices && !isDefaultSms) {
            AlertDialog.Builder mNotices = new AlertDialog.Builder(this);
            mNotices.setTitle("更改默认短信程序?");
            mNotices.setMessage("请先把本程序设置为默认短信APP，才可以进行短信填充哦！");
            mNotices.setPositiveButton("现在设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String currentSmsApp = getPackageName();
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currentSmsApp);
                    startActivity(intent);

                }
            });
            mNotices.setNegativeButton("放弃设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent mainIntent = new Intent(MessageActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                }
            });
            mNotices.show();

        } else {
            initView();
        }
        Log.d("RDebug", "default app:" + isDefaultSms);
        // Log.d("RDebug","Read SMS:"+mPermissionChecker.hasPermissions(PERMISSIONS));


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (etPhoneNumber != null) {
            etPhoneNumber = null;
        }
        if (etNumber != null) {
            etNumber = null;
        }
        if (btDelete != null) {
            btDelete = null;
        }
        if (btInsert != null) {
            btInsert = null;
        }
        if (tvSmsNumber != null) {
            tvSmsNumber = null;
        }
        if (mrdSmsStatus != null) {
            mrdSmsStatus = null;
        }
        if (mrdSmsType != null) {
            mrdSmsType = null;
        }
        if (mrdSmsIsRandom != null) {
            mrdSmsIsRandom = null;
        }
        if (etSmsBody != null) {
            etSmsBody = null;
        }
        if (sProgressBar != null) {
            sProgressBar = null;
        }


    }

    private void initView() {
        TextView tvSmsNotices = findViewById(R.id.tv_sms_notices);
        tvSmsNotices.setOnClickListener(this);
        etNumber = findViewById(R.id.et_sms_number);
        etPhoneNumber = findViewById(R.id.et_sms_phoneNum);
        mrdSmsType = findViewById(R.id.rd_sms_smsType);
        mrdSmsStatus = findViewById(R.id.rd_sms_smsStatus);
        btDelete = findViewById(R.id.bt_sms_delete);
        btInsert = findViewById(R.id.bt_sms_insert);
        mrdSmsIsRandom = findViewById(R.id.rd_sms_isRandom);
        etSmsBody = findViewById(R.id.et_sms_body);
        sProgressBar = findViewById(R.id.pr_sms_process);
        tvSmsNumber = findViewById(R.id.tv_sms_total_num);

        btInsert.setOnClickListener(this);
        btDelete.setOnClickListener(this);
        mrdSmsStatus.setOnCheckedChangeListener(this);
        mrdSmsType.setOnCheckedChangeListener(this);
        mrdSmsIsRandom.setOnCheckedChangeListener(this);
        // disableUIElement();
        tvSmsNumber.setText(MessageUtils.getAllSmsNumber() + "");
        if (null != insertMessageThread && insertMessageThread.isAlive()
                ||null!=deleteMessageThread&&deleteMessageThread.isAlive()) {
            disableUIElement();
        }


    }

    @Override
    public void onClick(View v) {
        mHandler = new messageHandler();
        switch (v.getId()) {
            case R.id.tv_sms_notices:
                isNotices = false;
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                startActivity(intent);


                break;
            case R.id.bt_sms_insert:
                Log.d("RDebug", "SMSType:" + smsType + ",Sms Status:" + smsStatus);

                if (TextUtils.isEmpty(etNumber.getText().toString())) {
                    Toast.makeText(this, "插入数量为空哦！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    messageNumber = Integer.valueOf(etNumber.getText().toString());
                }
                if (!isRandom) {
                    messageAddress = etPhoneNumber.getText().toString();
                    if (TextUtils.isEmpty(etSmsBody.getText().toString())) {
                        Toast.makeText(this, "短信内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        messageBody = etSmsBody.getText().toString();
                    }


                }
                insertMessageThread = new insertMessageThread();
                insertMessageThread.start();
                disableUIElement();
                break;
            case R.id.bt_sms_delete:
                deleteMessageThread = new deleteMessageThread();
                deleteMessageThread.start();
                disableUIElement();

        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

            case R.id.rd_sms_receive:
                smsType = "1";
                break;
            case R.id.rd_sms_send:
                smsType = "2";
                break;
            case R.id.rd_sms_read:
                smsStatus = "1";
                break;
            case R.id.rd_sms_unread:
                smsStatus = "0";
                break;
            case R.id.bt_sms_delete:
                MessageUtils.clearMessage();
                break;
            case R.id.rd_sms_random:
                isRandom = true;
                isRandom();
                break;
            case R.id.rd_sms_notRandom:
                isRandom = false;

                notRandom();
                break;


        }

    }

    private void disableUIElement() {
        etPhoneNumber.setEnabled(false);
        etNumber.setEnabled(false);
        btInsert.setEnabled(false);
        btDelete.setEnabled(false);
        etSmsBody.setEnabled(false);
        BaseUtils.disableRadioGroup(mrdSmsStatus);
        BaseUtils.disableRadioGroup(mrdSmsType);
        BaseUtils.disableRadioGroup(mrdSmsIsRandom);
        sProgressBar.setVisibility(View.VISIBLE);
    }

    private void enableUIElement() {
        etPhoneNumber.setEnabled(true);
        etNumber.setEnabled(true);
        btInsert.setEnabled(true);
        btDelete.setEnabled(true);
        etSmsBody.setEnabled(true);

        BaseUtils.enableRadioGroup(mrdSmsStatus);
        BaseUtils.enableRadioGroup(mrdSmsType);
        BaseUtils.enableRadioGroup(mrdSmsIsRandom);
        sProgressBar.setVisibility(View.GONE);

    }

    private void notRandom() {
        etSmsBody.setVisibility(View.VISIBLE);
        etPhoneNumber.setVisibility(View.VISIBLE);
        mrdSmsType.setVisibility(View.VISIBLE);
        mrdSmsStatus.setVisibility(View.VISIBLE);


    }

    private void isRandom() {

        etSmsBody.setVisibility(View.GONE);
        etPhoneNumber.setVisibility(View.GONE);
        mrdSmsType.setVisibility(View.GONE);
        mrdSmsStatus.setVisibility(View.GONE);
    }

    private class insertMessageThread extends Thread {
        @Override
        public void run() {
            Log.d("RDebug", "Start message insert");
            super.run();
            int currentMessageNumber = 0;
            while (currentMessageNumber < messageNumber) {
                if (isRandom) {
                    MessageUtils.insertMessage();
                } else {
                    MessageUtils.insertMessage(messageAddress, smsType, smsStatus, messageBody);
                }
                Log.d("RDebug", "Start  insert" + currentMessageNumber + "message");

                currentMessageNumber++;
            }
            mHandler.sendEmptyMessage(1);
            Log.d("RDebug", "Done");
        }
    }

    private class deleteMessageThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (MessageUtils.getAllSmsNumber() == MessageUtils.clearMessage()) {
                mHandler.sendEmptyMessage(2);
            }


        }
    }

    private class messageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.d("RDebug", "Insert done");
                    Toast.makeText(MessageActivity.this, "短信插入完毕！", Toast.LENGTH_SHORT).show();
                    enableUIElement();
                    tvSmsNumber.setText(MessageUtils.getAllSmsNumber() + "");
                    Log.d("RDebug", MessageUtils.getAllSmsNumber() + "");
                    break;
                case 2:
                    enableUIElement();
                    Toast.makeText(MessageActivity.this, "短信已清空！", Toast.LENGTH_SHORT).show();
                    tvSmsNumber.setText(MessageUtils.getAllSmsNumber() + "");

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
    }
}
