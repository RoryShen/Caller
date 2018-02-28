package com.ck_telecom.caller.activity;

import com.ck_telecom.caller.R;
import com.ck_telecom.caller.config.BaseConfig;
import com.ck_telecom.caller.config.ContactsConfig;
import com.ck_telecom.caller.utils.BaseUtils;
import com.ck_telecom.caller.utils.ContactsUtils;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {
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
                        //test();
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

    public void test() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ContentProviderOperation.Builder builder = null;
        int rawContactIndex = 0;
        builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null);

        ops.add(builder.build());

        //add phone number

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                rawContactIndex);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "10086");
        // builder.withValue(ContactsContract.Data.IS_PRIMARY, 1);
        ops.add(builder.build());


        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }
    private void initView() {
        final String charsContent = ContactsConfig.CHARS_SYMBOLS;
        final String charsChinese = ContactsConfig.CHARS_CHINESE;
        final String charsLowerChar = ContactsConfig.CHARS_LOWER_CHAR;
        final String CharsUpChar = ContactsConfig.CHARS_UP_CHAR;

    }

    @Override
    public void onClick(View v) {
        //radioGroup = (RadioGroup) findViewById(R.id.radio_group);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
