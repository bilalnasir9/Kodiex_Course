package com.example.kodiexcourseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class play_video_lecture_activity extends YouTubeBaseActivity {
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    String geturl, getkey, userid, get_coursekey;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_lecture);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        youTubePlayerView = findViewById(R.id.youtubeplayer_playvideolecture);
        Intent intent = getIntent();
        get_coursekey = intent.getStringExtra("course_key");
        geturl = intent.getStringExtra("curriculum_url");
        getkey = intent.getStringExtra("curriculum_key");
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
                player.loadVideo(geturl);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        youTubePlayerView.initialize(playConfig.API_KEY, onInitializedListener);

    }


    public void btn_playvideolecture_complete(View view) {
        reference.child(userid).child("enrolled_courses").child(get_coursekey).child("lectures_progress").get().addOnCompleteListener(task -> {
            try {
                int getprogress = Integer.parseInt(Objects.requireNonNull(task.getResult().getValue()).toString());
                reference.child(userid).child("completed_lectures").child(get_coursekey).child(getkey).get().addOnCompleteListener(task2 -> {
                    if (task2.getResult().getValue() == null) {
                        reference.child(userid).child("completed_lectures").child(get_coursekey).child(getkey).setValue("completed");
                        reference.child(userid).child("enrolled_courses").child(get_coursekey).child("lectures_progress").setValue(String.valueOf(getprogress + 1));
                        Toast.makeText(play_video_lecture_activity.this, "Completed successful", Toast.LENGTH_SHORT).show();
                   super.onBackPressed();
                    } else {
                        Toast.makeText(play_video_lecture_activity.this, "Already Completed", Toast.LENGTH_SHORT).show();
                        super.onBackPressed();
                    }
                });
            } catch (Exception ignored) {
            }
        });

    }
}