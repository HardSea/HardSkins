package com.hardskins.hardskins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteHolder> {

    static List<Site> sites;
    private Context context;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private TimerStarter firstStarter;
    private boolean onBind;



    class SiteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private CardView cardView;
        private TextView sitename;
        private ImageView sitePhoto;
        SwitchCompat switchNotify;
        private TextView textDate;
        private LinearLayout linearCard;
        private CountDownTimer t;
        private TimerStarter secondStarter;
        private String locale = "%02d:%02d:%02d";


        SiteHolder(View itemView, TimerStarter timerStarter) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            sitename = itemView.findViewById(R.id.site_name);
            sitePhoto = itemView.findViewById(R.id.site_photo);
            switchNotify = itemView.findViewById(R.id.notificationSwitcher);
            textDate = itemView.findViewById(R.id.textDate);
            linearCard = itemView.findViewById(R.id.linearCard);
            linearCard.setOnClickListener(this);
            linearCard.setOnLongClickListener(this);
            secondStarter = timerStarter;



            switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!onBind) {
                        int tempPosition = getIndexByName(String.valueOf(sitename.getText()));
                        Site tempSite = sites.get(tempPosition);
                        if (b) {
                            switchOn(Long.parseLong(tempSite.getSite_free_bonus_hour_time()));
                            secondStarter.startServiceTimer(tempPosition);

                        } else {
                            switchOff();
                            t.cancel();
                            secondStarter.stopServiceTimer(tempPosition);

                        }
                    }

                }
            });

            textDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });





        }


        void startTimer(long time, final int position) {
            long tick = 1000;

            t = new CountDownTimer(time, tick) {

                public void onTick(long millisUntilFinished) {
                    //     long remainedSecs = millisUntilFinished / 1000;
                    //    textDate.setText( + ":" + (remainedSecs / 60) + ":" + (remainedSecs % 60));// manage it accordign to you

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
                    secondStarter.showNotification(position);
                    cancel();
                    // switchNotify.setChecked(false);
                }
            }.start();


        }

        private void switchOn(long time) {
            sites.get(getIndexByName(String.valueOf(sitename.getText()))).setSite_isnotify("1");
            MainActivity.mSites.get(getIndexByName(String.valueOf(sitename.getText()))).setSite_isnotify("1");
            Log.d("HardSkins", "Switch on clicked!");
            Toast.makeText(context, "Switcher ON", Toast.LENGTH_SHORT).show();
            textDate.setVisibility(View.VISIBLE);
            // notifyItemMoved(getAdapterPosition(), 0); //working not correct
            startTimer(time, getIndexByName(String.valueOf(sitename.getText())));
            MainActivity.cnt_timer++;
        }

        private void switchOff() {
            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefsEditor = appSharedPrefs.edit();
            prefsEditor.putLong(String.valueOf(sitename.getText()) + "time to notify", 0);

            // sites.get(getIndexByName(String.valueOf(sitename.getText()))).setSite_time_to_notify(0);
            // MainActivity.mSites.get(getIndexByName(String.valueOf(sitename.getText()))).setSite_time_to_notify(0);

            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefsEditor = appSharedPrefs.edit();
            prefsEditor.remove(context.getString(R.string.starttimertimer) + sitename.getText());
            sites.get(getIndexByName(String.valueOf(sitename.getText()))).setSite_isnotify("0");
            MainActivity.mSites.get(getIndexByName(String.valueOf(sitename.getText()))).setSite_isnotify("0");
            Log.d("HardSkins", "Switch off clicked!");
            //     appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Toast.makeText(context, "Switcher OFF", Toast.LENGTH_SHORT).show();
            //     notifyItemMoved(getAdapterPosition(), appSharedPrefs.getInt("notify_site"+sitename, getAdapterPosition()));
            textDate.setVisibility(View.INVISIBLE);
            sites.get(getIndexByName(String.valueOf(sitename.getText()))).setSite_isnotify("0");
            MainActivity.mSites.get(getIndexByName(String.valueOf(sitename.getText()))).setSite_isnotify("0");
            prefsEditor.apply();
            MainActivity.cnt_timer--;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, SiteActivity.class);
            intent.putExtra("position", getAdapterPosition());
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
                Site tempSite = sites.get(getIndexByName(String.valueOf(sitename.getText())));
                switchOn(Long.parseLong(tempSite.getSite_free_bonus_hour_time()));
                Toast.makeText(context, "Уведомления включено", Toast.LENGTH_SHORT).show();
                return true;
            }

        }


    }


    static void cleanList() {
        sites.clear();
    }

    static void setSites(List<Site> sites1) {
        sites = sites1;
    }


    private int getIndexByName(String pName) {
        for (Site _item : sites) {
            if (_item.getName().equals(pName))
                return sites.indexOf(_item);
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
    public void onBindViewHolder(final SiteHolder siteHolder, final int position) {
        int t = sites.size();
        for (int i = 0; i < t; i++) {
            siteHolder.sitename.setText(sites.get(position).getSite_name());

            Picasso.with(context)
                    .load(sites.get(position).getSite_photo_url())
                    .into(siteHolder.sitePhoto);

            i++;
        }

        onBind = true;

        if (sites.get(position).getSite_isnotify()) {
            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefsEditor = appSharedPrefs.edit();
            siteHolder.switchNotify.setChecked(true);
            int tempPosition = getIndexByName(String.valueOf(siteHolder.sitename.getText()));
            //    Site tempSite = sites.get(tempPosition);
            Log.d("HardSkins", "Position: " + position);
            long timeLastClose = appSharedPrefs.getLong("LastCloseAppTime", 0);
            long timeLastOpen = appSharedPrefs.getLong("LastOpenTime", 0);
            //  long timeToNotify = sites.get(tempPosition).getSite_time_to_notify();
            long timeToNotify = appSharedPrefs.getLong(MainActivity.mSites.get(tempPosition).getSite_name() + "time to notify", 0);
            long timeRemaning = timeLastOpen - timeLastClose;
            long setTime;
            if (timeToNotify - timeRemaning >= 0) {
                setTime = timeToNotify - timeRemaning;
            } else {
                setTime = 500;

            }

            prefsEditor.putLong(MainActivity.mSites.get(tempPosition).getSite_name() + "time to notify", setTime);
            prefsEditor.apply();

            //   sites.get(tempPosition).setSite_time_to_notify(setTime);
            //   MainActivity.mSites.get(tempPosition).setSite_time_to_notify(setTime);


            siteHolder.textDate.setVisibility(View.VISIBLE);


            siteHolder.startTimer(appSharedPrefs.getLong(MainActivity.mSites.get(tempPosition).getSite_name() + "time to notify", 0),
                    getIndexByName(String.valueOf(siteHolder.sitename.getText())));

            siteHolder.secondStarter.continueServicetimer(tempPosition);


        }


        onBind = false;


    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    SiteAdapter(List<Site> sites, Context context, TimerStarter timerStarter) {
        SiteAdapter.sites = sites;
        this.context = context;
        firstStarter = timerStarter;

    }


}