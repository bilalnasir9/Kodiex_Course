package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kodiexcourseapp.adapters.adapter_allcourses;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class allcourses_activity extends AppCompatActivity {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin");
    List<String> list_courses_keys = new ArrayList<>();
    List<String> list_image_url = new ArrayList<>();
    List<String> list_instructor = new ArrayList<>();
    List<String> list_lectures = new ArrayList<>();
    List<String> list_level = new ArrayList<>();
    List<String> list_price = new ArrayList<>();
    List<String> list_rating = new ArrayList<>();
    List<String> list_subject = new ArrayList<>();
    List<String> list_title = new ArrayList<>();
    ProgressDialog progressDialog;
    adapter_allcourses adapter_allcourses;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allcourses);
        recyclerView = findViewById(R.id.recycler_allcourses);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        reference.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    try {
                    String courses_key = snap.getKey();
                    list_courses_keys.add(courses_key);
                    assert courses_key != null;
                    String url = Objects.requireNonNull(snap.child("image_url").getValue()).toString();
                    String instructor = Objects.requireNonNull(snap.child("instructor").getValue()).toString();
                    String lectures = Objects.requireNonNull(snap.child("lectures").getValue()).toString();
                    String level = Objects.requireNonNull(snap.child("level").getValue()).toString();
                    String price = Objects.requireNonNull(snap.child("price").getValue()).toString();
                    String rating = Objects.requireNonNull(snap.child("rating").getValue()).toString();
                    String subject = Objects.requireNonNull(snap.child("subject").getValue()).toString();
                    String title = Objects.requireNonNull(snap.child("title").getValue()).toString();
                    list_image_url.add(url);
                    list_instructor.add(instructor);
                    list_lectures.add(lectures);
                    list_level.add(level);
                    list_price.add(price);
                    list_rating.add(rating);
                    list_subject.add(subject);
                    list_title.add(title);
                } catch (Exception e){
//           Toast.makeText(allcourses_activity.this, "Course not found", Toast.LENGTH_SHORT).show();

                    }
                }
                allcourses_modelClass modelClass = new allcourses_modelClass(list_image_url,list_courses_keys,
                        list_instructor, list_lectures, list_level, list_price, list_rating, list_subject, list_title);
                recyclerView.setLayoutManager(new LinearLayoutManager(allcourses_activity.this));
                adapter_allcourses = new adapter_allcourses(allcourses_activity.this, modelClass);
                recyclerView.setAdapter(adapter_allcourses);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }
}