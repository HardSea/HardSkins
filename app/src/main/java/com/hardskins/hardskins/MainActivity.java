package com.hardskins.hardskins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    protected static List<Site> mSites = new ArrayList<>();;
    private Context context;
    private com.github.clans.fab.FloatingActionMenu fab;
    private final String TAG_PREFS = "com.hardskins.hardskins";
    private final Gson gson = new Gson();
    private SiteAdapter siteAdapter;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private String TAG = "HardSkins";


    private static void crateSite(){
        mSites.add(new Site("TempSite"));

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crateSite();


        context = this;

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun){
            mSites.add(new Site("Temp site"));
            Log.d(TAG, "Site add!");
            mSites.add(new Site("Temp site"));
            Log.d(TAG, "Site add!");
            getOnlineElements();
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
        } else{
            initializeData();
        }

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
    protected void onStop() {

        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        prefsEditor = appSharedPrefs.edit();

        String json = gson.toJson(mSites);
        prefsEditor.putString(TAG_PREFS, json);
        prefsEditor.apply();
        Log.d(TAG, "Get save array list successful!");

        super.onStop();
    } //save data to sharedpreference

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    } // condition on online

    protected static Date addMinutesToDate(int minutes, Date beforeTime){
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }


    protected void initializeData(){
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(TAG_PREFS, "");

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

    protected static void getOnlineElements(){
        final List <Site> tempmSites = new ArrayList<>();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("site");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tempSnapshot : dataSnapshot.getChildren()){
                    Site tempSite = tempSnapshot.getValue(Site.class);
                    tempmSites.add(tempSite);
                    // String value = dataSnapshot.child("hardskins-8912e").child("Site").child("0").child("site_address").getValue(String.class);
                    assert tempSite != null;
                    Log.d("HardSkins","Value is:" + tempSite.getSite_name());

                }

                Log.d("HardSkins", String.valueOf(mSites.size()));

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(Global.getAppContext(), "Неудалось обновить базу данных.\n Попробуйте позже", Toast.LENGTH_SHORT).show();


                Log.d("HardSkins", "Failed to read value", databaseError.toException());
            }
        });
        mSites.clear();
        mSites = tempmSites;

    } //Download element from Firebase


    private void createRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearManager);
        siteAdapter = new SiteAdapter(mSites, context);
        recyclerView.setAdapter(siteAdapter);

//        recyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(context, recyclerView,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//
//                        Intent intent = new Intent(context, SiteActivity.class);
//                        intent.putExtra("position", position);
//                        startActivity(intent);
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                    }
//                })
//        );

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
                siteAdapter.notifyDataSetChanged();
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




    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (int position = 0; position < mSites.size(); position++) {
            Picasso.with(context).invalidate(mSites.get(position).getSite_photo_url());
        }
    }


}
