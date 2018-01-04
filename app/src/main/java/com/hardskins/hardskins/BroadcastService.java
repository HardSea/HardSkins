package com.hardskins.hardskins;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hardskins.hardskins.Activities.MainActivity;
import com.hardskins.hardskins.supportiveclasses.Vibration;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.MAX_PRIORITY;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";
    private int cnt_service = 0;
    private List<Timer> mTimers = new ArrayList<>();
    private Context context = this;
    private int position;



    @Override
    public void onDestroy() {
        Log.d(TAG, "Timer cancelled");
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if ("SERVICE_START".equals(intent.getAction())) {
            long time = (intent.getLongExtra("time", 0));
            String currentTimer = intent.getStringExtra("nameSite");
            position = getIndexBynameSite(currentTimer);
            Timer timer = new Timer(time, currentTimer);
            timer.startTimer();
            mTimers.add(timer);

            cnt_service++;
            Log.d(TAG, "Calling onStart command");
            Log.d(TAG, "Count service = " + cnt_service);
        } else if ("SERVICE_STOP".equals(intent.getAction())) {
            String nameSite = intent.getStringExtra("nameSite");
            try{
                mTimers.get(getIndexByname(nameSite)).stopTimer();
                Log.d(TAG, "Stooping timer in SERVICE_STOP");
                mTimers.remove(getIndexByname(nameSite));
                cnt_service--;
            } catch(java.lang.ArrayIndexOutOfBoundsException e){
                Log.d(TAG, "Not exist timer has been off");
            }



            Log.d(TAG, "Calling onStop command");
            Log.d(TAG, "Count service = " + cnt_service);

        } else if ("SERVICE_CONTINUE".equals(intent.getAction())) {
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


        return START_STICKY;


    }



    private int getIndexBynameSite(String pName) {
        for (Site _item : MainActivity.mSites) {
            if (_item.getName().equals(pName))
                return MainActivity.mSites.indexOf(_item);
        }
        return -1;
    }

    private int getIndexByname(String timerName) {
        for (Timer _item : mTimers) {
            if (_item.getNameTimer().equals(timerName))
                return mTimers.indexOf(_item);
        }
        return -1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    class Timer extends Vibration {
        String nameTimer;
        private CountDownTimer timer;
        protected long time;
        long timeServiceTime;


        Timer(long t, String name) {
            time = t;
            nameTimer = name;
        }

        private void stopTimer() {
            timer.cancel();
        }

        void startTimer() {
            timer = new CountDownTimer(time, 1000) {
                @Override
                public void onTick(long l) {
                    Log.d("BroadcastService", nameTimer + " countdown timer seconds reamaning: " + l / 1000);
                    timeServiceTime = l;
                }

                @Override
                public void onFinish() {
                    String nameSite = nameTimer;

                    Intent resultIntent = new Intent(BroadcastService.this, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(BroadcastService.this);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.mSites.get(position).getSite_free_bonus_link()));
                    PendingIntent pIntentBrowser = PendingIntent.getActivity(BroadcastService.this, 0, browserIntent, 0);


                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, ALARM_SERVICE)
                                    .setSmallIcon(R.drawable.ic_launcher_small)
                                    .setContentTitle("Ваш бонус готов!")
                                    .setContentText(nameSite)
                                    .setAutoCancel(true)
                                    .setPriority(MAX_PRIORITY)
                                    .addAction(R.drawable.ic_launcher_small, "Открыть сайт", pIntentBrowser)
                                    .setLights(10, 10, 10)
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    assert mNotificationManager != null;
                    mNotificationManager.notify(position, mBuilder.build());

                    Vibrator v = (Vibrator) BroadcastService.this.getSystemService(Context.VIBRATOR_SERVICE);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BroadcastService.this);
                    int numVibrate = prefs.getInt("Number of vibrate", 2);
                    int timeVibrate = prefs.getInt("Time of vibrate", 2);
                    long[] pattern = patternForVibrate(numVibrate, timeVibrate);

                    assert v != null;
                    v.vibrate(pattern, -1);

                    cancel();


                }
            }.start();
        }


        String getNameTimer() {
            return nameTimer;
        }

    }
}
