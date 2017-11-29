package com.ck_telecom.caller;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Rory on 2017/11/17   .
 */

public class CallUtils {
    /**
     * Use for end call.
     *
     * @param context context.
     */
    public static void endCall(Context context) {
        try {

            Class<?> telephoneManager = Class.forName ("android.telephony.TelephonyManager");
            Method endCall = telephoneManager.getDeclaredMethod ("endCall", null);
            TelephonyManager tm = (TelephonyManager) context.getSystemService (TELEPHONY_SERVICE);
            endCall.setAccessible (true);
            endCall.invoke (tm);
        } catch (ClassNotFoundException e) {
            e.printStackTrace ( );
        } catch (NoSuchMethodException e) {
            e.printStackTrace ( );
        } catch (IllegalAccessException e) {
            e.printStackTrace ( );
        } catch (InvocationTargetException e) {
            e.printStackTrace ( );
        }
    }

    /**
     * Answer the incoming call.
     */
    public static void answerRingingCall(Context context) {
        try {
            // get telephony manager.
            Class<?> tm = Class.forName ("android.telephony.TelephonyManager");
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService (TELEPHONY_SERVICE);
            //get answerRingCall method.
            Method answerRingingCall = tm.getDeclaredMethod ("answerRingingCall");
            answerRingingCall.setAccessible (true);

            //Invoke the method.
            answerRingingCall.invoke (telephonyManager);

        } catch (ClassNotFoundException e) {
            e.printStackTrace ( );
        } catch (NoSuchMethodException e) {
            e.printStackTrace ( );
        } catch (IllegalAccessException e) {
            e.printStackTrace ( );
        } catch (InvocationTargetException e) {
            e.printStackTrace ( );
        }
    }

    /**
     * Check the number is correct or not.
     *
     * @param phoneNum the target number.
     * @return if the number is correct return true.
     */
    public static boolean checkNumber(String phoneNum) {
        boolean isCorrect = false;

        if (phoneNum.matches ("1[3-8]\\d{9}$"))

        {
            isCorrect = true;
        } else if (phoneNum.matches ("^\\d+$"))

        {
            switch (phoneNum.length ( )) {
                case 5:
                case 6:
                case 7:
                case 8:
                    isCorrect = true;
                    break;
                default:
                    isCorrect = false;
                    break;
            }
        }
        return isCorrect;

    }

//
}
