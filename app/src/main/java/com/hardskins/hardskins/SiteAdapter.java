package com.hardskins.hardskins;

import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hardskins.hardskins.Activities.MainActivity;
import com.hardskins.hardskins.Activities.SiteActivity;
import com.hardskins.hardskins.supportiveclasses.TimerStarter;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteHolder> {

    private static List<Site> sites;
    private Context context;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private TimerStarter firstStarter;
    private boolean onBind;
    private static int numOfBindOnElements;



    class SiteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private CardView cardView;
        private TextView sitename;
        private LinearLayout linearLayoutComplete;
        private ImageView sitePhoto;
        SwitchCompat switchNotify;
        private TextView textDate;
        private LinearLayout linearCard;
         CountDownTimer t;
        private TimerStarter secondStarter;
        private String locale = "%02d:%02d:%02d";
        private boolean isContinue = false;
        private Button openinbrowser_card;
        private Button buildOnBtn;
        private TextView changeNickname;


        SiteHolder(View itemView, TimerStarter timerStarter) {
            super(itemView);
            linearLayoutComplete = itemView.findViewById(R.id.linearcompletecard);
            cardView =  itemView.findViewById(R.id.cardView);
            sitename =  itemView.findViewById(R.id.site_name);
            sitePhoto =  itemView.findViewById(R.id.site_photo);
            switchNotify = itemView.findViewById(R.id.notificationSwitcher);
            textDate = itemView.findViewById(R.id.textDate);
            linearCard = itemView.findViewById(R.id.linearCard);
            linearCard.setOnClickListener(this);
            linearCard.setOnLongClickListener(this);
            secondStarter = timerStarter;
            openinbrowser_card = itemView.findViewById(R.id.openinbrowser_card);
            buildOnBtn = itemView.findViewById(R.id.buildOnElement_card);
            changeNickname = itemView.findViewById(R.id.changeNickname);

            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            numOfBindOnElements = appSharedPrefs.getInt("numofbindonelements", 0 );


            buildOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    prefsEditor = appSharedPrefs.edit();
                    boolean buildon = appSharedPrefs.getBoolean(sitename.getText().toString() + "buildon", false);
                    if (buildon){
                        numOfBindOnElements--;
                        notifyItemMoved(getAdapterPosition(), numOfBindOnElements);
                        buildOnBtn.setBackground(context.getResources().getDrawable(R.drawable.ico_start_no));
                        prefsEditor.putBoolean(sitename.getText().toString() + "buildon", false);
                        prefsEditor.putInt("numofbindonelements", numOfBindOnElements);
                        prefsEditor.apply();
                        Site tempSite = MainActivity.mSites.remove(getIndexByName(sitename.getText().toString()));
                        //Site tempSite2 = sites.remove(getIndexByName(sitename.getText().toString()));

                       // Site tempSite = MainActivity.mSites.remove(getAdapterPosition());


                        MainActivity.mSites.add(numOfBindOnElements, tempSite);
                        //sites.add(numOfBindOnElements, tempSite2);


                    } else {
                        numOfBindOnElements++;
                        notifyItemMoved(getAdapterPosition(), 0);
                        Site tempSite = MainActivity.mSites.remove(getIndexByName(sitename.getText().toString()));
                        MainActivity.mSites.add(0, tempSite);


                        buildOnBtn.setBackground(context.getResources().getDrawable(R.drawable.ico_start_yes));
                        prefsEditor.putBoolean(sitename.getText().toString() + "buildon", true);
                        prefsEditor.putInt("numofbindonelements", numOfBindOnElements);
                        prefsEditor.apply();





                    }
                }
            });




            if(appSharedPrefs.getBoolean("show_change_nickname_btn", false)) {
                changeNickname.setVisibility(View.INVISIBLE);
            } else {


                changeNickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final CheckBox dontShowAgain;
                        AlertDialog.Builder adb = new AlertDialog.Builder(context);
                        LayoutInflater adbInflater = LayoutInflater.from(context);
                        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                        String skipMessage = settings.getString("skipMessage", "NOT checked");

                        dontShowAgain = eulaLayout.findViewById(R.id.skip);
                        adb.setView(eulaLayout);
                        adb.setTitle("Открыть приложение Steam и скопировать ник?");

                        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String checkBoxResult = "NOT checked";

                                if (dontShowAgain.isChecked()) {
                                    checkBoxResult = "checked";
                                }

                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = settings.edit();

                                editor.putString("skipNickNameMessage", checkBoxResult);
                                editor.apply();

                                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Copied", MainActivity.mSites.get(getIndexByName(sitename.getText().toString())).getSite_nickName());
                                assert cm != null;
                                cm.setPrimaryClip(clip);
                                MainActivity.openSteamApp(context);

                            }
                        });

                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String checkBoxResult = "NOT checked";

                                if (dontShowAgain.isChecked()) {
                                    checkBoxResult = "checked";
                                }

                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = settings.edit();

                                editor.putString("skipNickNameMessage", checkBoxResult);
                                editor.apply();

                                // Do what you want to do on "CANCEL" action

                            }
                        });

                        if (!skipMessage.equals("checked")) {
                            adb.show();
                        } else {


                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Copied", MainActivity.mSites.get(getIndexByName(sitename.getText().toString())).getSite_nickName());
                            assert cm != null;
                            cm.setPrimaryClip(clip);
                            MainActivity.openSteamApp(context);
                        }
                    }
                });
            }


            switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!onBind) {

                        // int tempPosition = getIndexByName(sitename.getText().toString());

                        Site tempSite = MainActivity.mSites.get(getIndexByName(sitename.getText().toString()));
                        if (b) {
                            if (!isContinue) {
                                appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                                long beforeTimeNotify = appSharedPrefs.getLong("before_time", 0);
                                long timeBonusBySite = tempSite.getSite_free_bonus_hour_time() - beforeTimeNotify;
                                Date date = new Date();
                                long timeStartTimer = date.getTime();
                                long timeEndTimer = timeStartTimer + timeBonusBySite - beforeTimeNotify;

                                appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                                prefsEditor = appSharedPrefs.edit();
                                prefsEditor.putLong(sitename.getText().toString() + "time start timer", timeStartTimer);
                                prefsEditor.putLong(sitename.getText().toString() + "time end timer", timeEndTimer);
                                prefsEditor.apply();
                                switchOn(timeBonusBySite);
                                secondStarter.startServiceTimer(getIndexByName(sitename.getText().toString()), timeBonusBySite);
                            }
                        } else {
                            linearLayoutComplete.setBackgroundColor(-1);
                            isContinue = false;
                            switchOff();
                            t.cancel();
                             secondStarter.stopServiceTimer(getIndexByName(sitename.getText().toString()));


                        }
                    }

                }
            });

            openinbrowser_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CheckBox dontShowAgain;
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    LayoutInflater adbInflater = LayoutInflater.from(context);
                    View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    String skipMessage = settings.getString("skipMessage", "NOT checked");

                    dontShowAgain =  eulaLayout.findViewById(R.id.skip);
                    adb.setView(eulaLayout);
                    adb.setTitle("Открыть сайт в браузере?");

                    adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String checkBoxResult = "NOT checked";

                            if (dontShowAgain.isChecked()) {
                                checkBoxResult = "checked";
                            }

                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = settings.edit();

                            editor.putString("skipMessage", checkBoxResult);
                            editor.apply();

                            // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.mSites.get(getIndexByName(String.valueOf(sitename.getText().toString()))).getSite_free_bonus_link()));

                             Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.mSites.get(getIndexByName(sitename.getText().toString())).getSite_free_bonus_link()));

                            context.startActivity(browserIntent);

                        }
                    });

                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String checkBoxResult = "NOT checked";

                            if (dontShowAgain.isChecked()) {
                                checkBoxResult = "checked";
                            }

                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = settings.edit();

                            editor.putString("skipMessage", checkBoxResult);
                            editor.apply();

                            // Do what you want to do on "CANCEL" action

                        }
                    });

                    if (!skipMessage.equals("checked")) {
                        adb.show();
                    } else {
                      //  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.mSites.get(getIndexByName(String.valueOf(sitename.getText().toString()))).getSite_address()));

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.mSites.get(getIndexByName(sitename.getText().toString())).getSite_address()));
                        context.startActivity(browserIntent);
                    }
                }
            });

            textDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    prefsEditor = appSharedPrefs.edit();
                    Calendar mcurrentTime1 = Calendar.getInstance();
                    int hour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                    int minute =  mcurrentTime1.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            isContinue = false;
                           // int tempPosition = getIndexByName(String.valueOf(sitename.getText().toString()));

                            int tempPosition = getIndexByName(sitename.getText().toString());
                            prefsEditor.remove(sitename.getText().toString() + "time end timer");
                            prefsEditor.apply();
                            switchOff();
                            secondStarter.stopServiceTimer(tempPosition);
                            switchNotify.setChecked(false);
                            Date date = new Date();
                            long currentTimeDate = date.getTime();
                            long timeSet = ((selectedHour * 3600000) + (selectedMinute * 60000)) + currentTimeDate;
                            prefsEditor.putLong(sitename.getText().toString() + "time end timer", timeSet);
                            prefsEditor.apply();
                            Log.d("bradk", "Select time in datePicker");

                            isContinue = true;
                            long selectedTime = (selectedHour * 3600000) + (selectedMinute * 60000);
                            switchOn(selectedTime);
                            secondStarter.continueServicetimer(sitename.getText().toString());
                            switchNotify.setChecked(true);

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });


        }


        void startTimer(final long time) {
            long tick = 1000;
            t = new CountDownTimer(time, tick) {


                public void onTick(long millisUntilFinished) {
                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;

                    long elapsedHours = millisUntilFinished / hoursInMilli;
                    millisUntilFinished = millisUntilFinished % hoursInMilli;

                    long elapsedMinutes = millisUntilFinished / minutesInMilli;
                    millisUntilFinished = millisUntilFinished % minutesInMilli;

                    long elapsedSeconds = millisUntilFinished / secondsInMilli;

                    String yy = String.format(locale, elapsedHours, elapsedMinutes, elapsedSeconds);
                    textDate.setText(yy);

                }

                public void onFinish() {
                    textDate.setText(R.string.zerozerozerozerozerozero);
                    Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();
                    linearLayoutComplete.setBackgroundColor(-16711936);
                    cancel();
                }
            }.start();


        }

        private void switchOn(long time) {


            prefsEditor.putBoolean(sitename.getText().toString() + "site is notify", true);
            prefsEditor.apply();


            Log.d("HardSkins", "Switch on clicked!" + sitename.getText().toString());
            Toast.makeText(context, "Switcher ON", Toast.LENGTH_SHORT).show();
            textDate.setVisibility(View.VISIBLE);

            startTimer(time);
            MainActivity.cnt_timer++;
            Log.d("HardSkins", "Count timer" + MainActivity.cnt_timer);

        }

        private void switchOff() {
            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefsEditor = appSharedPrefs.edit();
            prefsEditor.remove(sitename.getText().toString() + "time end timer");
            prefsEditor.remove(context.getString(R.string.starttimertimer) + sitename.getText().toString());
            prefsEditor.putBoolean(String.valueOf(sitename.getText().toString()) + "site is notify", false);
            prefsEditor.apply();
            Log.d("HardSkins", "Switch off clicked!" + sitename.getText().toString());
            Toast.makeText(context, "Switcher OFF", Toast.LENGTH_SHORT).show();
            textDate.setVisibility(View.INVISIBLE);
            MainActivity.cnt_timer--;
            Log.d("HardSkins", "Count timer" + MainActivity.cnt_timer);

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, SiteActivity.class);
            intent.putExtra("position", getIndexByName(sitename.getText().toString()));
            context.startActivity(intent);
            Log.d("HardSkins", "One click in linearcard");
        }

        @Override
        public boolean onLongClick(View view) {
            if (switchNotify.isChecked()) {
                Toast.makeText(context, "Уведомления уже включены", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Log.d("HardSkins", "Long tap on linear layout!");
                switchNotify.setChecked(true);
                //Site tempSite = MainActivity.mSites.get(getIndexByName(String.valueOf(sitename.getText().toString())));

                Site tempSite = MainActivity.mSites.get(getIndexByName(sitename.getText().toString()));

                switchOn(tempSite.getSite_free_bonus_hour_time());
                Toast.makeText(context, "Уведомления включено", Toast.LENGTH_SHORT).show();
                return true;
            }

        }


    }


    public static void cleanList() {
        sites.clear();
    }

    public static void setSites(List<Site> sites1) {
        sites = sites1;
    }


    private int getIndexByName(String pName) {
        for (Site _item : MainActivity.mSites) {
            if (_item.getSite_name().equals(pName))
                return MainActivity.mSites.indexOf(_item);
        }
        return -1;
    }


    @Override
    public int getItemCount() {

            return sites.size();

    }

    @Override
    public SiteHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);



        return new SiteHolder(v, firstStarter);
    }


    @Override
    public void onBindViewHolder(final SiteHolder siteHolder, int position) {
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefsEditor = appSharedPrefs.edit();
        siteHolder.sitename.setText(MainActivity.mSites.get(position).getSite_name());

        if (appSharedPrefs.getBoolean("load_pictures", true)){
            Picasso.with(context)
                    .load(MainActivity.mSites.get(position).getSite_photo_url())
                    .into(siteHolder.sitePhoto);
        }



        siteHolder.setIsRecyclable(false);





        if (sites.size() <= 3) {
            siteHolder.switchNotify.setVisibility(View.INVISIBLE);
        } else {
            siteHolder.switchNotify.setVisibility(View.VISIBLE);

        }

        onBind = true;


        boolean buildon = appSharedPrefs.getBoolean(siteHolder.sitename.getText().toString() + "buildon", false);
        if (!buildon){
            siteHolder.buildOnBtn.setBackground(context.getResources().getDrawable(R.drawable.ico_start_no));
        } else{
            siteHolder.buildOnBtn.setBackground(context.getResources().getDrawable(R.drawable.ico_start_yes));

        }


        if (appSharedPrefs.getBoolean("new elements", false)) {
            prefsEditor.putBoolean(String.valueOf(siteHolder.sitename.getText().toString()) + "site is notify", false);
            prefsEditor.putBoolean("new elements", false);
            prefsEditor.apply();
        }

        if (appSharedPrefs.getBoolean(siteHolder.sitename.getText().toString() + "site is notify", false)) {
            siteHolder.switchNotify.setChecked(true);
            Date date = new Date();
            long nowtime = date.getTime();
            long timeEndTimer = appSharedPrefs.getLong(siteHolder.sitename.getText().toString() + "time end timer", 0);
           // int tempPosition = getIndexByName(String.valueOf(siteHolder.sitename.getText().toString()));

            int tempPosition = getIndexByName(siteHolder.sitename.getText().toString());


            long setTime;
            if (timeEndTimer < nowtime) {
                setTime = 500;
            } else {
                setTime = timeEndTimer - nowtime;
            }
            Log.d("HardSkins", "Time select in OnBind Holder" + String.valueOf(setTime));

            siteHolder.textDate.setVisibility(View.VISIBLE);
            siteHolder.startTimer(setTime);
            //siteHolder.secondStarter.startServiceTimer(tempPosition, MainActivity.mSites.get(position).getSite_free_bonus_hour_time());

            siteHolder.secondStarter.continueServicetimer(siteHolder.sitename.getText().toString());

            Log.d("HardSkins", "On bindHolder SUCCESS" + siteHolder.sitename.getText().toString());
        } else {
            siteHolder.switchNotify.setChecked(false);
            siteHolder.textDate.setVisibility(View.INVISIBLE);
            Log.d("HardSkins", "On bindHolder FALSE " + siteHolder.sitename.getText().toString());
            try{
                siteHolder.t.cancel();
            } catch (NullPointerException e){

            }
            siteHolder.secondStarter.stopServiceTimer(position);



        }


        onBind = false;
    }




    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    public SiteAdapter(List<Site> sites, Context context, TimerStarter timerStarter) {
        SiteAdapter.sites = sites;
        this.context = context;
        firstStarter = timerStarter;


    }


}