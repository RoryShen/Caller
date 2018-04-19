package com.ck.testassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.ck.testassistant.R;
import com.ck.testassistant.utils.BaseUtils;

public class MmsActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private static Button mbtInsert;
    private static Button mbtClear;
    private static EditText metPhone;
    private static EditText metNumber;
    private static EditText metBody;
    private static RadioGroup mrdMedia;
    private static RadioGroup mrdStatus;
    private static RadioGroup mrdType;
    private static RadioGroup mrdRondom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mms);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rd_mms_random:
                random();
                break;
            case R.id.rd_mms_custom:
                custom();
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        mbtClear = findViewById(R.id.bt_mms_clear);
        mbtInsert = findViewById(R.id.bt_mms_insert);
        metBody = findViewById(R.id.et_mms_body);
        metNumber = findViewById(R.id.et_mms_number);
        metPhone = findViewById(R.id.et_mms_phone);
        mrdMedia = findViewById(R.id.rd_mms_media);
        mrdStatus = findViewById(R.id.rd_mms_status);
        mrdType = findViewById(R.id.rd_mms_type);
        mrdRondom = findViewById(R.id.rd_mms_isRandom);

        mbtInsert.setOnClickListener(this);
        mbtClear.setOnClickListener(this);
        mrdType.setOnCheckedChangeListener(this);
        mrdStatus.setOnCheckedChangeListener(this);
        mrdMedia.setOnCheckedChangeListener(this);
        mrdRondom.setOnCheckedChangeListener(this);


    }

    private void random() {
        metPhone.setVisibility(View.GONE);
        metBody.setVisibility(View.GONE);
        mrdMedia.setVisibility(View.GONE);
        mrdStatus.setVisibility(View.GONE);
        mrdType.setVisibility(View.GONE);


    }

    private void custom() {
        metBody.setVisibility(View.VISIBLE);
        mrdMedia.setVisibility(View.VISIBLE);
        mrdStatus.setVisibility(View.VISIBLE);
        mrdType.setVisibility(View.VISIBLE);
        metPhone.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

    }

    private void disableElement() {
        metPhone.setEnabled(false);
        metBody.setEnabled(false);
        metNumber.setEnabled(false);
        mbtClear.setEnabled(false);
        mbtInsert.setEnabled(false);
        BaseUtils.disableRadioGroup(mrdMedia);
        BaseUtils.disableRadioGroup(mrdRondom);
        BaseUtils.disableRadioGroup(mrdStatus);
        BaseUtils.disableRadioGroup(mrdType);
    }
}
