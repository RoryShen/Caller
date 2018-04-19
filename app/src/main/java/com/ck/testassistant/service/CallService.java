package com.ck.testassistant.service;


import com.ck.testassistant.R;
import com.ck.testassistant.utils.CallUtils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import java.lang.ref.WeakReference;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.widget.Toast.LENGTH_SHORT;
import static com.ck.testassistant.activity.CallActivity.getCallActivity;


/**
 * Created by Rory on 2017/11/16   .
 */

public class CallService extends Service {

    private String phoneNum;
    private int times;
    private int frequency;
    private boolean exit = false;
    private Handler mHandler;
    private Intent callIntent;
    private TelecomManager telecomManager;
    private HandlerThread handlerThread;
    private Message message;
    private static TextView tvLog;
    private static TextView tvPass;
    private static TextView tvFail;
    private static EditText etTimes;
    private static EditText etFrequency;
    private static EditText phone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        CallActivity callActivity = new CallActivity();
        tvFail = getCallActivity().findViewById(R.id.tv_ca_fail_num);
        tvPass = getCallActivity().findViewById(R.id.tv_ca_pass_num);
        tvLog = getCallActivity().findViewById(R.id.tv_ca_log);
        etTimes = getCallActivity().findViewById(R.id.et_ca_total);
        etFrequency = getCallActivity().findViewById(R.id.et_ca_frequency);
        phone = getCallActivity().findViewById(R.id.et_ca_phone_ref_num);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service", "onStartCommand");


        //读取界面传过来的信息

        phoneNum = intent.getStringExtra("phone");
        times = intent.getIntExtra("times", -1);
        frequency = intent.getIntExtra("frequency", -1);

        makeCall();
        Log.e("Call,Times", times + "");
        Log.e("Call,frequency", frequency + "");

        mHandler = new MyHandler(this);

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
                Toast.makeText(this, "号码不符合规则,请重新输入", LENGTH_SHORT).show();


            }
        }


    }

    @SuppressLint("MissingPermission")
    Runnable mBackground = new Runnable() {
        @Override
        public void run() {

            int i = 0;
            for (; i < times; i++) {


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
                message = mHandler.obtainMessage();

                message.what = 1;
                message.obj = i;
                mHandler.sendMessage(message);

            }
            if (i >= times) {
                Log.e("Service", getString(R.string.Call_Completed));
                message = mHandler.obtainMessage();
                message.what = 3;
                message.obj = 0;
                mHandler.sendMessage(message);
                Thread.interrupted();

            } else {
                message = mHandler.obtainMessage();
                message.what = 2;
                message.obj = i;
                mHandler.sendMessage(message);
                Log.e("Service", "呼叫中断，目前共执行了" + i + "次。");
                Thread.interrupted();
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
        if (tvLog != null) {
            tvLog = null;
        }
        if (tvFail != null) {
            tvFail = null;
        }
        if (etTimes != null) {
            etTimes = null;
        }
        if (etFrequency != null) {
            etFrequency = null;
        }
        if (phone != null) {
            phone = null;
        }

    }

    private class MyHandler extends Handler {
        private final WeakReference<CallService> mActivity;

        public MyHandler(CallService activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int called;
            int left;
            CallService activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        called = (Integer) msg.obj + 1;
                        left = times - called;


                        Toast.makeText(CallService.this, called + "", LENGTH_SHORT).show();
                        tvPass.setText(called + "");
                        tvFail.setText(left + "");


                        break;
                    case 2:
                        called = (Integer) msg.obj;

                        left = times - called;

                        Toast.makeText(CallService.this, called + "", LENGTH_SHORT).show();
                        tvPass.setText(called + "");
                        tvFail.setText(left + "");
                        tvLog.setText(getString(R.string.manually_stop));
                        tvLog.setTextSize(40);
                        tvLog.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        break;
                    case 3:
                        tvLog.setText("测试完成!");
                        tvLog.setTextSize(40);
                        tvLog.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        phone.setEnabled(true);
                        etTimes.setEnabled(true);
                        etFrequency.setEnabled(true);
                        break;

                }
            }
        }
    }
}
