package com.hardskins.hardskins;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class Global extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();







        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        Date date = new Date();
        long time = date.getTime();

        SharedPreferences appshared = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = appshared.edit();
        editor.putLong("LastOpenTime", time);
        editor.apply();



    }

    public static Context getAppContext() {
        return Global.context;
    }




}
