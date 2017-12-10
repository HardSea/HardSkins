package com.hardskins.hardskins;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TimerStarter{

    protected static List<Site> mSites = new ArrayList<>();
    private Context context;
    private com.github.clans.fab.FloatingActionMenu fab;
    private final String TAG_PREFS = "com.hardskins.hardskins";
    private final Gson gson = new Gson();
    private SiteAdapter siteAdapter;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private String TAG = "HardSkins";
    public final static String COUNTDOWN_BR = "hardskins.countdown_br";
    protected static int cnt_timer = 0;
    public static Activity mainActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;



        context = this;
        firstrun(); // check on first run and initializedata

        createNavigationMenu();



        createRecyclerView();         //creating recyclerview and refreshing new changes in recycler view
        signInFireBase();             // signInAnonymusly to FireBase




        boolean b = isOnline();
        if (b){
            Toast.makeText(this,"You are online!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"You are offline!", Toast.LENGTH_SHORT).show();

        }



    }

    @Override
    public void startServiceTimer(int position){

        startService(new Intent(this, BroadcastService.class)
                .putExtra("time", mSites.get(position).getSite_free_bonus_hour_time())
                .putExtra("nameSite", mSites.get(position).getSite_name())
                .setAction("SERVICE_START"));





        Log.d("BroadcastService", "Started service");
    }

    @Override
    public void continueServicetimer(int position) {

        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String nameSite = mSites.get(position).getSite_name();
        long temptime = appSharedPrefs.getLong(nameSite + "time to notify", 0);
        startService(new Intent(this, BroadcastService.class)
                .putExtra("time", temptime)
                .putExtra("nameSite", mSites.get(position).getSite_name())
                .setAction("SERVICE_CONTINUE"));





        Log.d("BroadcastService", "Started service");
    }


    @Override
    public void stopServiceTimer(int position) {
        startService(new Intent(this, BroadcastService.class)
                .putExtra("nameSite", mSites.get(position).getSite_name())
                .setAction("SERVICE_STOP"));





        Log.d("BroadcastService", "Stoped service");
    }

    @Override
    public  void showNotification(int position) {
        String nameSite = mSites.get(position).getSite_name();

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, ALARM_SERVICE)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Ваш бонус готов!")
                        .setContentText(nameSite)
                        .setPriority(MAX_PRIORITY)
                        .addAction(R.mipmap.ic_launcher, "Открыть сайт", pIntent)
                        .addAction(R.mipmap.ic_launcher, "Выключить уведомления", pIntent)
                        .setLights(10,10,10)
                        .setDefaults(Notification.DEFAULT_SOUND| Notification.DEFAULT_LIGHTS);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(position, mBuilder.build());

        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        v.vibrate(750);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    } // condition on online



    private void firstrun() {
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun){
            mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_0), "0"));
            Log.d(TAG, "Site add!");
            mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_1), "0"));
            Log.d(TAG, "Site add!");
            mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_2), "0"));
            Log.d(TAG, "Site add!");

            getOnlineElements(context);
            // Code to run once
            SharedPreferences.Editor editor = wmbPreference.edit();
            if (isOnline()){
                editor.putBoolean("FIRSTRUN", false);
                Log.d(TAG, "First time has been closed!");

            } else {
                editor.putBoolean("FIRSTRUN", true);
                Log.d(TAG, "First open, beacouse not internet!");
            }
            editor.apply();
        } else {
            initializeData();
        }
    }

    protected void initializeData(){
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(TAG_PREFS, "");
        cnt_timer = appSharedPrefs.getInt("count timer", 0);
        Type type = new TypeToken<ArrayList<Site>>(){}.getType();
      //  mSites.clear();
        mSites = gson.fromJson(json, type);

        Log.d(TAG, "Size of loading array = " + mSites.size());
    } //load saved data from sharedpreference


    private void createNavigationMenu(){
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public static void getOnlineElements(final Context context){
        SharedPreferences appSharedpreff = PreferenceManager.getDefaultSharedPreferences(context);
        boolean run = appSharedpreff.getBoolean("GetOnlineElement", true);
        SharedPreferences.Editor editor = appSharedpreff.edit();


        if (run){
            final List <Site> tempmSites = new ArrayList<>();
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference myRef = database.child("site");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mSites.clear();
                    mSites = tempmSites;
                    SiteAdapter.cleanList();
                    SiteAdapter.setSites(mSites);

                    for(DataSnapshot tempSnapshot : dataSnapshot.getChildren()){
                        Site tempSite = tempSnapshot.getValue(Site.class);
                        tempmSites.add(tempSite);
                        // String value = dataSnapshot.child("hardskins-8912e").child("Site").child("0").child("site_address").getValue(String.class);
                        assert tempSite != null;
                        Log.d("HardSkins","Value is:" + tempSite.getSite_name());

                    }
                    Toast.makeText(context, "Загрузка данных с сервера...", Toast.LENGTH_SHORT).show();
                    Log.d("HardSkins", String.valueOf(mSites.size()));

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Toast.makeText(Global.getAppContext(), "Неудалось обновить базу данных.\n Попробуйте позже", Toast.LENGTH_SHORT).show();


                    Log.d("HardSkins", "Failed to read value", databaseError.toException());
                }


            });



            editor.putBoolean("GetOnlineElement", false);


        }
        Date dateDownload = new Date();
        long timeDownload = dateDownload.getTime();
        editor.putLong("LastDownloadFromServer", timeDownload);
        editor.apply();

    } //Download element from Firebase

    private void createRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearManager);

        siteAdapter = new SiteAdapter(mSites, context, this);
        recyclerView.setAdapter(siteAdapter);

        try{
            sleep(1000);
            siteAdapter.notifyDataSetChanged();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        fab = findViewById(R.id.fab);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.hideMenu(true);
                else if (dy < 0)
                    fab.showMenu(true);
            }
        });




    }

    private void signInFireBase(){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("HardSkins", "signInAnonymusly::success");
                } else {
                    Log.w("HardSkins", "signInAnonymously::failure", task.getException());
                    Toast.makeText(MainActivity.this, R.string.error_connect,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        siteAdapter.notifyDataSetChanged();
    }//Authorized in Firebase

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_setting_button:
                startSettings();
                return true;
            case R.id.refresh_recyclerview:
                if (cnt_timer == 0){
                    siteAdapter.notifyDataSetChanged();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    } //tollbar buttons

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            startSettings();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    } //Navigator on click listerner

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void startSettings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

        startActivity(intent);
    }

    public void startHelp(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void fab_settings(View view) {
        startSettings();
    }

    public void fab_helps(View view) {
            startHelp();
    }



    @Override
    protected void onDestroy() {
        stopService(new Intent(this, BroadcastService.class));
        Log.i("BroadcastService", "Stopped service");

        for (int position = 0; position < mSites.size(); position++) {
            Picasso.with(context).invalidate(mSites.get(position).getSite_photo_url());
        }

        Date date = new Date();
        long time = date.getTime();
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefsEditor = appSharedPrefs.edit();
        prefsEditor.putLong("LastCloseAppTime", time);
        prefsEditor.apply();



        super.onDestroy();
    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            String nameTimer = intent.getStringExtra("name");
            long time = intent.getLongExtra("time", 0);

            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefsEditor = appSharedPrefs.edit();
            prefsEditor.putLong(nameTimer + "time to notify", time);
            prefsEditor.apply();


        }
    }

    private int getIndexByname(String timerName)
    {
        for(Site _item : mSites)
        {
            if(_item.getSite_name().equals(timerName))
                return mSites.indexOf(_item);
        }
        return -1;
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.d("BroadcastService", "Uregistered broadcast receiver");

    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            Log.d("BroadcastService", "Uregistered broadcast receiver"); // Receiver was probably already stopped in onPause()
        }

        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        prefsEditor = appSharedPrefs.edit();

        prefsEditor.putInt("count timer", cnt_timer);
        String json = gson.toJson(mSites);
        prefsEditor.putString(TAG_PREFS, json);
        prefsEditor.apply();
        Log.d(TAG, "Get save array list successful!");

        super.onStop();
    } //save data to sharedpreference

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(COUNTDOWN_BR));
        Log.d("BroadcastService", "Registered broadcast receiver");

    }
}
