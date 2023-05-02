package com.example.sbwmserviceapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyExportedService extends Service {

    private static final String TAG= MyExportedService.class.getSimpleName();

    private int mRandomNumber;
    private boolean mIsRandomGeneratorOn;

    private static final int MIN=0;
    private static final int MAX=100;

    public static final int GET_RANDOM_NUMBER_FLAG=0;

    private ScheduledExecutorService mExecutorService;

    private static class RandomNumberRequestHandler extends Handler {

        private final WeakReference<MyExportedService> serviceRef;

        RandomNumberRequestHandler(MyExportedService service) {
            super(Looper.getMainLooper());
            this.serviceRef = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MyExportedService service = serviceRef.get();
            // msg.what is used to identify what this message is about
            if (service != null && msg.what == GET_RANDOM_NUMBER_FLAG) {
                // in this example we are using arg1 to pass an integer into the message
                // we can use obj to pass complex object
                Message messageSendRandomNumber = Message.obtain(null, GET_RANDOM_NUMBER_FLAG);
                messageSendRandomNumber.arg1 = service.getRandomNumber();
                try {
                    // the replyTo is used to send a reply message to the sender
                    msg.replyTo.send(messageSendRandomNumber);
                } catch (RemoteException e) {
                    Log.i(TAG, "" + e.getMessage());
                }
            }
            super.handleMessage(msg);
        }
    }

    private final Messenger randomNumberMessenger=new Messenger(new RandomNumberRequestHandler(MyExportedService.this));


    @Override
    public IBinder onBind(Intent intent) {
        return randomNumberMessenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRandomNumberGenerator();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIsRandomGeneratorOn =true;
        new Thread(this::startRandomNumberGenerator).start();
        return START_STICKY;
    }

    private void startRandomNumberGenerator() {

        mIsRandomGeneratorOn =true;
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        mExecutorService.scheduleAtFixedRate(() -> {
            if(mIsRandomGeneratorOn){
                mRandomNumber =new Random().nextInt(MAX)+MIN;
                Log.i(TAG,"Random Number: "+mRandomNumber);
            }else{
                stopRandomNumberGenerator();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopRandomNumberGenerator() {
        mIsRandomGeneratorOn =false;
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            Toast.makeText(getApplicationContext(),"Service Stopped",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    public int getRandomNumber(){
        return mRandomNumber;
    }


}