package com.ck_telecom.caller.activity;

import com.ck_telecom.caller.R;
import com.ck_telecom.caller.config.BaseConfig;
import com.ck_telecom.caller.config.ContactsConfig;
import com.ck_telecom.caller.utils.BaseUtils;
import com.ck_telecom.caller.utils.ContactsUtils;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
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
    EditText etNumber;
    ProgressBar progressBar;
    RadioButton mRadioMax;
    ObjectAnimator mObjectAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initView();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
    }


    private void initView() {
        Button btInsert = findViewById(R.id.bt_insert);
        btInsert.setOnClickListener(this);
        RadioGroup radioGroup = findViewById(R.id.nameType);
        radioGroup.setOnCheckedChangeListener(this);
        etNumber = findViewById(R.id.et_new_Contacts_number);
        progressBar = findViewById(R.id.pr_process);
        mRadioMax = findViewById(R.id.ck_max);

        mRadioMax.setChecked(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_insert:


                int k = Integer.parseInt(etNumber.getText().toString());

                progressBar.setMax(k);
                progressBar.setProgress(0);


                for (int i = 0; i < k; i++) {

                    String name = BaseUtils.getRandomString(baseString, 5);
                    String email = ContactsUtils.getRandomEmail();
                    String phone = ContactsUtils.getRandmPhone(8);
                    String address = BaseUtils.getRandomString(baseString, 10);
                    String notes = BaseUtils.getRandomString(baseString, 20);
                    ContactsUtils.addNewContact(name, phone, email, address, notes);
                    progressBar.incrementProgressBy(5);
                }


                Toast.makeText(ContactsActivity.this, etNumber.getText(), Toast.LENGTH_SHORT).show();
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


        Log.d(BaseConfig.TAG, "The base string :" + baseString);
    }
}

