package com.example.sbwmserviceapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity  extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG=MainActivity.class.getSimpleName();

    private Context mContext;

    private Intent serviceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=getApplicationContext();
        Button buttonStartService = (Button) findViewById(R.id.buttonStartService);
        Button buttonStopService = (Button) findViewById(R.id.buttonStopService);

        buttonStopService.setOnClickListener(this);
        buttonStartService.setOnClickListener(this);
        serviceIntent=new Intent(getApplicationContext(),MyExportedService.class);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonStartService) {
            startService(serviceIntent);
            Toast.makeText(mContext, "Service Started", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.buttonStopService) {
            stopService(serviceIntent);
        }

    }
}