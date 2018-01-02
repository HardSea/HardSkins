package com.hardskins.hardskins.Activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hardskins.hardskins.R;
import com.hardskins.hardskins.Site;

public class SiteActivity extends AppCompatActivity {



    private Site mySite;
    public CheckBox dontShowAgain;
    final Context context = this;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        Intent intent = getIntent();
        final int position = intent.getIntExtra("position", 0);

        mySite = MainActivity.mSites.get(position);



        initializeTextView();

        initializeEditText();




        ScrollView scrollView = findViewById(R.id.scroll);

        AnimationDrawable animationDrawable = (AnimationDrawable) scrollView.getBackground();
        animationDrawable.setEnterFadeDuration(4000);
        animationDrawable.setExitFadeDuration(10000);
        animationDrawable.start();










    }

    private void initializeEditText() {

        final EditText edit_text_site_link = findViewById(R.id.editText_site_link);
        edit_text_site_link.setText(mySite.getAddress());
        edit_text_site_link.setKeyListener(null);
        edit_text_site_link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", edit_text_site_link.getText().toString());
                assert cm != null;
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Адрес сайта скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        final EditText edit_text_ref_code = findViewById(R.id.editText_ref_code);
        if (mySite.getSite_ref_code().equals("")){
            edit_text_ref_code.setText(mySite.getSite_ref_link());

        }else{
            edit_text_ref_code.setText(mySite.getSite_ref_code());
        }

        edit_text_ref_code.setKeyListener(null);
        edit_text_ref_code.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", edit_text_ref_code.getText().toString());
                assert cm != null;
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Адрес сайта скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
                return true;
            }
        });



        Button copyBtn_site_name = findViewById(R.id.copy_btn_site_name);
        copyBtn_site_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", edit_text_site_link.getText().toString());
                assert cm != null;
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Адрес сайта скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
            }
        });

        Button copyBtn_ref_code = findViewById(R.id.copy_btn_ref_code);
        copyBtn_ref_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", edit_text_ref_code.getText().toString());
                assert cm != null;
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Бонус для сайта скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeTextView(){
        TextView nameSite = findViewById(R.id.info_site_name);
        nameSite.setText(mySite.getName());


        TextView ref_bonus_count_text = findViewById(R.id.ref_bonus_count_text);
        ref_bonus_count_text.setText(mySite.getSite_free_bonus_reg_count() + "$");

        TextView is_daily_bonus = findViewById(R.id.is_daily_bonus);
        is_daily_bonus.setText(mySite.getSite_free_bonus_hour());

        TextView daily_bonus_time = findViewById(R.id.daily_bonus_time);
        daily_bonus_time.setText(mySite.getSite_free_bonus_hour_time_text());

        TextView daily_bonus_count = findViewById(R.id.daily_bonus_count);
        daily_bonus_count.setText(mySite.getSite_free_bonus_hour_count());

        TextView withdraw_dep = findViewById(R.id.withdraw_dep);
        if (mySite.getSite_no_need_dep()){
            withdraw_dep.setText(R.string.text_yes);
        } else {
            withdraw_dep.setText(R.string.text_no);
        }

        TextView bonus_for_reg = findViewById(R.id.text_for_promo);
        if (mySite.getSite_ref_code().equals("")){
            bonus_for_reg.setText("Бонус за регистрацию по ссылке: ");

        }
    }



    public void closeActivity(View view) {
        finish();
    }


    public void openinbrowser(View view) {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SiteActivity.this);
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

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SiteActivity.this);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.apply();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mySite.getSite_address()));
                startActivity(browserIntent);

            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SiteActivity.this);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.apply();

                // Do what you want to do on "CANCEL" action

            }
        });

        if (!skipMessage.equals("checked")) {
            adb.show();
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mySite.getSite_address()));
            startActivity(browserIntent);
        }



    }
}
