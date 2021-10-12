package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.kodiexcourseapp.adapters.adapter_allcourses;
import com.example.kodiexcourseapp.adapters.adapter_enrolled_courses;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class enrolled_courses_activity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String userid;
    Context context = enrolled_courses_activity.this;
    List<String> list_courses_keys = new ArrayList<>();
    List<String> list_title = new ArrayList<>();
    List<String> list_subject = new ArrayList<>();
    List<String> list_price = new ArrayList<>();
    List<String> list_instructor = new ArrayList<>();
    List<String> list_rating = new ArrayList<>();
    List<String> list_url = new ArrayList<>();
    List<String> list_lecturesprogress = new ArrayList<>();
    List<String> list_lectures = new ArrayList<>();

    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_courses);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        recyclerView = findViewById(R.id.recyclerview_enrolled_courses);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        reference.child(userid).child("enrolled_courses").get().addOnCompleteListener(task -> {
            if (task.getResult().getValue()!=null){
                reference.child(userid).child("enrolled_courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        for (DataSnapshot snapshot : snap.getChildren()) {
                            String key = snapshot.getKey();
                            try {
                                String instructor = Objects.requireNonNull(snapshot.child("instructor").getValue()).toString();
                                String rating = Objects.requireNonNull(snapshot.child("rating").getValue()).toString();
                                String price = Objects.requireNonNull(snapshot.child("price").getValue()).toString();
                                String title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
                                String subject = Objects.requireNonNull(snapshot.child("subject").getValue()).toString();
                                String url = Objects.requireNonNull(snapshot.child("image_url").getValue()).toString();
                                String progress = Objects.requireNonNull(snapshot.child("lectures_progress").getValue()).toString();
                                String lectures = Objects.requireNonNull(snapshot.child("lectures").getValue()).toString();
                                list_instructor.add(instructor);
                                list_courses_keys.add(key);
                                list_rating.add(rating);
                                list_price.add(price);
                                list_title.add(title);
                                list_subject.add(subject);
                                list_url.add(url);
                                list_lecturesprogress.add(progress);
                                list_lectures.add(lectures);

                            } catch (Exception exception) {
//                                Toast.makeText(context, "You have not enrolled any course", Toast.LENGTH_SHORT).show();
                            }
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        adapter_enrolled_courses adapter = new adapter_enrolled_courses(context, list_title, list_subject, list_instructor, list_price, list_rating,list_url,list_courses_keys,list_lecturesprogress,list_lectures);
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
                list_instructor.clear();
                list_courses_keys.clear();
                list_rating.clear();
                list_price.clear();
                list_title.clear();
                list_subject.clear();
                list_url.clear();
                list_lecturesprogress.clear();
                list_lectures.clear();
            }
            else {
                Toast.makeText(context, "You have not enrolled any course", Toast.LENGTH_SHORT).show();
           progressDialog.dismiss();
            }
        });


    }
}