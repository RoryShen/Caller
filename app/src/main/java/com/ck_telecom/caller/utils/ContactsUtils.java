package com.ck_telecom.caller.utils;

import com.ck_telecom.caller.activity.MainActivity;
import com.ck_telecom.caller.config.BaseConfig;
import com.ck_telecom.caller.config.ContactsConfig;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Rory on 2/8/2018.
 */

public class ContactsUtils {


    /**
     * New a contact.
     *
     * @param name    The contact name.
     * @param phone   The contact phone number
     * @param email   Email address.
     * @param address Address
     * @param notes   Notes.
     * @return if insert return true, else return false.
     */

    public static void addNewContact(String name, String phone, String email, String address, String notes) {
        boolean hasEmail = TextUtils.isEmpty(email);
        boolean hasAddress = TextUtils.isEmpty(address);
        boolean hasName = TextUtils.isEmpty(name);
        boolean hasNotes = TextUtils.isEmpty(notes);
        boolean hasPhone = TextUtils.isEmpty(phone);

        //Check the basic info.
        if (hasName) {
            throw new IllegalArgumentException("Name can not be empty!");
        } else if (!phone.matches("[\\+0-9\\-]*")) {
            throw new IllegalArgumentException("Please check you phone number!" + phone);
        } else if (!hasEmail && !email.matches("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,}+){1,}+)$")) {
            throw new IllegalArgumentException("Please check you email address!" + email);
        }

        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation operation = null;

        operation = ContentProviderOperation
                .newInsert(ContactsConfig.RAW_CONTACTS_URI).withValue(ContactsConfig.ACCOUNT_TYPE, "com.android.localphone")
                .withValue(ContactsConfig.ACCOUNT_NAME, "PHONE").build();
        operations.add(operation);
        //Add contacts name to database.
        operation = ContentProviderOperation.newInsert(ContactsConfig.RAW_CONTACTS_URI).
                withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                .withValue(ContactsConfig.MIMETYPE, ContactsConfig.NAME_ITEM_TYPE)
                .withValue(ContactsConfig.DISPLAY_NAME, name).build();
        operations.add(operation);


        //If email is not empty insert email.
        if (!hasEmail) {

            operation = ContentProviderOperation.newInsert(ContactsConfig.DATA_URI)
                    .withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.EMAIL_ITEM_TYPE)
                    .withValue(ContactsConfig.EMAIL_TYPE, ContactsConfig.EMAIL_TYPE_HOME)
                    .withValue(ContactsConfig.EMAIL_DATA, email).build();
            operations.add(operation);

            //Insert phone.
        } else if (!hasPhone) {

            operation = ContentProviderOperation.newInsert(ContactsConfig.DATA_URI)
                    .withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.PHONE_ITEM_TYPE)
                    .withValue(ContactsConfig.PHONE_TYPE, ContactsConfig.PHONE_TYPE_MOBILE)
                    .withValue(ContactsConfig.PHONE_NUMBER, phone).build();
            operations.add(operation);

            //Insert Address.
        } else if (!hasAddress) {


            operation = ContentProviderOperation.newInsert(ContactsConfig.DATA_URI)
                    .withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.ADDRESS_ITEM_TYPE)
                    .withValue(ContactsConfig.ADDRESS_CITY, address)
                    .withValue(ContactsConfig.ADDRESS_COUNTRY, "China").build();
            operations.add(operation);

            //Insert notes.
        } else if (!hasNotes) {

            operation = ContentProviderOperation.newInsert(ContactsConfig.DATA_URI)
                    .withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.NOTE_ITEM_TYPE)
                    .withValue(ContactsConfig.NOTE_DATA, notes).build();
            operations.add(operation);
        }


        try {
            ContentResolver resolver = MainActivity.mContext.getContentResolver();
            ContentProviderResult[] results = resolver.applyBatch(ContactsConfig.AUTHORITY, operations);

        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }


    }


    /**
     * Get a random email address.
     */
    public static String getRandomEmail() {
        Random random = new Random();
        int length = random.nextInt(10);
        String base = "qwertyuiopasdfghjklzxcvbnm0123456789";
        StringBuilder builder = new StringBuilder();
        builder.append(BaseUtils.getRandomString(base, length))
                .append("@")
                .append(BaseUtils.getRandomString(base, length))
                .append(".")
                .append(BaseUtils.getRandomString(base, length));

        String email = builder.toString();
        Log.d(BaseConfig.TAG, "New Email is:" + email);
        return email;

    }

    /**
     * Get a random phone number.
     */
    public static String getRandmPhone(int length) {
        String base = "+-0123456789";
        String phone = BaseUtils.getRandomString(base, length);
        Log.d(BaseConfig.TAG, "New phone is:" + phone);
        return phone;

    }
}


