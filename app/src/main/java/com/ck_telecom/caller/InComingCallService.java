package com.ck_telecom.caller;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.ck_telecom.caller.CallUtils.answerRingingCall;

/**
 * Created by Rory on 11/14/2017.
 */

public class InComingCallService extends Service {
    //private String phoneNumber;
    private String inComingNumber;
    private TelecomManager telecomManager;
    private TelephonyManager telephonyManager;
    private InComingCallListener inComingCallListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("InComingCall", "InComingCall Service start!");
        inComingNumber = intent.getStringExtra("phone");
        Log.e("Phone", inComingNumber);
        Toast.makeText(InComingCallService.this, "自动接听设置成功，当前监听号码为：" + inComingNumber, Toast.LENGTH_SHORT).show();
        getIncomingCall();
        return super.onStartCommand(intent, flags, startId);

    }

    private void getIncomingCall() {

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        inComingCallListener = new InComingCallListener();

        telephonyManager.listen(inComingCallListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class InComingCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
//                    Toast.makeText(InComingCallService.this, "来电号码是：" + incomingNumber, Toast.LENGTH_SHORT).show();
                    if (inComingNumber.equals(incomingNumber)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            telecomManager.acceptRingingCall();

                        } else {
                            answerRingingCall(getApplicationContext ());
                        }

                    }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
