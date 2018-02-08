package com.ck_telecom.caller.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.ck_telecom.caller.R;
import com.ck_telecom.caller.service.CallService;
import com.ck_telecom.caller.utils.CallUtils;
import com.ck_telecom.caller.utils.PermissionUtils;

import static android.widget.Toast.LENGTH_SHORT;
import static com.ck_telecom.caller.R.id.et_ca_frequency;
import static com.ck_telecom.caller.R.id.et_ca_phone_ref_num;
import static com.ck_telecom.caller.R.id.et_ca_total;

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
    private static TextView tvTotal;
    private static TextView tvLog;
    private static TextView tvFail;
    private static TextView tvPass;
    private static EditText phone;
    private static EditText etTimes;
    private static EditText etFrequency;
    private static String phoneNum;
    private int times;
    private int frequency;
    private static Activity callActivity;


    public static Activity getCallActivity() {
        return callActivity;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        callActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        mPermissionsChecker = new PermissionUtils(this);
        callSharePreferences = getSharedPreferences("config", MODE_PRIVATE);
        EditText et_phone = findViewById(et_ca_phone_ref_num);
        et_phone.setText(callSharePreferences.getString("phoneNum", ""));
        tvTotal = findViewById(R.id.tv_ca_total_num);
        tvLog = findViewById(R.id.tv_ca_log);
        tvFail = findViewById(R.id.tv_ca_fail_num);
        tvLog.setTextSize(20);
        tvLog.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tvLog.setText(getString(R.string.notices));
        tvPass = findViewById(R.id.tv_ca_pass_num);
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


        callSharePreferences.edit().putString("phoneNum", phoneNum).apply();
        Intent callIntent = new Intent(CallActivity.this, CallService.class);
        callIntent.putExtra("phone", phoneNum);
        callIntent.putExtra("times", times);
        callIntent.putExtra("frequency", frequency);
        Log.e("Call,Times Service", times + "");
        Log.e("Call,frequency Service", frequency + "");
        //判断号码是否符合规则。
        if (phoneNum.isEmpty() || phoneNum.length() < 5) {
            Toast.makeText(this, "请至少输入5位数字！", Toast.LENGTH_SHORT).show();
            tvLog.setText("号码异常，请重试！");
        } else if (CallUtils.checkNumber(phoneNum)) {
            {
                phone.setEnabled(false);
                etTimes.setEnabled(false);
                etFrequency.setEnabled(false);

                startService(callIntent);
            }

        } else {
            Toast.makeText(this, "请检查您输入的号码，并重新输入。", LENGTH_SHORT).show();
            //tvLog.setText("请检查您输入的号码，并重新输入");

        }
    }

    public void Call(View view) {
        phone = findViewById(et_ca_phone_ref_num);
        phoneNum = phone.getText().toString();
        etTimes = findViewById(et_ca_total);
        times = Integer.parseInt(etTimes.getText().toString());
        etFrequency = findViewById(et_ca_frequency);
        frequency = Integer.parseInt(etFrequency.getText().toString());
        tvTotal.setText(times + "");
        tvFail.setText(times + "");
        tvLog.setText(getString(R.string.testing));
        tvLog.setTextSize(40);
        tvLog.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        tvPass.setText(0 + "");
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
        phone.setEnabled(true);
        etTimes.setEnabled(true);
        etFrequency.setEnabled(true);

        Intent callIntent = new Intent(CallActivity.this, CallService.class);
        stopService(callIntent);

    }
}
