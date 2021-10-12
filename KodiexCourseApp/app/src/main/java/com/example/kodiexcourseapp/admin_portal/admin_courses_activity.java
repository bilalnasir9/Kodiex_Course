package com.example.kodiexcourseapp.admin_portal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.kodiexcourseapp.R;
import com.example.kodiexcourseapp.adapters.adapter_admin_courses;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class admin_courses_activity extends AppCompatActivity {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    List<String> list_title = new ArrayList<>();
    List<String> list_coursekey = new ArrayList<>();
    List<String> list_url = new ArrayList<>();
    RecyclerView recyclerView;
    Context context = admin_courses_activity.this;
    FloatingActionButton floatingActionButton_addcourse;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_courses);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        recyclerView = findViewById(R.id.recycler_admin_allcourses);
        floatingActionButton_addcourse = findViewById(R.id.floatingActionButton_newcourse);
        reference.child("admin").child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String key = Objects.requireNonNull(Objects.requireNonNull(ds.getKey()).toString());
                        String title = Objects.requireNonNull(ds.child("title").getValue()).toString();
                        String url = Objects.requireNonNull(ds.child("image_url").getValue()).toString();
                        list_coursekey.add(key);
                        list_title.add(title);
                        list_url.add(url);
                    } catch (Exception ignored) {
//                        Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();

                    }

                }
                recyclerView.setLayoutManager(new LinearLayoutManager(admin_courses_activity.this));
                adapter_admin_courses adapter = new adapter_admin_courses(context, list_coursekey, list_title, list_url);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        floatingActionButton_addcourse.setOnClickListener(view -> {
            reference.child("admin").child("counter_allcourses").get().addOnCompleteListener(task -> {
                try {
                    int key = Integer.parseInt(Objects.requireNonNull(task.getResult().getValue()).toString());
                    String course_key = String.valueOf(key + 1);
                    add_new_course(course_key);
                } catch (NullPointerException exception) {
                    reference.child("admin").child("counter_allcourses").setValue("1");
                    add_new_course("1");
                }

            });


        });
    }

    private void add_new_course(String course_key) {
        Intent intent = new Intent(this, admin_add_new_course_activity.class);
        intent.putExtra("course_key", course_key);
        startActivity(intent);
    }


}