package com.ck_telecom.caller.utils;

import com.ck_telecom.caller.activity.MainActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Author/作者: Rory
 * Date/日期： 2018/04/18
 * Description/描述：
 */

public class MessageUtils {
    /**
     * get current message number.include inbox and send box.
     * @return
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

}
