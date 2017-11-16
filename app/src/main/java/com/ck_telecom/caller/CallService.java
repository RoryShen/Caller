package com.ck_telecom.caller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

        return super.onStartCommand(intent, flags, startId);
    }


    @SuppressLint("MissingPermission")
    private void makeCall() {
        if (times != -1 && frequency != -1) {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            if (phoneNum.matches("1[3-8]\\d{9}$")) {
                for (int i = 0; i <times;i++){
                    startActivity(callIntent);
                }

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
}
