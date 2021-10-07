package com.example.kodiexcourseapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.VideoView;

import java.io.IOException;

public class allcourses_detail_explaination extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allcourses_detail_explaination);
//        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://youtu.be/up1Vvf9VNdk")));


//        VideoView videoview = (VideoView) findViewById(R.id.videoView);
//        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.test);
//        videoview.setVideoURI(uri);
//        videoview.start();
//        String videourl = "https://youtu.be/up1Vvf9VNdk.mp4";
//        Uri uri2 = Uri.parse(videourl);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri2);
//        intent.setDataAndType(uri2, "video/mp4");
//        startActivity(intent);
    }
}