package com.ck_telecom.caller.activity;

import com.ck_telecom.caller.R;
import com.ck_telecom.caller.utils.ContactsUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

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

    private String smsType = "1";
    private String smsStatus = "1";


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
        if (isNotices && !defaultSmsApp.equals(currentSmsApp)) {
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
        }
        initView();

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

        btInsert.setOnClickListener(this);
        mrdSmsStatus.setOnCheckedChangeListener(this);
        mrdSmsType.setOnCheckedChangeListener(this);
        // disableUIElement();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sms_notices:
                isNotices = false;
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                startActivity(intent);


                break;
            case R.id.bt_sms_insert:
                Log.d("RDebug", "SMSType:" + smsType + ",Sms Status:" + smsStatus);
                break;

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


        }

    }

    private void disableUIElement() {
        etPhoneNumber.setEnabled(false);
        etNumber.setEnabled(false);
        btInsert.setEnabled(false);
        btDelete.setEnabled(false);
        ContactsUtils.disableRadioGroup(mrdSmsStatus);
        ContactsUtils.disableRadioGroup(mrdSmsType);
    }

    private void ensableUIElement() {
        etPhoneNumber.setEnabled(true);
        etNumber.setEnabled(true);
        btInsert.setEnabled(true);
        btDelete.setEnabled(true);
        ContactsUtils.enableRadioGroup(mrdSmsStatus);
        ContactsUtils.enableRadioGroup(mrdSmsType);
    }


}
