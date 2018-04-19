package com.ck.testassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Rory on 4/10/2018.
 */

public class ContactService extends Service {

    // UI element.
    private static EditText etNumber;// contacts input box.
    private static ProgressBar progressBar;// display insert progress.
    private static RadioButton mRadioMax;// for baseString max.
    private static Button btInsert; // Insert button
    private static RadioGroup radioGroup;// Radio group for choose name char.
    private static Button btDeleteAll;// Delete all contacts button.
    private static TextView tvTotal;// For display all contacts number.


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int insert = intent.getIntExtra("InsertNumber", -1);
        Log.d("RDebug",insert+"Insert number");
        return super.onStartCommand(intent, flags, startId);

    }


}
