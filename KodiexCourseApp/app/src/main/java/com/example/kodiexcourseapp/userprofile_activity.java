package com.example.kodiexcourseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class userprofile_activity extends AppCompatActivity {
    String getphoto_url, getname, getemail, getcontact, userid;
    ImageView userprofile;
    MaterialButton button_change_profile;
    EditText editText_name, editText_email, editText_contact;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        Intent intent = getIntent();
        getphoto_url = intent.getStringExtra("photo_url");
        getname = intent.getStringExtra("name");
        getemail = intent.getStringExtra("email");
        getcontact = intent.getStringExtra("contact");
        userprofile = findViewById(R.id.imageview_profile_setting);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        if (getphoto_url.equals("null")) {
        } else {
            Glide.with(this).load(getphoto_url).into(userprofile);
        }
        button_change_profile = findViewById(R.id.button_profile_setting_changeProfile);
        editText_name = findViewById(R.id.edittext_setting_name);
        editText_email = findViewById(R.id.edittext_setting_email);
        editText_contact = findViewById(R.id.edittext_setting_contact);
        if (getname.equals("null")) {
            editText_name.setText("Not found");
        } else {
            editText_name.setText(getname);
        }
        if (getemail.equals("null")) {
            editText_email.setText("Not found");
        } else {
            editText_email.setText(getemail);
        }
        if (getcontact.equals("null")) {
            editText_contact.setText("Not found");
        } else {
            editText_contact.setText(getcontact);
        }

    }

    public void button_profile_setting_update(View view) {
        String name, email, contact, url;
        name = editText_name.getText().toString();
        email = editText_email.getText().toString();
        contact = editText_contact.getText().toString();
        if (TextUtils.isEmpty(name)) {
            editText_name.setError("Input required");
        } else if (TextUtils.isEmpty(email)) {
            editText_email.setError("Input required");
        } else if (TextUtils.isEmpty(contact)) {
            editText_contact.setError("Input required");
        } else {
            reference.child(userid).child("name").setValue(name);
            reference.child(userid).child("email").setValue(email);
            reference.child(userid).child("contact").setValue(contact);
            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show();
        }


    }

    public void button_setting_changephoto(View view) {
    }
}