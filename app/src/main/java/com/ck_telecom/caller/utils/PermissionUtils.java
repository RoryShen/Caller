package com.ck_telecom.caller.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Rory on 11/13/2017.
 */

public class PermissionUtils {
    private Context mContext;

    public PermissionUtils(Context context) {
        mContext=context.getApplicationContext();
    }
    // 判断权限集合
    public boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    //Check the permission.
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
