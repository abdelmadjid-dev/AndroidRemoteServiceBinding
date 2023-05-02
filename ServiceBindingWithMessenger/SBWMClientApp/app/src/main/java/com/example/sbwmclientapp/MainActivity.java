package com.example.sbwmclientapp;

import static com.example.sbwmclientapp.R.id.buttonBindService;
import static com.example.sbwmclientapp.R.id.buttonGetRandomNumber;
import static com.example.sbwmclientapp.R.id.buttonUnBindService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    public static final int GET_RANDOM_NUMBER_FLAG = 0;
    private boolean mIsBound;

    Messenger randomNumberRequestMessenger, randomNumberReceiveMessenger;

    private Intent serviceIntent;

    private TextView textViewRandomNumber;

    private class ReceiveRandomNumberHandler extends Handler {
        public ReceiveRandomNumberHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            int randomNumberValue;
            if (msg.what == GET_RANDOM_NUMBER_FLAG) {
                randomNumberValue = msg.arg1;
                String s = "Random Number: " + randomNumberValue;
                textViewRandomNumber.setText(s);
            }
            super.handleMessage(msg);
        }
    }

    ServiceConnection randomNumberServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            randomNumberRequestMessenger = null;
            randomNumberReceiveMessenger = null;
            mIsBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            randomNumberRequestMessenger = new Messenger(binder);
            randomNumberReceiveMessenger = new Messenger(new ReceiveRandomNumberHandler(Looper.getMainLooper()));
            mIsBound = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        textViewRandomNumber = (TextView) findViewById(R.id.textViewRandomNumber);

        Button buttonBindService = (Button) findViewById(R.id.buttonBindService);
        Button buttonUnBindService = (Button) findViewById(R.id.buttonUnBindService);
        Button buttonGetRandomNumber = (Button) findViewById(R.id.buttonGetRandomNumber);

        buttonGetRandomNumber.setOnClickListener(this);
        buttonBindService.setOnClickListener(this);
        buttonUnBindService.setOnClickListener(this);

        serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName("com.example.sbwmserviceapp",
                "com.example.sbwmserviceapp.MyExportedService"));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case buttonBindService:
                bindToRemoteService();
                break;
            case buttonUnBindService:
                unbindFromRemoteSevice();
                break;
            case buttonGetRandomNumber:
                fetchRandomNumber();
                break;
            default:
                break;
        }
    }

    private void bindToRemoteService() {
        bindService(serviceIntent, randomNumberServiceConnection, BIND_AUTO_CREATE);
        Toast.makeText(mContext, "Service bound", Toast.LENGTH_SHORT).show();
    }

    private void unbindFromRemoteSevice() {
        if (mIsBound) {
            unbindService(randomNumberServiceConnection);
            mIsBound = false;
            Toast.makeText(mContext, "Service Unbound", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRandomNumber() {
        if (mIsBound) {
            Message requestMessage = Message.obtain(null, GET_RANDOM_NUMBER_FLAG);
            requestMessage.replyTo = randomNumberReceiveMessenger;
            try {
                randomNumberRequestMessenger.send(requestMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "Service Unbound, can't get random number",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        randomNumberServiceConnection = null;
    }
}