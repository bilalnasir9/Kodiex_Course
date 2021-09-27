package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class dashboard_student extends AppCompatActivity {
    ImageView imageView_userprofile;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String name, email, userid, contact;
    ProgressDialog progressDialog;
    Uri profileurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_student);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait");
        imageView_userprofile = findViewById(R.id.imageview_dashboarduserprofile);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        loaddata();
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
}