package com.hardskins.hardskins.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hardskins.hardskins.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView helpText = findViewById(R.id.HelpText);

        helpText.setText(Html.fromHtml("<b>HardSkins<b> — приложение помогающее  зарабатывать деньги"));
    }


    public void closeActivity(View view) {
        finish();
    }
}
