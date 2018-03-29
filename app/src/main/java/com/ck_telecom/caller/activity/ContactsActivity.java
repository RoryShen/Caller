package com.ck_telecom.caller.activity;

import com.ck_telecom.caller.R;
import com.ck_telecom.caller.config.ContactsConfig;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class ContactsActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    String baseString;
    String charsContent = ContactsConfig.CHARS_SYMBOLS;
    String charsChinese = ContactsConfig.CHARS_CHINESE;
    String charsLowerChar = ContactsConfig.CHARS_LOWER_CHAR;
    String CharsUpChar = ContactsConfig.CHARS_UP_CHAR;
    private static EditText etNumber;
    private static ProgressBar progressBar;
    private static Thread InsertContactsThread;
    private static RadioButton mRadioMax;
    private PermissionUtils mPermissionsChecker; // 权限检测器
    int contacts;
    private static Button btInsert;
    private static RadioGroup radioGroup;
    private static boolean insertContacts = false;
    private Message message;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;

    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //清空对象，便于被回收
        if (etNumber != null) {
            etNumber = null;
        }
        if (progressBar != null) {
            etNumber = null;
        }
        if (mRadioMax != null) {
            mRadioMax = null;
        }
        if (btInsert != null) {
            btInsert = null;
        }

        if (radioGroup != null) {
            radioGroup = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mPermissionsChecker = new PermissionUtils(this);


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
    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.hasPermissions(PERMISSIONS)) {
            AlertDialog.Builder mBuilderNotes = new AlertDialog.Builder(this);
            mBuilderNotes.setTitle("权限不足");
            mBuilderNotes.setMessage("进行联系人填充，需要先开启联系人读写权限的哦！");
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
                    Intent mainIntent = new Intent(ContactsActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            });
            mBuilderNotes.show();

        }
        initView();
        mSharedPreferences = getSharedPreferences("InsertContacts", MODE_PRIVATE);
        boolean isInsert = mSharedPreferences.getBoolean("Inserting", false);
        String insertNumber = mSharedPreferences.getString("contactsNumber", "");
        int totalContacts = mSharedPreferences.getInt("AllContacts", 0);


        if (isInsert) {
            btInsert.setEnabled(false);
            etNumber.setEnabled(false);
            etNumber.setText(insertNumber);
            radioGroup.setEnabled(false);
            ContactsUtils.disableRadioGroup(radioGroup);
            progressBar.setMax(totalContacts);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
    }


    private void initView() {
        btInsert = findViewById(R.id.bt_insert);
        btInsert.setOnClickListener(this);
        radioGroup = findViewById(R.id.nameType);
        radioGroup.setOnCheckedChangeListener(this);
        etNumber = findViewById(R.id.et_new_Contacts_number);
        progressBar = findViewById(R.id.pr_process);
        mRadioMax = findViewById(R.id.ck_max);

        mRadioMax.setChecked(true);


    }

    @Override
    public void onClick(View v) {
        mHandler = new MyHandler();
        switch (v.getId()) {
            case R.id.bt_insert:
                if (insertContacts) {
                    Toast.makeText(this, "当前有操作还未完成，请等待！", Toast.LENGTH_SHORT).show();
                    btInsert.setEnabled(false);
                    etNumber.setEnabled(false);

                } else {


                    contacts = Integer.valueOf(etNumber.getText().toString());

                    if (contacts > 500) {
                        Toast.makeText(this, "当前限制为每次插入500个联系人", Toast.LENGTH_SHORT).show();
                    } else {
                        int totalContacts = Integer.parseInt(etNumber.getText().toString());
                        Toast.makeText(this, "正在进行数据插入，请稍后！", Toast.LENGTH_SHORT).show();
                        progressBar.setMax(totalContacts);
                        progressBar.setProgress(0);
                        Log.i("RDebug", "startHandler");
                        insertContacts = true;
                        InsertContactsThread = new InsertContactsThread();
                        InsertContactsThread.start();
                        ContactsUtils.disableRadioGroup(radioGroup);
                        etNumber.setEnabled(false);
                        radioGroup.setEnabled(false);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("contactsNumber", etNumber.getText().toString());
                        editor.putBoolean("Inserting", insertContacts);
                        editor.putInt("AllContacts", totalContacts);
                        editor.apply();
                        progressBar.setVisibility(View.VISIBLE);
                    }


                }


        }

    }

    public class InsertContactsThread extends Thread {
        @Override
        public void run() {


            int insertedContacts = 1;
            while (insertContacts && insertedContacts <= (contacts)) {

                String name = BaseUtils.getRandomString(baseString, 5);
                String email = ContactsUtils.getRandomEmail();
                String phone = ContactsUtils.getRandmPhone(8);
                String address = BaseUtils.getRandomString(baseString, 10);
                String notes = BaseUtils.getRandomString(baseString, 20);

                ContactsUtils.addNewContact(name, phone, email, address, notes);

                message = mHandler.obtainMessage();
                message.arg1 = insertedContacts;
                message.what = 1;
                mHandler.sendMessage(message);
                insertedContacts++;

            }

            //当生成的数据与输入的数据相等时，停止插入。
            insertContacts = false;
            message = mHandler.obtainMessage();
            message.what = 2;
            mHandler.sendMessage(message);


        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // create base string, default value is random string.
        if (!TextUtils.isEmpty(baseString)) {
            baseString = "";
        }
        if (mRadioMax.isChecked()) {
            baseString = charsContent + charsChinese + CharsUpChar + charsLowerChar;
        } else {
            switch (checkedId) {
                case R.id.ck_chars:
                    baseString = charsContent;

                    break;
                case R.id.ck_Chinese:
                    baseString = charsChinese;
                    break;
                case R.id.ck_LowerChar:
                    baseString = charsLowerChar;
                    break;
                case R.id.ck_UpChar:
                    baseString = CharsUpChar;
                    break;
                default:
                    baseString = charsContent + charsChinese + CharsUpChar + charsLowerChar;

            }
        }


    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:

                    progressBar.setProgress(msg.arg1);
                    Log.d("RDebug", "正在插入第" + msg.arg1 + "个联系人");
                    break;
                case 2:

                    btInsert.setEnabled(true);
                    etNumber.setEnabled(true);
                    radioGroup.setEnabled(true);
                    ContactsUtils.enableRadioGroup(radioGroup);
                    Toast.makeText(getApplicationContext(), "插入完成!", Toast.LENGTH_SHORT).show();
                    progressBar.setProgress(contacts);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("contactsNumber", "");
                    editor.putBoolean("Inserting", insertContacts);
                    editor.putInt("AllContacts", 0);
                    editor.apply();
                    break;

            }
        }
    }


}


