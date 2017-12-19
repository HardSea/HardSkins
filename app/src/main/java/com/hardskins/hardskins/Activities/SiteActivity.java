package com.hardskins.hardskins.Activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hardskins.hardskins.R;
import com.hardskins.hardskins.Site;
import com.squareup.picasso.Picasso;

public class SiteActivity extends AppCompatActivity {



    private Site mySite;
    public CheckBox dontShowAgain;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        final Context context = this;
        Intent intent = getIntent();
        final int position = intent.getIntExtra("position", 0);

        mySite = MainActivity.mSites.get(position);

        TextView nameSite = findViewById(R.id.info_site_name);
        nameSite.setText(mySite.getName());

        final EditText editTextSite = findViewById(R.id.editText);
        editTextSite.setText(mySite.getAddress());
        editTextSite.setKeyListener(null);
        editTextSite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", editTextSite.getText().toString());
                assert cm != null;
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Адрес сайта скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ImageView imageView = findViewById(R.id.imageView);
        Picasso mPicasso = Picasso.with(this);

        mPicasso.load(mySite.getSite_photo_url()).into(imageView);


        Button copyBtn = findViewById(R.id.copy_btn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", editTextSite.getText().toString());
                assert cm != null;
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Адрес сайта скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
            }
        });









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
