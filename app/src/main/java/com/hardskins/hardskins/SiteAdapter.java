package com.hardskins.hardskins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateUtils;
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

import java.util.Date;
import java.util.List;
import java.util.Timer;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteHolder>{

    private List<Site> sites;
    private Context context;
    private static Date now;
    private static Date then;
    CountDownTimer t;
    Timer mTimer;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;





    class SiteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        CardView cardView;
        TextView sitename;
        ImageView sitePhoto;
        SwitchCompat switchNotify;
        TextView textDate;
        LinearLayout linearCard;


        SiteHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            sitename = itemView.findViewById(R.id.site_name);
            sitePhoto = itemView.findViewById(R.id.site_photo);
            switchNotify = itemView.findViewById(R.id.notificationSwitcher);
            textDate = itemView.findViewById(R.id.textDate);
            linearCard = itemView.findViewById(R.id.linearCard);
            linearCard.setOnClickListener(this);
            linearCard.setOnLongClickListener(this);
            switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        prefsEditor = appSharedPrefs.edit();
                        prefsEditor.putInt("notify_site"+sitename, getAdapterPosition());
                        prefsEditor.apply();
                        Toast.makeText(context,"Switcher ON", Toast.LENGTH_SHORT).show();
                        textDate.setVisibility(View.VISIBLE);
                        notifyItemMoved(getAdapterPosition(), 0);
                        startTimer(1000);
                    } else {
                        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        Toast.makeText(context,"Switcher OFF", Toast.LENGTH_SHORT).show();
                        t.cancel();
                        notifyItemMoved(getAdapterPosition(), appSharedPrefs.getInt("notify_site"+sitename, 0));
                        textDate.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }





        void startTimer(long tick) {
            final long finish = 60000 * (Long.parseLong(sites.get(getAdapterPosition()).getSite_free_bonus_hour_time()));
            t = new CountDownTimer(finish, tick) {

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

                    String yy = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
                    textDate.setText(yy);

                }

                public void onFinish() {
                    textDate.setText("00:00:00");
                    Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();

                    cancel();
                }
            }.start();
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
            Log.d("HardSkins", "Long click in siteHolder");
            switchNotify = itemView.findViewById(R.id.notificationSwitcher);
            Toast.makeText(context, "Уведомления long tap", Toast.LENGTH_SHORT).show();
            if (switchNotify.isChecked()){
                Toast.makeText(context, "Уведомления уже включены", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                showTextDate(getAdapterPosition());
                switchNotify.setChecked(true);
                Toast.makeText(context, "Уведомления включено", Toast.LENGTH_SHORT).show();
                return true;
            }

        }

        void showTextDate(int position){
            MainActivity.mSites.get(position).setSite_isnotify("1");

            textDate.setVisibility(View.VISIBLE);
            now = new Date();
            then = MainActivity.addMinutesToDate(Integer.parseInt(sites.get(position).getSite_free_bonus_hour_time()), now);

            String remaining1 = DateUtils.formatElapsedTime ((then.getTime() - now.getTime())/1000);
            textDate.setText(remaining1);
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

            prefsEditor.putLong(sites.get(position).getName(), now.getTime());
            prefsEditor.apply();



        }


    }






    @Override
    public int getItemCount() {
        return sites.size();
    }

    @Override
    public SiteHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new SiteHolder(v);
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





    }




    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    SiteAdapter(List<Site> sites, Context context) {
        this.sites = sites;
        this.context = context;
    }



}