package com.hardskins.hardskins;


import android.os.CountDownTimer;
import android.util.Log;

class Timer {
    private String nameTimer;
    private CountDownTimer timer;
    private int time;

    Timer(int t, String name){
        time = t;
        nameTimer = name;
    }

    public void stopTimer(){
        timer.cancel();
    }

    public void startTimer(){
        long nowtime = time * 60000;
        timer = new CountDownTimer(nowtime ,1000) {
            @Override
            public void onTick(long l) {
                Log.d("BroadcastService", nameTimer + " countdown timer seconds reamaning: " + l/1000);
            }

            @Override
            public void onFinish() {
                cancel();
            }
        }.start();
    }


    public String getNameTimer() {
        return nameTimer;
    }

    public void setNameTimer(String nameTimer) {
        this.nameTimer = nameTimer;
    }
}
