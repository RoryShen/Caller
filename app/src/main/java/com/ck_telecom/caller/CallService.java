package com.ck_telecom.caller;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Rory on 2017/11/16   .
 */

public class CallService extends Service {
    private Intent dataIntent;
    private String phoneNum;
    private int times;
    private int frequency;
//   callIntent.putExtra("phone", phoneNum);
//        callIntent.putExtra("times", times);
//        callIntent.putExtra("frequency", frequency);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dataIntent = intent;
        phoneNum = intent.getStringExtra("phone");
        times = intent.getIntExtra("times", -1);
        frequency = intent.getIntExtra("frequency", -1);
        makeCall();
        Log.e("Call,Times", times + "");
        Log.e("Call,frequency", frequency + "");
        return super.onStartCommand(intent, flags, startId);
    }


    @SuppressLint("MissingPermission")
    private void makeCall() {

        if (times != -1 && frequency != -1) {
            final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            if (phoneNum.matches("1[3-8]\\d{9}$")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(;;){
                            Log.e("Call,Times", getCallStatus() + "");
                        }


                    }
                }).start();


            } else if (phoneNum.matches("^\\d+$")) {
                switch (phoneNum.length()) {
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        startActivity(callIntent);
                        break;
                    default:
                        Toast.makeText(this, R.string.number_short, Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, R.string.times_error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private int getCallStatus() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return telephonyManager.getCallState();
    }


}
