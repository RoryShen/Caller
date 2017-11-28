package com.ck_telecom.caller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static com.ck_telecom.caller.R.id.et_frequency;
import static com.ck_telecom.caller.R.id.et_phone_ref_num;
import static com.ck_telecom.caller.R.id.et_total;

/**
 * Created by Rory on 2017/11/08   .
 */

public class CallActivity extends Activity {
    private SharedPreferences callSharePreferences;
    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CALL_PHONE
    };

    private PermissionUtils mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        mPermissionsChecker = new PermissionUtils(this);
        callSharePreferences = getSharedPreferences("config", MODE_PRIVATE);
        EditText et_phone = findViewById(et_phone_ref_num);
        et_phone.setText(callSharePreferences.getString("phoneNum", ""));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.hasPermissions(PERMISSIONS)) {
            AlertDialog.Builder mBuilderNotes = new AlertDialog.Builder(this);
            mBuilderNotes.setTitle("权限缺失");
            mBuilderNotes.setMessage("需要开启电话权限才能正常使用功能哦！");
            mBuilderNotes.setPositiveButton("开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startPermissionsActivity();
                }
            });
            mBuilderNotes.setNegativeButton("算了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent mainIntent = new Intent(CallActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            });
            mBuilderNotes.show();

        }

    }


    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    private void startCallService() {


        EditText phone = findViewById(et_phone_ref_num);
        String phoneNum = phone.getText().toString();
        EditText etTimes = findViewById(et_total);
        int times = Integer.parseInt(etTimes.getText().toString());
        EditText etFrequency = findViewById(et_frequency);
        int frequency = Integer.parseInt(etFrequency.getText().toString());

        callSharePreferences.edit().putString("phoneNum", phoneNum).apply();
        Intent callIntent = new Intent(CallActivity.this, CallService.class);
        callIntent.putExtra("phone", phoneNum);
        callIntent.putExtra("times", times);
        callIntent.putExtra("frequency", frequency);
        Log.e("Call,Times Service", times + "");
        Log.e("Call,frequency Service", frequency + "");
        startService(callIntent);

    }

    public void Call(View view) {

        startCallService();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    public void Stop(View view) {
        Intent callIntent = new Intent (CallActivity.this, CallService.class);
        stopService (callIntent);
    }
}
