package com.ck.testassistant.config;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Author/作者: Rory
 * Date/日期： 2018/02/13
 * Description/描述：
 */

public class ContactsConfig {

    public static String CHARS_SYMBOLS = "!@#$%^&*()_+{}|\\][:'\"\\;''./>?<";
    public static String CHARS_CHINESE = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许";
    public static String CHARS_LOWER_CHAR = "abcdefghigklmnopqrstuvwxyz";
    public static String CHARS_UP_CHAR = "ABCDEFGHIGKLMNOPQRSTUVWXYZ";

    //Contacts data table.
    public static final Uri DATA_URI = ContactsContract.Data.CONTENT_URI;

    //raw_contacts table
    public static final Uri RAW_CONTACTS_URI = ContactsContract.RawContacts.CONTENT_URI;

    //account_type
    public static final String ACCOUNT_TYPE = ContactsContract.RawContacts.ACCOUNT_TYPE;

    //account_name
    public static final String ACCOUNT_NAME = ContactsContract.RawContacts.ACCOUNT_NAME;

    //raw_contact_id
    public static final String RAW_CONTACT_ID = ContactsContract.Data.RAW_CONTACT_ID;

    //mimetype
    public static final String MIMETYPE = ContactsContract.Data.MIMETYPE;

    public static final String NAME_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
    public static final String DISPLAY_NAME = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;

    public static final String PHONE_ITEM_TYPE = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    public static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    public static final String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
    public static final int PHONE_TYPE_HOME = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
    public static final int PHONE_TYPE_MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

    public static final String EMAIL_ITEM_TYPE = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    public static final String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;
    public static final String EMAIL_TYPE = ContactsContract.CommonDataKinds.Email.TYPE;
    public static final int EMAIL_TYPE_HOME = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
    public static final int EMAIL_TYPE_WORK = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
    public static final String AUTHORITY = ContactsContract.AUTHORITY;

    public static final String ADDRESS_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
    public static final String ADDRESS_COUNTRY = ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY;
    public static final String ADDRESS_CITY = ContactsContract.CommonDataKinds.StructuredPostal.CITY;
    public static final String ADDRESS_STREET = ContactsContract.CommonDataKinds.StructuredPostal.STREET;

    public static final String NOTE_ITEM_TYPE = ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE;
    public static final String NOTE_DATA = ContactsContract.CommonDataKinds.Note.NOTE;

    public static final String PHOTO_ITEM_TYPE = ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE;
    public static final String PHOTO_DATA = ContactsContract.CommonDataKinds.Photo.PHOTO;


}

