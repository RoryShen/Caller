package com.ck_telecom.caller.utils;

import com.ck_telecom.caller.activity.MainActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import java.util.Date;
import java.util.Random;

/**
 * Author/作者: Rory
 * Date/日期： 2018/04/18
 * Description/描述：
 */

public class MessageUtils {
    /**
     * get current message number.include inbox and send box.
     */

    public static int getAllSmsNumber() {
        ContentResolver contentResolver = MainActivity.mContext.getContentResolver();
        final String[] projection = new String[]{"*"};
        Uri uri = Uri.parse("content://sms/inbox");
        Uri uri1 = Uri.parse("content://sms/sent");
        Cursor query = contentResolver.query(uri, projection, null, null, "date DESC");
        Cursor query1 = contentResolver.query(uri1, projection, null, null, "date DESC");
        int i = query.getCount() + query1.getCount();
        query.close();
        query1.close();
        return i;
    }

    public static int clearMessage() {
        Uri allUri = Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;
        return MainActivity.mContext.getContentResolver().delete(allUri, null, null);
    }


    public static void insertMessage(String phone, String messageType, String messageState, String smsBody) {

        ContentValues values = new ContentValues();
        values.put("address", phone);
        values.put("type", messageType);
        if (messageType.equals("1")) {
            values.put("date_sent", new Date().getTime());
        }
        values.put("read", messageState);
        values.put("body", smsBody);
        values.put("date", new Date().getTime());
        values.put("reply_path_present", 0);
        MainActivity.mContext.getApplicationContext().getContentResolver()
                .insert(Uri.parse("content://sms/inbox"), values);


    }

    public static void insertMessage() {

        ContentValues values = new ContentValues();
        String phone = BaseUtils.getRandPhone(11);
        String messageType = new Random().nextInt(2) + 1 + "";
        String messageState = new Random().nextInt(2) + "";
        String smsBody = BaseUtils.getEnglishRandomString(140);

        values.put("address", phone);
        values.put("type", messageType);
        if (messageType.equals("1")) {
            values.put("date_sent", new Date().getTime());
        }
        values.put("read", messageState);
        values.put("body", smsBody);
        values.put("date", new Date().getTime());
        values.put("reply_path_present", 0);
        MainActivity.mContext.getApplicationContext().getContentResolver()
                .insert(Uri.parse("content://sms/inbox"), values);


    }


}
