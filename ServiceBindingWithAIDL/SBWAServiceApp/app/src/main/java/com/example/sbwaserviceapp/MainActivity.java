package com.example.sbwaserviceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ITestServiceAIDL iTestServiceAIDL;
    private boolean bound = false;
    private TextView textView;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
//            TestService.MyBinder binder = (TestService.MyBinder) service;
//            myService = binder.getService();
            iTestServiceAIDL = ITestServiceAIDL.Stub.asInterface(service);
            bound = true;
            textView.setText("Service Bound Successfully!");
            Toast.makeText(MainActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            textView.setText("Service NOT BOUND");
            Toast.makeText(MainActivity.this, "Service Stopped", Toast.LENGTH_SHORT).show();
        }
    };
    private Intent testServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.service_status);
        Button buttonStartService = (Button) findViewById(R.id.buttonStartService);
        Button buttonStopService = (Button) findViewById(R.id.buttonStopService);
        Button buttonCheckService = (Button) findViewById(R.id.buttonCheckService);

        buttonStopService.setOnClickListener(this);
        buttonStartService.setOnClickListener(this);
        buttonCheckService.setOnClickListener(this);

        testServiceIntent = new Intent(this, com.example.sbwaserviceapp.TestService.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonStartService) {
            bindService(testServiceIntent, connection, Context.BIND_AUTO_CREATE);
        } else if (id == R.id.buttonStopService) {
            if (bound) {
                unbindService(connection);
                bound = false;
            }
            stopService(testServiceIntent);
        }else if (id == R.id.buttonCheckService){
            if (AppUtils.isServiceRunning(this,"com.example.sbwaserviceapp.TestService")){
                textView.setText("Service is Running");
            }else{
                textView.setText("Service is Stopped");
            }
        }

    }

}