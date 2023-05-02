package com.example.sbwaclientapp;

import static com.example.sbwaclientapp.R.id.buttonBindService;
import static com.example.sbwaclientapp.R.id.buttonUnBindService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sbwaserviceapp.CustomHashMap;
import com.example.sbwaserviceapp.ITestServiceAIDL;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView textView;
    private ITestServiceAIDL iTestServiceAIDL;
    boolean isTestServiceBound = false;

    //    private ITestServiceAIDL mService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // Service is connected, you can call its methods
            textView.setText("Remote Service Bound!!");

            iTestServiceAIDL = ITestServiceAIDL.Stub.asInterface(iBinder);
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put("requestKey1", "requestValue1");
                map.put("requestKey2", "requestValue2");
                CustomHashMap customHashMap = new CustomHashMap(map);

                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : iTestServiceAIDL.processCustomData(
                        customHashMap
                ).getData().entrySet()) {
                    String s = entry.getKey() + " | " + entry.getValue();
                    sb.append(s);
                }
                textView.setText("Remote Service Bound!! Results : " + sb.toString());

            } catch (RemoteException e) {
                // Handle exception
                Log.e(TAG, "onServiceConnected: ",e );
            }
            isTestServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Service is disconnected
            textView.setText("Remote Service Not Bound");
            isTestServiceBound = false;
        }
    };
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.service_status);

        Button buttonBindService = (Button) findViewById(R.id.buttonBindService);
        Button buttonUnBindService = (Button) findViewById(R.id.buttonUnBindService);

        buttonBindService.setOnClickListener(this);
        buttonUnBindService.setOnClickListener(this);


        serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName("com.example.sbwaserviceapp",
                "com.example.sbwaserviceapp.TestService"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTestServiceBound) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case buttonBindService:
                bindToRemoteService();
                break;
            case buttonUnBindService:
                unbindFromRemoteService();
                break;
            default:
                break;
        }
    }

    private void bindToRemoteService() {



//        if (ContextCompat.checkSelfPermission(this, "com.example.sbwaserviceapp" +
//                ".TEST_SERVICE_ACCESS_PERMISSION")
//                == PackageManager.PERMISSION_GRANTED) {
//            Log.e("MainActivity", "onStart: permission Granted!");
//            // permission is granted, bind to the service
            bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
//        } else {
//            Log.e("MainActivity", "onStart: requesting permission!");
//            // permission is not granted, request it
//            ActivityCompat.requestPermissions(this,
//                    new String[]{"com.example.sbwaserviceapp" +
//                            ".TEST_SERVICE_ACCESS_PERMISSION"},
//                    MY_PERMISSION_REQUEST_CODE);
//        }
    }

    private void unbindFromRemoteService() {
        if (isTestServiceBound) {
            unbindService(mServiceConnection);
            isTestServiceBound = false;
            Toast.makeText(this, "Service Unbound", Toast.LENGTH_SHORT).show();
        }
    }


}