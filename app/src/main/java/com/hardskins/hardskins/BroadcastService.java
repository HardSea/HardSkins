package com.hardskins.hardskins;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class  BroadcastService extends Service {

    private final static String TAG = "BroadcastService";
    private int cnt_service = 0;
    private List<Timer> mTimers = new ArrayList<>();
    public final static String COUNTDOWN_BR = "hardskins.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    private int position;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private Context context = this;




    @Override
    public void onDestroy() {
        Log.d(TAG, "Timer cancelled");
        super.onDestroy();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if ("SERVICE_START".equals(intent.getAction())){

            long time = (intent.getLongExtra("time",0));
            String currentTimer = intent.getStringExtra("nameSite");
            position = getIndexBynameSite(currentTimer);

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

        } else if ("SERVICE_CONTINUE".equals(intent.getAction())){
            long time = intent.getLongExtra("time", 0);
            String currentTimer = intent.getStringExtra("nameSite");
            position = getIndexBynameSite(currentTimer);

            Timer timer = new Timer(time, currentTimer);
            timer.startTimer();
            mTimers.add(timer);

            cnt_service++;
            Log.d(TAG, "Calling onStart command");
            Log.d(TAG, "Count service = " + cnt_service);
        }






        return START_REDELIVER_INTENT;




    }
    private int getIndexBynameSite(String pName)
    {
        for(Site _item : MainActivity.mSites)
        {
            if(_item.getName().equals(pName))
                return MainActivity.mSites.indexOf(_item);
        }
        return -1;
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

    class Timer {
        private String nameTimer;
        private CountDownTimer timer;
        private long time;


        Timer(long t, String name){
            time = t;
            nameTimer = name;
        }

        private void stopTimer(){
            timer.cancel();
        }

        void startTimer(){
            timer = new CountDownTimer(time ,1000) {
                @Override
                public void onTick(long l) {
                    Log.d("BroadcastService", nameTimer + " countdown timer seconds reamaning: " + l/1000);
                    appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

                    prefsEditor = appSharedPrefs.edit();
                    prefsEditor.putLong(nameTimer + "time to notify", l);
                    prefsEditor.apply();

//                    bi.putExtra("name", nameTimer);
//                    bi.putExtra("time", l);
//                    bi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    sendBroadcast(bi);


                }

                @Override
                public void onFinish() {
                    cancel();
                }
            }.start();
        }




        String getNameTimer() {
            return nameTimer;
        }

        public void setNameTimer(String nameTimer) {
            this.nameTimer = nameTimer;
        }
    }
}
