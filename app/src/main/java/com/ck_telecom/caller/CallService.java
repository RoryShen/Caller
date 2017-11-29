package com.ck_telecom.caller;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
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
    private boolean exit = false;
    private Handler mHandler;
    private Intent callIntent;
    private TelecomManager telecomManager;
    private HandlerThread handlerThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service", "onStartCommand");

        //读取界面传过来的信息
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
        //当次数和间隔时间不为空时开始拨号。
        if (times != -1 && frequency != -1) {

            callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
            //判断号码是否符合规则。
            if (CallUtils.checkNumber(phoneNum)) {
                //启动一个子线程，开始拨打电话。
                handlerThread = new HandlerThread("call");
                handlerThread.start();
                //把线程放到handler里为了能够停止线程。
                mHandler = new Handler(handlerThread.getLooper());
                mHandler.post(mBackground);


            } else {
                Toast.makeText(this, "号码不符合规则,请重新输入", Toast.LENGTH_SHORT).show();
             

            }
        }


    }

    @SuppressLint("MissingPermission")
    Runnable mBackground = new Runnable() {
        @Override
        public void run() {
            int i = 1;
            //for (; i <= times; i++) {
            for (; ; ) {
                i++;
                if (exit) {
                    if (telecomManager.isInCall()) {
                        CallUtils.endCall(getApplicationContext());
                    }

                    break;
                } else if (!telecomManager.isInCall()) {
                    Log.e("Service", "当前正在进行第" + i + "次呼叫。");
                    startActivity(callIntent);
                    try {
                        Thread.sleep(frequency * 1000);
                        CallUtils.endCall(getApplicationContext());
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        if (telecomManager.isInCall()) {
                            CallUtils.endCall(getApplicationContext());
                        }
                        e.printStackTrace();
                    }
                }


            }
            if (i >= times) {
                Log.e("Service", getString(R.string.Call_Completed));
                //Toast.makeText (getApplicationContext (), getString (R.string.Call_Completed), Toast.LENGTH_SHORT).show ( );
                Thread.interrupted();

            } else {
                Log.e("Service", "呼叫中断，目前共执行了" + (i - 1) + "次。");
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //设置退出表示
        exit = true;
        //移除线程
        if (handlerThread.isAlive()) {
            mHandler.removeCallbacks(mBackground);

        }
        Log.e("Service", "mHandler: " + mHandler);
        Log.e("Service", handlerThread.isAlive() + "HandlerThread");

        Log.e("Service", "Service Stoped!");
    }
}
