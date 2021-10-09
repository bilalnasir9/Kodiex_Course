package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kodiexcourseapp.adapters.adapter_announcement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class announcement_activity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ProgressDialog dialog;
    List<String> listtile = new ArrayList<>();
    List<String> listdetail = new ArrayList<>();
    List<String> listdays = new ArrayList<>();
    String current_datetime;
    adapter_announcement adapter;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        recyclerView = findViewById(R.id.recyclerview_announcement);
        dialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Please wait");
        dialog.show();
        current_datetime = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").
                format(Calendar.getInstance().getTime());
        reference.child("admin").child("announcement").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
//                        String key = ds.getKey();
                        String title = Objects.requireNonNull(ds.child("title").getValue()).toString();
                        String detail = Objects.requireNonNull(ds.child("detail").getValue()).toString();
                        String prev_date = Objects.requireNonNull(ds.child("date").getValue()).toString();
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                        String inputString1 = current_datetime;
                        Date date1 = myFormat.parse(inputString1);
                        Date date2 = myFormat.parse(prev_date);
                        long diff = Objects.requireNonNull(date1).getTime() - Objects.requireNonNull(date2).getTime();
                        String result = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                        listtile.add(title);
                        listdetail.add(detail);
                        if (result.equals("0")) {
                            listdays.add("Today");
                        } else {
                            listdays.add(result + " Days ago");
                        }
                    } catch (ParseException e) {
                        Toast.makeText(announcement_activity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                Collections.reverse(listtile);
                Collections.reverse(listdays);
                Collections.reverse(listdetail);

                recyclerView.setLayoutManager(new LinearLayoutManager(announcement_activity.this));
                adapter = new adapter_announcement(announcement_activity.this, listtile, listdetail, listdays);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }

            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

}