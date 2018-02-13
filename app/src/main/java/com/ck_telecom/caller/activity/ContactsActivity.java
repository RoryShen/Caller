package com.ck_telecom.caller.activity;

import com.ck_telecom.caller.R;
import com.ck_telecom.caller.config.BaseConfig;
import com.ck_telecom.caller.config.ContactsConfig;
import com.ck_telecom.caller.utils.BaseUtils;
import com.ck_telecom.caller.utils.ContactsUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity {
    String baseString;
    Button btInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        RadioGroup radioGroup = findViewById(R.id.nameType);
        radioGroup.setOnCheckedChangeListener(mradio);
        btInsert = findViewById(R.id.bt_insert);

        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btInsert.setOnClickListener(new View.OnClickListener() {
                    String name = BaseUtils.getRandomString(baseString, 5);
                    String email = ContactsUtils.getRandomEmail();
                    String phone = ContactsUtils.getRandmPhone(8);
                    String address = BaseUtils.getRandomString(baseString, 10);
                    String notes = BaseUtils.getRandomString(baseString, 20);

                    @Override
                    public void onClick(View v) {
                        ContactsUtils.addNewContact(name, phone, email, address, notes);
                        Toast.makeText(ContactsActivity.this, "Test", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
    }


    /*
    use this method to get a rand contacts name.
     */
    private RadioGroup.OnCheckedChangeListener mradio = new RadioGroup.OnCheckedChangeListener() {
        final String charsContent = ContactsConfig.CHARS_SYMBOLS;
        final String charsChinese = ContactsConfig.CHARS_CHINESE;
        final String charsLowerChar = ContactsConfig.CHARS_LOWER_CHAR;
        final String CharsUpChar = ContactsConfig.CHARS_UP_CHAR;

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (!TextUtils.isEmpty(baseString)) {
                baseString = "";
            }
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
                case R.id.ck_max:
                    baseString = charsContent + charsChinese + CharsUpChar + charsLowerChar;


            }

            Log.d(BaseConfig.TAG, "The base string :" + baseString);

        }


    };


}
