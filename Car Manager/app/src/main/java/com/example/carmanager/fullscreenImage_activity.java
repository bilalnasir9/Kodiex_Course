package com.example.carmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class fullscreenImage_activity extends AppCompatActivity {
    PhotoView fullscreen;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userid, currentvehicle, onlydate, datetime, cost, odo, type, getactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        fullscreen = findViewById(R.id.fullscreen_imageview);
        Intent intent = getIntent();
        String imgeurl = intent.getStringExtra("fullscreenimageurl");
        onlydate = intent.getStringExtra("getfirstdate");
        datetime = intent.getStringExtra("getdate");
        cost = intent.getStringExtra("getcost");
        odo = intent.getStringExtra("getodo");
        type = intent.getStringExtra("gettype");
        getactivity = intent.getStringExtra("getactivity");
        if (imgeurl.equals("abcdef")) {
            fullscreen.setImageResource(R.drawable.noreciept);
        } else {
            Glide.with(this).load(imgeurl).into(fullscreen);
        }


    }

    public void fullscreenmenu_clicked(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Delete Data")
                .setMessage("Are you sure to delete this record?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    reference.child(userid).child("current_vehicle").get().addOnCompleteListener(task -> {
                        try {
                            currentvehicle = Objects.requireNonNull(task.getResult().getValue()).toString();
                            reference.child(userid).child("expenses").child(currentvehicle).child(onlydate).child(datetime).removeValue().addOnSuccessListener(runnable -> {
                                Toast.makeText(this, "Value removed successfully", Toast.LENGTH_SHORT).show();
                                this.finish();
                                if (getactivity.equals("search")) {
                                    startActivity(new Intent(this, search_expense_activity.class));
                                }
                                if (getactivity.equals("view")) {
                                    startActivity(new Intent(this, view_expenses_activity.class));
                                }
                            });
                        } catch (Exception exception) {
                            Toast.makeText(this, "Something went wrong\n" + exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }).setNegativeButton("No", (dialogInterface, i) -> {
                });
        builder.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fullscreen_menu, menu);
        return true;
    }

}


