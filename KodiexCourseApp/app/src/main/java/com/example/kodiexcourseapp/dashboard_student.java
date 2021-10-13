package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.kodiexcourseapp.admin_portal.announcement_activity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class dashboard_student extends AppCompatActivity {
    ImageView imageView_userprofile;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String name, email, userid, contact;
    ProgressDialog progressDialog;
    Uri profileurl;
    TextView tv_completedcourse, tvenrolledcourse;
    BarChart barChart;
    // variable for our bar data.
    BarData barData;
    // variable for our bar data set.
    BarDataSet barDataSet;
    // array list for storing entries.
    List<BarEntry> barEntriesArrayList = new ArrayList<>();
    List<String> list_keys = new ArrayList<>();
    List<String> list_values = new ArrayList<>();
    int total=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_student);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait");
        tv_completedcourse = findViewById(R.id.textview_dashborad_completedcourse);
        tvenrolledcourse = findViewById(R.id.textview_dashborad_enrolledcourse);
        imageView_userprofile = findViewById(R.id.imageview_dashboarduserprofile);

        barChart = findViewById(R.id.verticalbarchart_chart);


        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        reference.child("admin").child("enrolled_status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 1;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
//                        String key = ds.getKey();
                        String value = Objects.requireNonNull(ds.child("total_enrolled").getValue()).toString();
                        list_keys.add(String.valueOf(counter));
                        list_values.add(value);
//                        Toast.makeText(dashboard_student.this, String.valueOf(total), Toast.LENGTH_SHORT).show();
                        counter++;
                    } catch (Exception ignored) {
                    }
                }
                loadChartData();
                createChartData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        reference.child(userid).child("total_enrolled_courses").get().addOnCompleteListener(task -> {
            try {
                String enrolled_course_value = Objects.requireNonNull(task.getResult().getValue()).toString();
                tvenrolledcourse.setText(enrolled_course_value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        reference.child(userid).child("completed_courses").get().addOnCompleteListener(task -> {
            try {
                String value = Objects.requireNonNull(task.getResult().getValue()).toString();
                tv_completedcourse.setText(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        loaddata();
    }

    private void loadChartData() {
        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.

                for (int i=0;i<list_keys.size();i++){
            barEntriesArrayList.add(
                    new BarEntry(Float.parseFloat(list_keys.get(i)), Float.parseFloat(list_values.get(i))));
        }
    }

    public void createChartData() {
        barDataSet = new BarDataSet(barEntriesArrayList, "Courses");
        barDataSet.setDrawValues(false);
        barChart.setDrawBorders(false);
        XAxis xAxis = barChart.getXAxis();
//        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(2000);
    }

    private void loaddata() {
        progressDialog.show();
        reference.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                    name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    contact = Objects.requireNonNull(snapshot.child("contact").getValue()).toString();
                    profileurl = Uri.parse(Objects.requireNonNull(snapshot.child("profile_url").getValue()).toString());
                    if (profileurl.toString().equals("null")) {
                    } else {
                        Glide.with(dashboard_student.this).load(profileurl).into(imageView_userprofile);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usermenu, menu);
        return true;
    }

    public void usermenu_signout_clicked(MenuItem item) {
        auth.signOut();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void usermenu_profilesetting_clicked(MenuItem item) {
        Intent intent = new Intent(this, userprofile_activity.class);
        intent.putExtra("photo_url", profileurl.toString());
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }

    public void button_announcement_clicked(View view) {
        @SuppressLint("SimpleDateFormat")
        String current_datetime = new SimpleDateFormat("ddMMyyyyHHmmss").
                format(Calendar.getInstance().getTime());
        Intent intent = new Intent(this, announcement_activity.class);
        intent.putExtra("date", current_datetime);
        startActivity(intent);
    }

    public void button_allcourses_clicked(View view) {
        startActivity(new Intent(this, allcourses_activity.class));
    }

    public void button_videolectures_clicked(View view) {
        startActivity(new Intent(this, video_lectures_activity.class));


    }

    public void button_enrolledcourses_clicked(View view) {
        startActivity(new Intent(this, enrolled_courses_activity.class));

    }

    public void button_dashboard_refresh(View view) {
        this.recreate();
    }
}