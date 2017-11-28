package com.hardskins.hardskins;


import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";
    private final static String COUNTDOWN_BR = "hardskins.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;
    private int cnt_service = 0;

    private void startTimer(){
        cdt = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long l) {
                Log.d(TAG, "SECOND Countdown timer seconds reamaning: " + l/1000);
                bi.putExtra("countdown", l);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "SECOND timer finished.");
            }
        }.start();
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Starting service timer...");
        cnt_service++;
        Log.d(TAG, "Count service = " + cnt_service);

        startTimer();

        cdt = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                Log.d(TAG, "FIRST Countdown timer seconds reamaning: " + l/1000);
                bi.putExtra("countdown", l);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "FIRST timer finished.");
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        Log.d(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cnt_service++;
        Log.d(TAG, "Calling onStart command");
        Log.d(TAG, "Count service = " + cnt_service);
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
