package com.hardskins.hardskins;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class  BroadcastService extends Service {

    private final static String TAG = "BroadcastService";
    public final static String COUNTDOWN_BR = "hardskins.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    private int cnt_service = 0;
    private List<Timer> mTimers = new ArrayList<>();





    @Override
    public void onDestroy() {
        Log.d(TAG, "Timer cancelled");
        super.onDestroy();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if ("SERVICE_START".equals(intent.getAction())){
            int time = Integer.parseInt((intent.getStringExtra("time")));
            String currentTimer = intent.getStringExtra("nameSite");

            Timer timer = new Timer(time, currentTimer);
            timer.startTimer();
            mTimers.add(timer);

            cnt_service++;
            Log.d(TAG, "Calling onStart command");
            Log.d(TAG, "Count service = " + cnt_service);
        } else if ("SERVICE_STOP".equals(intent.getAction())){
            String nameSite = intent.getStringExtra("nameSite");
            mTimers.get(getIndexByname(nameSite)).stopTimer();
            mTimers.remove(getIndexByname(nameSite));
            cnt_service--;
            Log.d(TAG, "Calling onStop command");
            Log.d(TAG, "Count service = " + cnt_service);

        }






        return START_REDELIVER_INTENT;




    }

    private int getIndexByname(String timerName)
    {
        for(Timer _item : mTimers)
        {
            if(_item.getNameTimer().equals(timerName))
                return mTimers.indexOf(_item);
        }
        return -1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
