package com.hardskins.hardskins;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private boolean shouldAllowBack = true;

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
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

//            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
//            builder.setTitle("Важное сообщение!")
//                    .setMessage("Покормите кота!")
//                    .setCancelable(false)
//                    .setNegativeButton("ОК, иду на кухню",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//            AlertDialog alert = builder.create();
//            alert.show();


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
                    SharedPreferences.Editor editor = prefs.edit();
                    Date date = new Date();
                    long time = date.getTime();
                    long lastDownload = prefs.getLong("LastDownloadFromServer", 0);
                    // TODO поменять время на раз в сутки(77760000)
                    if (time - 3000 > lastDownload) {
                        editor.putBoolean("GetOnlineElement", true);
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
