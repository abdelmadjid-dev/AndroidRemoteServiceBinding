package com.example.sbwaserviceapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import androidx.annotation.NonNull;

public class AppUtils {
    private static final String TAG = "AppUtils";

    public static boolean isServiceRunning(@NonNull Activity activity,
                                             @NonNull String serviceFullPath) {
        ActivityManager manager =
                (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceFullPath.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
