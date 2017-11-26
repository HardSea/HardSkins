package com.hardskins.hardskins;

import android.app.Application;
import android.content.Context;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


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





    }

    public static Context getAppContext() {
        return Global.context;
    }




}
