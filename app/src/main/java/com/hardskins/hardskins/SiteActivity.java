package com.hardskins.hardskins;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class SiteActivity extends AppCompatActivity {


    TextView textDate;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        final Context context = this;
        Intent intent = getIntent();
        final int position = intent.getIntExtra("position", 0);

        TextView nameSite = findViewById(R.id.info_site_name);
        nameSite.setText(MainActivity.mSites.get(position).getName());

        final EditText editTextSite = findViewById(R.id.editText);
        editTextSite.setText(MainActivity.mSites.get(position).getAddress());

        ImageView imageView = findViewById(R.id.imageView);
        Picasso mPicasso = Picasso.with(this);

        mPicasso.load(MainActivity.mSites.get(position).getSite_photo_url()).into(imageView);


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


}
