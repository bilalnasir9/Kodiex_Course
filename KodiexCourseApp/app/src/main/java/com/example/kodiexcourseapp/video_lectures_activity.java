package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kodiexcourseapp.adapters.adapter_videolectures;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class video_lectures_activity extends AppCompatActivity {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userid;
    Context context = video_lectures_activity.this;
    List<String> list_enrolledcourse_keys = new ArrayList<>();
    List<String> list_title = new ArrayList<>();
    List<String> list_duration = new ArrayList<>();
    List<String> list_links = new ArrayList<>();
    List<String> list_curriculum_keys = new ArrayList<>();
    List<String> list_counter = new ArrayList<>();
    ConstraintLayout layout_videolecture;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lectures);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait");
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        recyclerView = findViewById(R.id.recycler_videolectures);
        layout_videolecture = findViewById(R.id.constraintLay_videolecture_activity);
        reference.child(userid).child("enrolled_courses").get().addOnCompleteListener(task -> {
            if (task.getResult().getValue() != null) {
                reference.child(userid).child("enrolled_courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                String key = Objects.requireNonNull(ds.getKey()).toString();
                                list_enrolledcourse_keys.add(key);
                            } catch (Exception exception) {
                                Toast.makeText(context, "Error found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(context, "You have not enrolled any course", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void btn_videolecture_course1(View view) {
        try {
            progressDialog.show();
            String course1_id = list_enrolledcourse_keys.get(0);
            layout_videolecture.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            reference.child("admin").child("courses").child(course1_id).child("Curriculum").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter = 1;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        String duration = Objects.requireNonNull(ds.child("duration").getValue()).toString();
                        String link = Objects.requireNonNull(ds.child("link").getValue()).toString();
                        String title = Objects.requireNonNull(ds.child("title").getValue()).toString();
                        list_counter.add(String.valueOf(counter));
                        list_curriculum_keys.add(key);
                        list_title.add(title);
                        list_duration.add(duration);
                        list_links.add(link);
                        counter++;
                    }
                    adapter_videolectures adapter = new adapter_videolectures(context, list_curriculum_keys, list_title, list_duration, list_links, list_counter, course1_id);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception exception) {
            progressDialog.dismiss();
            Toast.makeText(context, "Course 1 not found\n please make sure you have enrolled this", Toast.LENGTH_LONG).show();
        }
    }

    public void btn_videolecture_course2(View view) {
        try {
            progressDialog.show();
            String course2_id = list_enrolledcourse_keys.get(1);
            layout_videolecture.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            reference.child("admin").child("courses").child(course2_id).child("Curriculum").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter = 1;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        String duration = Objects.requireNonNull(ds.child("duration").getValue()).toString();
                        String link = Objects.requireNonNull(ds.child("link").getValue()).toString();
                        String title = Objects.requireNonNull(ds.child("title").getValue()).toString();
                        list_counter.add(String.valueOf(counter));
                        list_curriculum_keys.add(key);
                        list_title.add(title);
                        list_duration.add(duration);
                        list_links.add(link);
                        counter++;
                    }
                    adapter_videolectures adapter = new adapter_videolectures(context, list_curriculum_keys, list_title, list_duration, list_links, list_counter, course2_id);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception exception) {
            progressDialog.dismiss();
            Toast.makeText(context, "Course 2 not found\n please make sure you have enrolled this", Toast.LENGTH_LONG).show();
        }
    }
}