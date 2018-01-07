package com.hardskins.hardskins.Activities;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.hardskins.hardskins.BroadcastService;
import com.hardskins.hardskins.R;
import com.hardskins.hardskins.Site;
import com.hardskins.hardskins.SiteAdapter;
import com.hardskins.hardskins.supportiveclasses.TimerStarter;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TimerStarter {

    public static List<Site> mSites = new ArrayList<>();
    private Context context;
    private com.github.clans.fab.FloatingActionMenu fab;
    private final String TAG_PREFS = "com.hardskins.hardskins";
    private final Gson gson = new Gson();
    private SiteAdapter siteAdapter;
    private SharedPreferences appSharedPrefs;
    private String TAG = "HardSkins";
    public static int cnt_timer;
    public static Activity mainActivity;
    public  boolean isFirstRun;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        context = this;
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor1 = appSharedPrefs.edit();
        isFirstRun = appSharedPrefs.getBoolean("FIRSTRUN", true);
        if (appSharedPrefs.getBoolean("FIRSTFIRSTRUNAPP", true)){
            Intent intent = new Intent(context, HelpActivity.class);
            startActivity(intent);
            editor1.putBoolean("FIRSTFIRSTRUNAPP", false);
            editor1.apply();

        }

        firstrun(); // check on first run and initializedata





        Date dateNow = new Date();
        long timeNow = dateNow.getTime();
        SharedPreferences appshared = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = appshared.edit();
        editor.putLong("LastOpenTime", timeNow);
        editor.apply();


        createNavigationMenu();
        cnt_timer = appSharedPrefs.getInt("count timer", 0);

        for (int i = 0; i < mSites.size(); i++) {
            if (appshared.getBoolean(mSites.get(i).getSite_name() + "site is notify", false)){
                startServiceTimer(i, mSites.get(i).getSite_free_bonus_hour_time());
                Log.d("abradk", "Initialize timers in MAINACTIVITY");
            }
        }

        createRecyclerView();         //creating recyclerview and refreshing new changes in recycler view
        signInFireBase();             // signInAnonymusly to FireBase






    }



    @Override
    public void startServiceTimer(int position, long time){

        startService(new Intent(this, BroadcastService.class)
                .putExtra("time", time)
                .putExtra("nameSite", mSites.get(position).getSite_name())
                .setAction("SERVICE_START"));


        Log.d("BroadcastService", "Started service");
    }

    @Override
    public void continueServicetimer(int position, String nameSite) {



        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        Date date = new Date();
        long nowtime = date.getTime();
        long timeEndTimer = appSharedPrefs.getLong(nameSite + "time end timer", 0);
        long setTime;
        setTime = timeEndTimer - nowtime;



        startService(new Intent(this, BroadcastService.class)
                .putExtra("time", setTime)
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


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    } // condition on online

    private void firstrun() {
        if (isFirstRun){
            isFirstRun = false;
            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);




            SharedPreferences.Editor editor = appSharedPrefs.edit();

            if (isOnline()){

                mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_3)));
                Log.d(TAG, "Site add!");
                mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_3)));
                Log.d(TAG, "Site add!");
                mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_3)));
                Log.d(TAG, "Site add!");

                editor.putBoolean("FIRSTRUN", false);
                editor.apply();
                getOnlineElements(context);
                Log.d(TAG, "First time has been closed!");

            } else {

                mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_0)));
                Log.d(TAG, "Site add!");
                mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_1)));
                Log.d(TAG, "Site add!");
                mSites.add(new Site(getResources().getString(R.string.text_for_temp_site_2)));
                Log.d(TAG, "Site add!");
                editor.putBoolean("FIRSTRUN", true);
                editor.apply();
                Log.d(TAG, "First open, beacouse not internet!");
            }
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
        mSites = gson.fromJson(json, type);

        try{
            Log.d(TAG, "Size of loading array = " + mSites.size());
        } catch (java.lang.NullPointerException e){
            Log.d("Eror", "Eror");
        }
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
        final SharedPreferences.Editor editor = appSharedpreff.edit();


        if (run){
            final List <Site> tempmSites = new ArrayList<>();
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference myRef = database.child("site");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mSites.clear();
                    mSites = tempmSites;
                    SiteAdapter.cleanList();
                    SiteAdapter.setSites(mSites);
                    editor.putBoolean("new elements", true);
                    editor.apply();

                    for(DataSnapshot tempSnapshot : dataSnapshot.getChildren()){
                        Site tempSite = tempSnapshot.getValue(Site.class);
                        tempmSites.add(tempSite);
                        // String value = dataSnapshot.child("hardskins-8912e").child("Site").child("0").child("site_address").getValue(String.class);
                        assert tempSite != null;
                        Log.d("HardSkins","Value is:" + tempSite.getSite_name());

                    }
                    Toast.makeText(context, "Загрузка данных с сервера...", Toast.LENGTH_SHORT).show();
                    Log.d("HardSkins", String.valueOf(mSites.size()));
                    myRef.removeEventListener(this);
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, "Неудалось обновить базу данных.\n Попробуйте позже", Toast.LENGTH_SHORT).show();
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

        linearManager.setAutoMeasureEnabled(false);

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
    public boolean onOptionsItemSelected(MenuItem item) { //    refresh item
        switch (item.getItemId()){
            case R.id.action_setting_button:
                startSettings();

                return true;
            case R.id.refresh_recyclerview:
                if (isFirstRun){
                    firstrun();
                }
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

        if (id == R.id.nav_help) {
            Intent intent = new Intent(context, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            startSettings();


        } else if(id == R.id.nav_steam){
            openSteamApp(context);
        } else if (id == R.id.nav_rateus) {

        } else if(id == R.id.nav_calc){
            Intent intent = new Intent(context, CalcActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_aboutus) {
            Intent intent = new Intent(context, AboutusActivity.class);
            startActivity(intent);
        }

        //TODO:create "clicable listener"

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
       finish();
    }

    public void startHelp(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public static void openSteamApp(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage("com.valvesoftware.android.steam.community");

            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Steam не установлен", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e){
            Toast.makeText(context, "Steam не установлен", Toast.LENGTH_SHORT).show();

        }
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
        Log.i("BroadcastService", "Calling on Destroy");
        for (int i = 0; i < mSites.size(); i++) {
            stopServiceTimer(i);

        }
        for (int position = 0; position < mSites.size(); position++) {
            Picasso.with(context).invalidate(mSites.get(position).getSite_photo_url());
        }
        super.onDestroy();
    }





    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

        prefsEditor.putInt("count timer", cnt_timer);
        String json = gson.toJson(mSites);
        prefsEditor.putString(TAG_PREFS, json);
        prefsEditor.apply();
        Log.d(TAG, "Get save array list successful!");

        super.onStop();
    } //save data to sharedpreference

}
