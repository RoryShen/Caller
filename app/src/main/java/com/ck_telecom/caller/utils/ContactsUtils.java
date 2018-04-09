package com.ck_telecom.caller.utils;

import com.ck_telecom.caller.activity.MainActivity;
import com.ck_telecom.caller.config.BaseConfig;
import com.ck_telecom.caller.config.ContactsConfig;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioGroup;

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

    public static void addNewContact(String name, String phone, String email, String address, String notes) throws IllegalArgumentException {

        //empty info check.
        boolean hasEmail = TextUtils.isEmpty(email);
        boolean hasAddress = TextUtils.isEmpty(address);
        boolean hasName = TextUtils.isEmpty(name);
        boolean hasNotes = TextUtils.isEmpty(notes);
        boolean hasPhone = TextUtils.isEmpty(phone);
        Log.d("AddContacts", "Name:" + name);
        Log.d("AddContacts", "Email:" + email);
        Log.d("AddContacts", "Phone:" + phone);
        Log.d("AddContacts", "address:" + address);
        Log.d("AddContacts", "Notes:" + notes);
        Log.d("AddContacts", "HasName:" + hasName + ",hasEmail:"
                + hasEmail + ",HaseAddres:" + hasAddress + ",HasNotes:" + hasNotes + ",HasPhone:" + hasPhone);


        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        ContentProviderOperation.Builder operation;

        //Insert a new id.
        operation = ContentProviderOperation
                .newInsert(ContactsConfig.RAW_CONTACTS_URI)
                .withValue(ContactsConfig.ACCOUNT_TYPE, null)
                .withValue(ContactsConfig.ACCOUNT_NAME, null);
        operations.add(operation.build());


        //Add contacts name to database.
        if (!hasName) {
            operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                    withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.NAME_ITEM_TYPE)
                    .withValue(ContactsConfig.DISPLAY_NAME, name).build());
            Log.d("AddContacts", name);
        }


        //If email is not empty insert email.
        if (!hasEmail) {

            operation = ContentProviderOperation.newInsert(ContactsConfig.DATA_URI)
                    .withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.EMAIL_ITEM_TYPE)
                    .withValue(ContactsConfig.EMAIL_TYPE, ContactsConfig.EMAIL_TYPE_HOME)
                    .withValue(ContactsConfig.EMAIL_DATA, email);
            operations.add(operation.build());
            Log.d("AddContacts", email);
            //Insert phone.
        }

        //Insert phone.
        if (!hasPhone) {

            operation = ContentProviderOperation.newInsert(ContactsConfig.DATA_URI)
                    .withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.PHONE_ITEM_TYPE)
                    .withValue(ContactsConfig.PHONE_NUMBER, phone)
                    .withValue(ContactsConfig.PHONE_TYPE, ContactsConfig.PHONE_TYPE_MOBILE);
            operations.add(operation.build());
            Log.d("AddContacts", phone);
            //Insert Address.
        }

        //Insert address.
        if (!hasAddress) {


            operation = ContentProviderOperation.newInsert(ContactsConfig.DATA_URI)
                    .withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.ADDRESS_ITEM_TYPE)
//                    .withValue(ContactsConfig.ADDRESS_CITY, "SiChuan")
//                    .withValue(ContactsConfig.ADDRESS_COUNTRY,"China")
                    .withValue(ContactsConfig.ADDRESS_STREET, address);
            operations.add(operation.build());
            Log.d("AddContacts", address);
            //Insert notes.
        }

        //Insert notes.
        if (!hasNotes) {

            operation = ContentProviderOperation.newInsert(ContactsConfig.DATA_URI)
                    .withValueBackReference(ContactsConfig.RAW_CONTACT_ID, 0)
                    .withValue(ContactsConfig.MIMETYPE, ContactsConfig.NOTE_ITEM_TYPE)
                    .withValue(ContactsConfig.NOTE_DATA, notes);
            operations.add(operation.build());
            Log.d("AddContacts", notes);
        }


        try {
            MainActivity.mContext.getContentResolver().applyBatch(ContactsConfig.AUTHORITY, operations);


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

    /**
     * Set the radio button status to disable.
     */
    public static void disableRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    /**
     * Set the radio button status to disable.
     */
    public static void enableRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(true);
        }
    }

    /**
     * Get contacts Number.
     *
     * @return current contacts number.
     */
    public static int getContactsNumber() {

        Cursor cursor = MainActivity.mContext.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, null,
                ContactsContract.RawContacts.DELETED + "=?", new String[]{"0"}, null);
        int id = 0;

        // If record not empty try to get contacts number.
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ++id;
            }
            cursor.close();
        }


        return id;
    }

    /**
     * Delete all contacts.
     */
    public static boolean deleteAllContacts() {
        //Get all contacts number.
        //Cursor cursor = MainActivity.mContext.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, new String[]{ContactsContract.RawContacts._ID}, ContactsContract.RawContacts.DELETED + "=?", new String[]{"0"}, null);
        Cursor cursor = MainActivity.mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int id = 0;
        int contactsId;
        boolean result = false;
        //If not empty try to delete contacts.
        if (cursor != null) {
            while (cursor.moveToNext()) {

                contactsId = cursor.getInt(0);
                Log.d("deleteContactsByName", "Try to delete:" + contactsId);
                MainActivity.mContext.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.Data._ID + "=?", new String[]{contactsId + ""});
                ++id;
            }
            result = true;
        }
        return result;
    }
}


