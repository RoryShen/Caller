package com.ck.testassistant.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.RadioGroup;

import java.util.Random;

/**
 * Author/作者: Rory
 * Date/日期： 2018/02/13
 * Description/描述：
 */

public class BaseUtils {

    /**
     * Get a random str
     *
     * @param base   the base string
     * @param length string length.
     * @return the random staring
     */
    public static String getRandomString(String base, int length) {

        int randomNum;
        char randomChar;
        Random random = new Random();
        // Create a string buffer to operation string.
        StringBuffer str = new StringBuffer();

        for (int i = 0; i < length; i++) {

            //Get position between 0 to N.
            randomNum = random.nextInt(base.length());
            // get the char of the rand position.
            randomChar = base.charAt(randomNum);
            // get a random string
            str.append(randomChar);
        }
        return str.toString();
    }

    public static String getEnglishRandomString(int length) {
        String baseString = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        return getRandomString(baseString, length);
    }

    /**
     * Get a random phone number.
     */
    public static String getRandPhone(int length) {
        String base = "0123456789";

        String country = "+" + BaseUtils.getRandomString(base, new Random().nextInt(4));

        // Log.d(BaseConfig.TAG, "New phone is:" + phone);
        return country + BaseUtils.getRandomString(base, length);

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
     * get current app version code.
     *
     * @return version code.
     */
    public static float getVersion(Context context) throws PackageManager.NameNotFoundException {


        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;


    }

    /**
     * get current app version name.
     *
     * @return version name.
     */
    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
    }


}
