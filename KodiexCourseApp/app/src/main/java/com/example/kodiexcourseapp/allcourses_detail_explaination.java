package com.example.kodiexcourseapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kodiexcourseapp.adapters.adapter_curriculum;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class allcourses_detail_explaination extends YouTubeBaseActivity {
    String instructor, lectures, level, price, rating, subject, title, course_key, url;
    List<String> listduration = new ArrayList<>();
    List<String> listlinks = new ArrayList<>();
    List<String> listtitle = new ArrayList<>();
    YouTubePlayerView youTubePlayerView;
    AppCompatButton button_enroll;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allcourses_detail_explaination);
        youTubePlayerView = findViewById(R.id.youtubeplayer);
        button_enroll = findViewById(R.id.btn_allcourses_detail_enroll);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
                player.loadVideo("qhctSdkHRPI");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        youTubePlayerView.initialize(playConfig.API_KEY, onInitializedListener);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        instructor = intent.getStringExtra("instructor");
        lectures = intent.getStringExtra("lectures");
        level = intent.getStringExtra("level");
        price = intent.getStringExtra("price");
        subject = intent.getStringExtra("subject");
        rating = intent.getStringExtra("rating");
        url = intent.getStringExtra("url");

        course_key = intent.getStringExtra("course_key");
        listtitle = intent.getStringArrayListExtra("titlecurriculumlist");
        listduration = intent.getStringArrayListExtra("durationlist");
        listlinks = intent.getStringArrayListExtra("linklist");
        TextView tvtitle = findViewById(R.id.tv_allcourses_detail_title);
        TextView tvinstructor = findViewById(R.id.tv_allcourses_detail_instructor);
        TextView tvlectures = findViewById(R.id.tv_allcourses_detail_totalLectures);
        TextView tvlevel = findViewById(R.id.tv_allcourses_detail_level);
        TextView tvprice = findViewById(R.id.tv_allcourses_detail_price);
        RatingBar ratingBar = findViewById(R.id.ratingBar_allcourses_detail);
        TextView tvsubject = findViewById(R.id.tv_allcourses_detail_subject);
        tvtitle.setText(title);
        tvinstructor.setText(instructor);
        tvlectures.setText(lectures);
        tvlevel.setText(level);
        tvprice.setText(price);
        tvsubject.setText(subject);
        ratingBar.setRating(Float.parseFloat(rating));
        RecyclerView recyclerView = findViewById(R.id.recycler_allcourses_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter_curriculum adapter = new adapter_curriculum(this, listduration, listlinks, listtitle);
        recyclerView.setAdapter(adapter);

//        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();


    }

    public void btn_allcourse_detail_enroll(View view) {
        String userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        reference.child(userid).child("total_enrolled_courses").get().addOnCompleteListener(task11 -> {
            try {
                int result = Integer.parseInt(Objects.requireNonNull(task11.getResult().getValue()).toString());
                if (result < 2) {
                    reference.child(userid).child("enrolled_courses").child(course_key).get().addOnCompleteListener(task1122 -> {
                        try {
                            String enrollkey = Objects.requireNonNull(task1122.getResult().getValue()).toString();
                            Toast.makeText(allcourses_detail_explaination.this, "This course is already Enrolled", Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException exception) {
                            reference.child(userid).child("total_enrolled_courses").setValue(result + 1);
                            ref2.child(userid).child("enrolled_courses").child(course_key).child("title").setValue(title);
                            ref2.child(userid).child("enrolled_courses").child(course_key).child("subject").setValue(subject);
                            ref2.child(userid).child("enrolled_courses").child(course_key).child("price").setValue(price);
                            ref2.child(userid).child("enrolled_courses").child(course_key).child("rating").setValue(rating);
                            ref2.child(userid).child("enrolled_courses").child(course_key).child("instructor").setValue(instructor);
                            ref2.child(userid).child("enrolled_courses").child(course_key).child("image_url").setValue(url);
                            ref2.child(userid).child("enrolled_courses").child(course_key).child("lectures_progress").setValue("0");
                           ref2.child(userid).child("enrolled_courses").child(course_key).child("lectures").setValue(lectures);

                            reference.child("admin").child("enrolled_status").child(course_key).child("total_enrolled").get().addOnCompleteListener(task -> {
                                try {
                                    int total = Integer.parseInt(Objects.requireNonNull(task.getResult().getValue()).toString());
                                    reference.child("admin").child("enrolled_status").child(course_key).child("title").setValue(title);
                                    reference.child("admin").child("enrolled_status").child(course_key).child("total_enrolled").setValue(String.valueOf(total + 1));
                                    Toast.makeText(allcourses_detail_explaination.this, "Enrolled successful", Toast.LENGTH_SHORT).show();

                                } catch (NullPointerException exce) {
                                    reference.child("admin").child("enrolled_status").child(course_key).child("total_enrolled").setValue("1");
                                    Toast.makeText(allcourses_detail_explaination.this, "Enrolled successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(allcourses_detail_explaination.this, "You can only enroll in two courses at a time", Toast.LENGTH_LONG).show();
                }
            } catch (NullPointerException exception) {
                reference.child(userid).child("total_enrolled_courses").setValue("1");
                ref2.child(userid).child("enrolled_courses").child(course_key).child("title").setValue(title);
                ref2.child(userid).child("enrolled_courses").child(course_key).child("subject").setValue(subject);
                ref2.child(userid).child("enrolled_courses").child(course_key).child("price").setValue(price);
                ref2.child(userid).child("enrolled_courses").child(course_key).child("rating").setValue(rating);
                ref2.child(userid).child("enrolled_courses").child(course_key).child("instructor").setValue(instructor);
                ref2.child(userid).child("enrolled_courses").child(course_key).child("image_url").setValue(url);
                ref2.child(userid).child("enrolled_courses").child(course_key).child("lectures_progress").setValue("0");
                ref2.child(userid).child("enrolled_courses").child(course_key).child("lectures").setValue(lectures);

                reference.child("admin").child("enrolled_status").child(course_key).child("total_enrolled").get().addOnCompleteListener(task -> {
                    try {
                        int result = Integer.parseInt(Objects.requireNonNull(task.getResult().getValue()).toString());
                        reference.child("admin").child("enrolled_status").child(course_key).child("title").setValue(title);
                        reference.child("admin").child("enrolled_status").child(course_key).child("total_enrolled").setValue(String.valueOf(result + 1));
                        Toast.makeText(allcourses_detail_explaination.this, "Enrolled successful", Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException exce) {
                        reference.child("admin").child("enrolled_status").child(course_key).child("total_enrolled").setValue("1");
                        Toast.makeText(allcourses_detail_explaination.this, "Enrolled successful", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}