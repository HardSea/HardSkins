package com.hardskins.hardskins.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.hardskins.hardskins.AppCompatPreferenceActivity;
import com.hardskins.hardskins.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity{
    private boolean shouldAllowBack = true;


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();


            if (preference instanceof ListPreference) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                SharedPreferences.Editor editor = prefs.edit();
                switch (preference.getKey()) {
                    case "vibration_count":
                        Log.d("HardSkins", "select vibration count");
                        editor.putInt("Number of vibrate", Integer.parseInt(stringValue));
                        editor.apply();
                        int indexNumber = prefs.getInt("Number of vibrate", 3);
                        preference.setSummary(String.valueOf(indexNumber));
                        break;
                    case "vibration_time":
                        Log.d("HardSkins", "select vibration time");
                        try{
                            editor.putInt("Time of vibrate", Integer.parseInt(stringValue));

                        }catch(NumberFormatException ex){

                        }
                        editor.apply();
                        int indexTime = prefs.getInt("Time of vibrate", 1);

                        preference.setSummary(preference.getContext().getResources().getStringArray(R.array.pref_time_of_vibration)[indexTime-1]);
                        break;

                    case "before_notify":
                        Log.d("SettingsHardSkins", "select before notify time");
                        try {
                            editor.putLong("before_time", Long.parseLong(stringValue));
                            editor.apply();
                        }catch(NumberFormatException ex){
                            Log.d("SettingsHardSkins", "select before notify time ERROR");

                        }

                        Log.d("SettingsHardSkins", "select before notify time " + prefs.getLong("before_time", 0));

                        break;
                    default:
                        break;

                }
            } else if (preference instanceof SwitchPreference){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                SharedPreferences.Editor editor = prefs.edit();
                switch (preference.getKey()){
                    case "load_pictures":
                        Log.d("SettingsHardSkins", "select load pictures");
                        editor.putBoolean("load_pictures", prefs.getBoolean("load_pictures", true));
                        editor.apply();
                        ((SwitchPreference) preference).setChecked(prefs.getBoolean("load_pictures", true));
                        break;
                    case "show_change_nickname_btn":
                        Log.d("SettingsHardSkins", "select show nickaname btn");
                        editor.putBoolean("show_change_nickname_btn", prefs.getBoolean("show_change_nickname_btn", false));
                        editor.apply();
                        ((SwitchPreference) preference).setChecked(prefs.getBoolean("show_change_nickname_btn", false));
                        break;
                }
            }

            return true;
        }
    };

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    private static void bindPreferenceSummaryToValue(Preference preference, int type) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        if (type == 1){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        } else if(type == 2){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  setupActionBar();
    }




    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DesignPreferenceFragment.class.getName().equals(fragmentName);
    }


    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("vibration_count"), 1);
            bindPreferenceSummaryToValue(findPreference("vibration_time"), 1);
            bindPreferenceSummaryToValue(findPreference("before_notify"), 1);


        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    public static class DesignPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_design);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("load_pictures"), 2);

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }






    @Override
    public void onHeaderClick(Header header, int position) {
        super.onHeaderClick(header, position);
        if (header.id == R.id.download_data_from_server) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            SharedPreferences.Editor editor = prefs.edit();
            long lastDownload = prefs.getLong("LastDownloadFromServer", 0);
            Date lastDateDownload = new Date(lastDownload);
            @SuppressLint("SimpleDateFormat") java.text.DateFormat df = new SimpleDateFormat("EEEE HH:mm ");


            AlertDialog.Builder ad;
            ad = new AlertDialog.Builder(SettingsActivity.this);
            ad.setTitle("Обновить данные с сервера?");  // заголовок
            ad.setMessage("Внимание! После обновления все таймеры могут обнулиться!!! \n\nОбновляться можно только раз в сутки. \nДата последнего обновления: " + df.format(lastDateDownload)); // сообщение
            ad.setPositiveButton("Обновить", new OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    shouldAllowBack = false;
                    @SuppressLint("SimpleDateFormat") java.text.DateFormat df = new SimpleDateFormat("EEEE hh:mm a");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    //SharedPreferences prefs = getApplicationContext().getSharedPreferences("default", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    Date date = new Date();
                    long time = date.getTime();
                    long lastDownload = prefs.getLong("LastDownloadFromServer", 0);
                    // TODO поменять время на раз в сутки(77760000)
                    if (time - 3000 > lastDownload) {
                        editor.putBoolean("GetOnlineElement", true);
                        editor.apply();
                        MainActivity.getOnlineElements(getApplicationContext());
                    } else {
                        Toast.makeText(getApplicationContext(), "Обновлять данные с сервера \n    можно только раз в сутки", Toast.LENGTH_SHORT).show();
                        Date localdate = new Date(lastDownload);

                        Toast.makeText(getApplicationContext(), "Псоледняя дата загрузки:\n " + "       " + df.format(localdate), Toast.LENGTH_SHORT).show();

                    }
                    editor.apply();

                   Intent i = getBaseContext().getPackageManager().
                           getLaunchIntentForPackage(getBaseContext().getPackageName());
                    assert i != null;
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   shouldAllowBack = true;

                    showWorkingDialog();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            removeWorkingDialog();
                        }

                    }, 3000);

                    startActivity(i);
                    finish();


                }
            });

            ad.setNegativeButton("Отмена", new OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });

            ad.setCancelable(true);
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {

                }
            });
            ad.show();





            editor.apply();


        }


    }

    private ProgressDialog working_dialog;

    private void showWorkingDialog() {
        working_dialog = ProgressDialog.show(SettingsActivity.this, "","Working please wait...", true);
    }

    private void removeWorkingDialog() {
        if (working_dialog != null) {

            working_dialog = null;


        }
    }



        @Override
    public void onBackPressed() {
        if (!shouldAllowBack) {
            Toast.makeText(this, "Дождитесь окончания загрузки", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
            Intent i = getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(getBaseContext().getPackageName());
            startActivity(i);
            finish();
        }

    }

}
