package com.ck_telecom.caller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Rory on 11/14/2017.
 */

public class InComingCallService extends Service {
    private TelephonyManager telephonyManager;
    private InComingCallListener inComingCallListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                    Toast.makeText(InComingCallService.this, "来电号码是：" + incomingNumber, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
