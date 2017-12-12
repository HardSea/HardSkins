package com.hardskins.hardskins;


public interface TimerStarter  {

    void startServiceTimer(int position, long time);
    void continueServicetimer(int position);
    void stopServiceTimer(int position);
    void showNotification(int position);

}
