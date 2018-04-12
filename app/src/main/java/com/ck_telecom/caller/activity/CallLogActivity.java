package com.ck_telecom.caller.activity;

import com.ck_telecom.caller.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CallLogActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etPphoneNumber;
    private EditText etLogNum;
    private Spinner spLogType;
    private Button btLogInsert;
    private Button btLogDelete;
    private TextView tvLogTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        etPphoneNumber = findViewById(R.id.et_log_phone_num);
        etLogNum = findViewById(R.id.et_log_num);
        spLogType = findViewById(R.id.sp_log_type);
        btLogInsert = findViewById(R.id.bt_log_insert);
        btLogDelete = findViewById(R.id.bt_log_delete);
        tvLogTotal = findViewById(R.id.tv_log_total);

        //Set click listener.
        btLogInsert.setOnClickListener(this);
        btLogDelete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Toast.makeText(this,v.getId()+"",Toast.LENGTH_SHORT).show();


    }
}
