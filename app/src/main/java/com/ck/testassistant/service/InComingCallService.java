package com.ck.testassistant.service;

import com.ck.testassistant.R;
import com.ck.testassistant.utils.CallUtils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import static com.ck.testassistant.activity.AnswerActivity.getAnswerActivity;
import static com.ck.testassistant.utils.CallUtils.answerRingingCall;


/**
 * Created by Rory on 11/14/2017.
 */

public class InComingCallService extends Service {
    //private String phoneNumber;
    private String inComingNumber;
    private TelecomManager telecomManager;
    private TelephonyManager telephonyManager;
    private InComingCallListener inComingCallListener;
    private TextView total;
    private int sum = 0;
    private int time;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        total = getAnswerActivity().findViewById(R.id.tv_an_total_num);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        AnswerActivity answerActivity = new AnswerActivity();
//        total = answerActivity.findViewById(R.id.tv_an_total_num);
        Log.i("InComingCall", "InComingCall Service start!");
        inComingNumber = intent.getStringExtra("phone");
        time = intent.getIntExtra("frequency", 30);
        Log.i("RDebug", "InComing number is:" + inComingNumber);
        Toast.makeText(InComingCallService.this, "自动接听设置成功，当前监听号码为：" + inComingNumber, Toast.LENGTH_SHORT).show();
        getIncomingCall();
        total.findViewById(R.id.tv_an_total_num);

        return super.onStartCommand(intent, flags, startId);

    }

    private void getIncomingCall() {

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        inComingCallListener = new InComingCallListener();


        telephonyManager.listen(inComingCallListener, PhoneStateListener.LISTEN_CALL_STATE);


    }

    private class InComingCallListener extends PhoneStateListener {
        @SuppressLint("MissingPermission")
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i("RDebug", "InComing number is:" + incomingNumber);
                    //Toast.makeText(InComingCallService.this, "来电号码是：" + incomingNumber, Toast.LENGTH_SHORT).show();
                    if (inComingNumber.equals(incomingNumber)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            telecomManager.acceptRingingCall();
                            total.setText(sum + "");
                            Log.i("RDebug_O", "InComing number is:" + sum);
                        } else {

                            answerRingingCall(getApplicationContext());
                            sum = sum + 1;
                            Log.i("RDebug", "InComing number is:" + sum);
                            total.setText(sum + "");
                            //Toast.makeText(getAnswerActivity(), sum + "", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        CallUtils.endCall(getApplicationContext());
                    }
                    break;


            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sum = 0;
        total.setText(sum + "");
    }


}
