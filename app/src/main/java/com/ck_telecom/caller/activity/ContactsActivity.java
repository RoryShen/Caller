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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class ContactsActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    String baseString;
    String charsContent = ContactsConfig.CHARS_SYMBOLS;
    String charsChinese = ContactsConfig.CHARS_CHINESE;
    String charsLowerChar = ContactsConfig.CHARS_LOWER_CHAR;
    String CharsUpChar = ContactsConfig.CHARS_UP_CHAR;

    // UI element.
    private static EditText etNumber;// contacts input box.
    private static ProgressBar prAdd;// display insert progress.
    private static RadioButton mRadioMax;// for baseString max.
    private static Button btInsert; // Insert button
    private static RadioGroup radioGroup;// Radio group for choose name char.
    private static Button btDeleteAll;// Delete all contacts button.
    private static TextView tvTotal;// For display all contacts number.
    private static TextView tvPercent;//for display process.
    private static ProgressBar prDelete;//delete contacts.
    private static ImageView sImageView;

    //Thread
    private static InsertContactsThread insertContactsThread;// label for
    private static DeleteContactsThread deleteContactsThread;

    //internal label.
    private PermissionUtils mPermissionsChecker; // permission checker.
    private int insertContactsNumber;// all contacts number.
    private boolean hasUpdate = false;
    private int allContactsNumber;


    private static final int REQUEST_CODE = 0; // 请求码
    private Message message;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mPermissionsChecker = new PermissionUtils(this);
        mSharedPreferences = getSharedPreferences("InsertContacts", MODE_PRIVATE);

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (hasUpdate) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt("contactsNumber", insertContactsNumber);
            editor.apply();
        }


        Log.d("RDebug", "OnStop:" + "insertContactsNumber:" + insertContactsNumber);


    }

    @Override
    protected void onResume() {
        super.onResume();

        // 缺少权限时, 进入权限配置页面
        if (!mPermissionsChecker.hasPermissions(PERMISSIONS)) {
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

        } else {
            initView();

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //reset ui element for gc.
        if (etNumber != null) {
            etNumber = null;
        }
        if (prAdd != null) {
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
        if (tvTotal != null) {
            tvTotal = null;
        }
        if (btDeleteAll != null) {
            btDeleteAll = null;
        }
        if (tvPercent != null) {
            tvPercent = null;
        }
        if (prDelete != null) {
            prDelete = null;
        }
        if (sImageView != null) {
            sImageView = null;
        }


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
        } else {
            mSharedPreferences.edit().putBoolean("Init", true).apply();
        }
    }


    private void initView() {
        // find all element.
        btDeleteAll = findViewById(R.id.bt_deleteAll);
        btInsert = findViewById(R.id.bt_insert);
        radioGroup = findViewById(R.id.nameType);
        etNumber = findViewById(R.id.et_new_Contacts_number);
        prAdd = findViewById(R.id.pr_add);
        mRadioMax = findViewById(R.id.ck_max);
        tvTotal = findViewById(R.id.tv_an_total_contacts_num);
        tvPercent = findViewById(R.id.tv_percent);
        prDelete = findViewById(R.id.pr_delete);
        sImageView = findViewById(R.id.iv_ad);


        //Set click listener.
        radioGroup.setOnCheckedChangeListener(this);
        btInsert.setOnClickListener(this);
        btDeleteAll.setOnClickListener(this);
        sImageView.setOnClickListener(this);

        int insertNumber = mSharedPreferences.getInt("contactsNumber", 0);
        tvTotal.setText(ContactsUtils.getContactsNumber() + "");
        etNumber.setText(insertNumber + "");
        //Get insert status.
        if (null != insertContactsThread && insertContactsThread.isAlive()) {

            prAdd.setVisibility(View.VISIBLE);
            tvPercent.setVisibility(View.VISIBLE);
            prAdd.setMax(insertNumber);
            disableUiElement();
        } else if (null != deleteContactsThread && deleteContactsThread.isAlive()) {
            disableUiElement();
            prDelete.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        mHandler = new MyHandler();
        // Log.d("RDebug", "ID:" + v.getId());
        switch (v.getId()) {


            case R.id.bt_insert:

                if (TextUtils.isEmpty(etNumber.getText().toString()) || Integer.valueOf(etNumber.getText().toString()) == 0) {
                    Toast.makeText(this, "请输入要插入联系人的数量！", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    insertContactsNumber = Integer.valueOf(etNumber.getText().toString());
                }
                if (insertContactsNumber > 500) {
                    Toast.makeText(this, "当前限制为每次插入500个联系人", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    Toast.makeText(this, "正在进行数据插入，请稍后！", Toast.LENGTH_SHORT).show();
                    prAdd.setVisibility(View.VISIBLE);
                    tvPercent.setVisibility(View.VISIBLE);
                    prAdd.setMax(insertContactsNumber);
                    prAdd.setProgress(0);
                    Log.i("RDebug", "startHandler");
                    insertContactsThread = new InsertContactsThread();
                    insertContactsThread.start();
                    disableUiElement();
                    hasUpdate = true;


                }


                break;
            case R.id.bt_deleteAll:
                // Check Current is idle or not.


                AlertDialog.Builder mBuilderNotes = new AlertDialog.Builder(this);
                mBuilderNotes.setTitle("即将清空联系人！");
                mBuilderNotes.setMessage("此操作将清空联系人，是否继续？");
                mBuilderNotes.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "正在清空联系人，请等待!", Toast.LENGTH_SHORT).show();
                        deleteContactsThread = new DeleteContactsThread();
                        deleteContactsThread.start();
                        prDelete.setVisibility(View.VISIBLE);
                        disableUiElement();


                    }
                });
                mBuilderNotes.setNegativeButton("取消", null);
                mBuilderNotes.show();
            case R.id.iv_ad:

                Toast.makeText(getApplicationContext(), "占位符。。。不知道放撒。。。", Toast.LENGTH_SHORT).show();

        }

    }

    public class InsertContactsThread extends Thread {
        @Override
        public void run() {
            if (TextUtils.isEmpty(baseString)) {
                baseString = charsContent + charsChinese + CharsUpChar + charsLowerChar;
            }
            Log.d("RDebug", "Start Add contacts.");
            int insertedContacts = 1;
            while (insertedContacts <= insertContactsNumber) {


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
                // Log.d("RDebug", "Add contacts Completed."+insertedContacts);
            }
            message = mHandler.obtainMessage();
            message.what = 2;
            mHandler.sendMessage(message);


        }
    }

    private class DeleteContactsThread extends Thread {

        @Override
        public void run() {
            int totalContacts = ContactsUtils.getContactsNumber();
            super.run();
            Log.d("RDebug", "Start Delete contacts.");
            if (totalContacts == 0) {
                mHandler.sendEmptyMessage(3);

            } else {

                ContactsUtils.deleteAllContacts();
                mHandler.sendEmptyMessage(3);


            }


        }

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // create base string, default value is random string.
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

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:

                    prAdd.setProgress(msg.arg1);

                    float percent = (float) msg.arg1 / insertContactsNumber * 100;

                    if (null != tvPercent) {
                        tvPercent.setText((int) percent + "%");
                    }


                    break;
                case 2:
                    Log.d("RDebug", "插入完成");
                    allContactsNumber = ContactsUtils.getContactsNumber();
                    // Ui element.
                    enableUiElement();
                    prAdd.setVisibility(View.GONE);// progress bar.
                    tvTotal.setText(ContactsUtils.getContactsNumber() + "");
                    //update total contacts number

                    tvPercent.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "插入完成!",
                            Toast.LENGTH_SHORT).show();


                    break;
                case 3:
                    Log.d("RDebug", "联系人已清空");
                    allContactsNumber = ContactsUtils.getContactsNumber();
                    Toast.makeText(getApplicationContext(), "联系人已清空！", Toast.LENGTH_SHORT).show();
                    tvTotal.setText(allContactsNumber + "");
                    prDelete.setVisibility(View.GONE);
                    enableUiElement();


            }
        }

    }

    private static void disableUiElement() {
        btDeleteAll.setEnabled(false);
        btInsert.setEnabled(false);
        etNumber.setEnabled(false);
        ContactsUtils.disableRadioGroup(radioGroup);
    }

    private static void enableUiElement() {
        btDeleteAll.setEnabled(true);
        btInsert.setEnabled(true);
        etNumber.setEnabled(true);
        ContactsUtils.enableRadioGroup(radioGroup);
    }
}


