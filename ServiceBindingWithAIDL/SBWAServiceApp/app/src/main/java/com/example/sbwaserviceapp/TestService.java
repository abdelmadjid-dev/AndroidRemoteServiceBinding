package com.example.sbwaserviceapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TestService extends Service {
    public static final String TAG = "TestService";

    private final IBinder binder = new ITestServiceAIDL.Stub() {
        @Override
        public int sumAndGetResults(int a, int b) throws RemoteException {
            return sumAndGetResultsServiceMethod(a,b);
        }

        @Override
        public CustomHashMap processCustomData(CustomHashMap data) throws RemoteException {
            for (Map.Entry<String, String> entry : data.getData().entrySet()) {
                Log.e(TAG, "processCustomData: " + entry.getKey() + " | " + entry.getValue());
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("responseKey","responseValue");
            return new CustomHashMap(map);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    private int sumAndGetResultsServiceMethod(int a, int b) {
        return a+b;
    }

}
