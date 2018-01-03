package com.hardskins.hardskins.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.hardskins.hardskins.R;

import java.text.NumberFormat;

import static com.hardskins.hardskins.R.string.tilda;

public class CalcActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        final TextView textMoneyDay = findViewById(R.id.text_on_day);
        final TextView textMoneyWeek = findViewById(R.id.text_on_week);
        final TextView textMoneyMonth = findViewById(R.id.text_on_month);

        final CheckBox isSleepCheckBox = findViewById(R.id.checkbox_sleep);

        final EditText cntOfHour = findViewById(R.id.cnt_of_hour);
        final EditText cntOfMoney = findViewById(R.id.cnt_of_money);

        isSleepCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = 24;
                if (cntOfMoney.getText() != null) {


                    if (!cntOfHour.getText().toString().equals("") && !cntOfMoney.getText().toString().equals("")) {
                        if (isSleepCheckBox.isChecked()) {
                            hour -= 8;
                        }
                        String textInMoney = cntOfMoney.getText().toString().replace(",", ".");
                        String textInHour = cntOfHour.getText().toString().replace(",", ".");
                        double cntOfHourLong = Double.parseDouble(textInHour);
                        double cntOfMoneyLong = Double.parseDouble(textInMoney);
                        double cntInDay = ((hour / cntOfHourLong) * cntOfMoneyLong);
                        double cntInWeek = (((hour * 7) / cntOfHourLong) * cntOfMoneyLong);
                        double cntInMonth = (((hour * 30) / cntOfHourLong) * cntOfMoneyLong);


                        NumberFormat formatter = NumberFormat.getInstance();
                        formatter.setMaximumFractionDigits(2);

                        textMoneyDay.setText(String.format("%s%s", getString(tilda), formatter.format(cntInDay)));
                        textMoneyWeek.setText(String.format("%s%s", getString(tilda), formatter.format(cntInWeek)));
                        textMoneyMonth.setText(String.format("%s%s", getString(tilda), formatter.format(cntInMonth)));


                    } else {
                        cntOfHour.setText("");
                    }

                }
            }


        });


        cntOfHour.addTextChangedListener(new TextWatcher() {
            boolean flag = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int hour = 24;
                if (!flag) {
                    flag = true;

                    if (cntOfMoney.getText() != null) {


                            if (!cntOfHour.getText().toString().equals("") && !cntOfMoney.getText().toString().equals("")) {
                                if (cntOfHour.getText().toString().equals("24")){
                                    isSleepCheckBox.setChecked(false);
                                }

                                if (isSleepCheckBox.isChecked()) {
                                    hour -= 8;
                                }
                                String textInMoney = cntOfMoney.getText().toString().replace(",", ".");
                                String textInHour = cntOfHour.getText().toString().replace(",", ".");
                                double cntOfHourLong = Double.parseDouble(textInHour);
                                double cntOfMoneyLong = Double.parseDouble(textInMoney);
                                double cntInDay = ((hour / cntOfHourLong) * cntOfMoneyLong);
                                double cntInWeek = (((hour * 7) / cntOfHourLong) * cntOfMoneyLong);
                                double cntInMonth = (((hour * 30) / cntOfHourLong) * cntOfMoneyLong);


                                NumberFormat formatter = NumberFormat.getInstance();
                                formatter.setMaximumFractionDigits(2);

                                textMoneyDay.setText(String.format("%s%s", getString(tilda), formatter.format(cntInDay)));
                                textMoneyWeek.setText(String.format("%s%s", getString(tilda), formatter.format(cntInWeek)));
                                textMoneyMonth.setText(String.format("%s%s", getString(tilda), formatter.format(cntInMonth)));


                            } else {
                                cntOfHour.setText("");
                            }
                        }


                    flag = false;
                }
            }
        });


        cntOfMoney.addTextChangedListener(new TextWatcher() {
            boolean flag = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int hour = 24;
                if (!flag) {
                    flag = true;

                    if (cntOfMoney.getText() != null) {



                        if (!cntOfHour.getText().toString().equals("") && !cntOfMoney.getText().toString().equals("")) {
                            if (cntOfHour.getText().toString().equals("24")){
                                isSleepCheckBox.setChecked(false);
                            }

                            if (isSleepCheckBox.isChecked()) {
                                hour -= 8;
                            }
                            String textInMoney = cntOfMoney.getText().toString().replace(",", ".");
                            String textInHour = cntOfHour.getText().toString().replace(",", ".");
                            double cntOfHourLong = Double.parseDouble(textInHour);
                            double cntOfMoneyLong = Double.parseDouble(textInMoney);
                            double cntInDay = ((hour / cntOfHourLong) * cntOfMoneyLong);
                            double cntInWeek = (((hour * 7) / cntOfHourLong) * cntOfMoneyLong);
                            double cntInMonth = (((hour * 30) / cntOfHourLong) * cntOfMoneyLong);


                            NumberFormat formatter = NumberFormat.getInstance();
                            formatter.setMaximumFractionDigits(2);

                            textMoneyDay.setText(String.format("%s%s", getString(tilda), formatter.format(cntInDay)));
                            textMoneyWeek.setText(String.format("%s%s", getString(tilda), formatter.format(cntInWeek)));
                            textMoneyMonth.setText(String.format("%s%s", getString(tilda), formatter.format(cntInMonth)));


                        } else {
                            cntOfHour.setText("");
                        }

                }

                flag = false;
            }
        }
    });


}


    public void closeActivity(View view) {
        finish();
    }
}
