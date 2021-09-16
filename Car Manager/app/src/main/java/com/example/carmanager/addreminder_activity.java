package com.example.carmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Calendar.*;

public class addreminder_activity extends AppCompatActivity {
    TextView tvdays, tvtime;
    EditText ettitle, etmessage;
    int hours, minuts;

    List<String> getselecteddays = new ArrayList<>();
    @Override
    public boolean startActivityIfNeeded(@NonNull Intent intent, int requestCode) {
        return super.startActivityIfNeeded(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreminder);
        tvdays = findViewById(R.id.tvdate_addreminder);
        tvtime = findViewById(R.id.tvtime_addreminder);
        ettitle = findViewById(R.id.ettitle_addreminder);
        etmessage = findViewById(R.id.etdescription_addreminder);

    }

    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(this, dashboard_activity.class));
    }

    public void addreminderclicked(View view) {
        if (TextUtils.isEmpty(tvdays.getText().toString())) {
            tvdays.setError("Input requires");
        } else if (TextUtils.isEmpty(tvtime.getText().toString())) {
            tvtime.setError("Input requires");
        } else if (TextUtils.isEmpty(ettitle.getText().toString())) {
            ettitle.setError("Input requires");
        } else if (TextUtils.isEmpty(etmessage.getText().toString())) {
            etmessage.setError("Input requires");
        }
        else {
            staartalarm();
        }
    }

    private void staartalarm() {

        Calendar startTime = Calendar.getInstance();
        Intent intent = new Intent(addreminder_activity.this, AlarmReceiver.class);
        int notificationId = 1;
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("message", etmessage.getText().toString());
        intent.putExtra("title", ettitle.getText().toString());
        intent.putStringArrayListExtra("selecteddays", (ArrayList<String>) getselecteddays);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        startTime.setTimeInMillis(System.currentTimeMillis());
        startTime.set(HOUR_OF_DAY, hours);
        startTime.set(MINUTE, minuts);
        startTime.set(SECOND, 0);
        long alarmStartTime = startTime.getTimeInMillis();
//        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,alarmStartTime,
                AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Alarm is set Successfully", Toast.LENGTH_SHORT).show();
    }
    public void choosetime_addreminder(View view) {
        Calendar myCalendar = getInstance();
        int hour = myCalendar.get(HOUR_OF_DAY);
        int minute = myCalendar.get(MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(addreminder_activity.this, (timePicker, selectedHour, selectedMinute) -> {
            if (selectedMinute < 10) {
                tvtime.setText(selectedHour + ":0" + selectedMinute);
                hours = selectedHour;
            }
            else {
                tvtime.setText(selectedHour + ":" + selectedMinute);
            }
            if (selectedHour == 0) {
                tvtime.setText("12" + ":" + selectedMinute);
                hours = 12;
                minuts = selectedMinute;
            }
            if (selectedHour != 0) {
                tvtime.setText(selectedHour + ":" + selectedMinute);
                hours = selectedHour;
                minuts = selectedMinute;
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void choosedays_addreminder(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Days").setCancelable(false);
        String[] days = new String[]{
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        };
        // Boolean array for initial selected items
        final boolean[][] checkeddays = {new boolean[]{
                false, // sunday
                false, // monday
                false, // tuesday
                false, // wednesday
                false,// thursday
                false,// friday
                false // saturday

        }};
        final List<String> daysList = Arrays.asList(days);
        builder.setMultiChoiceItems(days, checkeddays[0], (dialog, which, isChecked) -> {

            // Update the current focused item's checked status
            checkeddays[0][which] = isChecked;

        });

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            getselecteddays.clear();
            StringBuilder stringBuilder = new StringBuilder();
            for (int k = 0; k < checkeddays[0].length; k++) {
                boolean checked = checkeddays[0][k];
                if (checked) {
                    stringBuilder.append(daysList.get(k));
                    tvdays.setText(stringBuilder.toString());
                    stringBuilder.append(",");
                    getselecteddays.add(daysList.get(k));
                }
            }

        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
        });

        builder.show();
    }
}