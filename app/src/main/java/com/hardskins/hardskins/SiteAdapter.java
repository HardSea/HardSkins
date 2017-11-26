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

import java.util.Date;
import java.util.List;
import java.util.Timer;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteHolder>{

    private List<Site> sites;
    private Context context;
    private static Date now;
    private static Date then;
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
        CountDownTimer t;



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
                        Site tempSite = sites.get(getIndexByname(String.valueOf(sitename.getText())));
                        switchOn(Long.parseLong(tempSite.getSite_free_bonus_hour_time()));

                    } else {
                        switchOff();
                        t.cancel();
                    }
                }
            });

        }


        int getIndexByname(String pName)
        {
            for(Site _item : sites)
            {
                if(_item.getName().equals(pName))
                    return sites.indexOf(_item);
            }
            return -1;
        }


        void startTimer( long time) {
            long tick = 1000;
            long finish = time * 60000;
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

        private void switchOn(long time){
            Log.d("HardSkins", "Switch on clicked!");
            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefsEditor = appSharedPrefs.edit();
            prefsEditor.putInt("notify_site"+sitename, getAdapterPosition());
            prefsEditor.apply();
            Toast.makeText(context,"Switcher ON", Toast.LENGTH_SHORT).show();
            textDate.setVisibility(View.VISIBLE);
            notifyItemMoved(getAdapterPosition(), 0);
            startTimer(time);
        }

        private void switchOff(){
            Log.d("HardSkins", "Switch off clicked!");
            appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Toast.makeText(context,"Switcher OFF", Toast.LENGTH_SHORT).show();
            notifyItemMoved(getAdapterPosition(), appSharedPrefs.getInt("notify_site"+sitename, getAdapterPosition()));
            textDate.setVisibility(View.INVISIBLE);
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
                Log.d("HardSkins", "Long tap on linear layout!");
                switchNotify.setChecked(true);
                Site tempSite = sites.get(getIndexByname(String.valueOf(sitename.getText())));
                switchOn(Long.parseLong(tempSite.getSite_free_bonus_hour_time()));
                Toast.makeText(context, "Уведомления включено", Toast.LENGTH_SHORT).show();
                return true;
            }

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