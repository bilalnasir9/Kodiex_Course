package com.example.kodiexcourseapp.admin_portal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kodiexcourseapp.MainActivity;
import com.example.kodiexcourseapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class admin_portal extends AppCompatActivity {
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("admin");
    FirebaseAuth auth=FirebaseAuth.getInstance();
    String counter,current_datetime;
    String counter_increse;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_portal);
        current_datetime = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").
                format(Calendar.getInstance().getTime());
        reference.child("counter_announce").get().addOnCompleteListener(task -> {
            try {
                counter= Objects.requireNonNull(task.getResult().getValue()).toString();
                int increment=Integer.parseInt(counter);
                counter_increse=String.valueOf(increment+1);
            }
            catch (Exception exception){
                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btn_admin_announcement(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_admin_add_announcement);
        EditText title = (EditText) dialog.findViewById(R.id.edittext_admin_announcement_title);
        EditText detail = (EditText) dialog.findViewById(R.id.edittext_admin_announcement_detail);
        AppCompatButton saveButton = (AppCompatButton) dialog.findViewById(R.id.btn_admin_save_announcement);
        AppCompatButton cancelButton = (AppCompatButton) dialog.findViewById(R.id.btn_admin_cancel_announcement);
        saveButton.setOnClickListener(v -> {
            String gettitle=title.getText().toString();
            String getdetail=detail.getText().toString();
            if (TextUtils.isEmpty(gettitle)){
                title.setError("Input required");
            }else if (TextUtils.isEmpty(getdetail)){
                detail.setError("Input required");
            }
            else {
          reference.child("announcement").child(counter).child("date").setValue(current_datetime);
           reference.child("announcement").child(counter).child("detail").setValue(getdetail);
           reference.child("announcement").child(counter).child("title").setValue(gettitle);
           reference.child("counter_announce").setValue(counter_increse);
                Toast.makeText(this,"Task Successful!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                startActivity(new Intent(this,admin_portal.class));
            }

        });
        cancelButton.setOnClickListener(v -> {
             dialog.dismiss();
        });
        dialog.show();
        Window window = dialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void btn_admin_signout(View view) {
        auth.signOut();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void btn_admin_courses(View view) {
        startActivity(new Intent(this, admin_courses_activity.class));

    }


}