package com.ck_telecom.caller;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
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
    private boolean exit;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e ("Service", "onStartCommand");

//        if (null != intent) {
        dataIntent = intent;
        phoneNum = intent.getStringExtra ("phone");
        times = intent.getIntExtra ("times", -1);
        frequency = intent.getIntExtra ("frequency", -1);
        makeCall ( );
        Log.e ("Call,Times", times + "");
        Log.e ("Call,frequency", frequency + "");


        return super.onStartCommand(intent, flags, startId);
    }


    @SuppressLint("MissingPermission")
    private void makeCall() {

        if (times != -1 && frequency != -1) {
            final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            final TelecomManager telecomManager = (TelecomManager) getSystemService (TELECOM_SERVICE);
            if (CallUtils.checkNumber (phoneNum)) {
                new Thread (new Runnable ( ) {
                    @Override
                    public void run() {
                        int i = 0;
                        for (; i < times; i++) {
                            if (!exit) {
                                startActivity (callIntent);
                                try {
                                    Thread.sleep (frequency * 1000);
                                    CallUtils.endCall (getApplicationContext ( ));
                                    Thread.sleep (5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace ( );
                                }
                            } else {
                                if (telecomManager.isInCall ( )) {
                                    CallUtils.endCall (getApplicationContext ( ));
                                }
                                Log.e ("Service","呼叫未完成，当前共执行了！"+i);

                                //Toast.makeText (getApplicationContext (), "呼叫未完成，当前共执行了" + i, Toast.LENGTH_SHORT).show ( );
                            }


                        }
                        if (i >= 49) {
                            Log.e ("Service",getString (R.string.Call_Completed));
                            //Toast.makeText (getApplicationContext (), getString (R.string.Call_Completed), Toast.LENGTH_SHORT).show ( );
                            Thread.interrupted ( );

                        }

                    }
                }).start ( );

            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy ( );
        exit = true;
        Log.e ("Service", "Service Stoped!");
    }
}
