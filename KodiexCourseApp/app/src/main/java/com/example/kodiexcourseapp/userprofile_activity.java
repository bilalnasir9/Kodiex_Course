package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private static final int CAMERA_REQUEST = 1888;
    private static final int MEDIA_REQUEST = 1520;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Uri filepath;
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
            if (filepath==null){
                reference.child(userid).child("name").setValue(name);
                reference.child(userid).child("email").setValue(email);
                reference.child(userid).child("contact").setValue(contact);
                Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show();
            }
            else {
                reference.child(userid).child("name").setValue(name);
                reference.child(userid).child("email").setValue(email);
                reference.child(userid).child("contact").setValue(contact);
                reference.child(userid).child("profile_url").setValue(filepath.toString());
                Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show();
            }

        }


    }

    public void button_setting_changephoto(View view) {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure to want exit?").create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        dialog.setContentView(R.layout.choose_image_layout);
        AppCompatImageButton btn_gallery=dialog.findViewById(R.id.dialog_gallerybutton);
        AppCompatImageButton btn_camera=dialog.findViewById(R.id.dialog_camerabutton);

        Objects.requireNonNull(btn_gallery).setOnClickListener(view1 -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, MEDIA_REQUEST);
            dialog.dismiss();
        });
        Objects.requireNonNull(btn_camera).setOnClickListener(view1 -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                dialog.dismiss();
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                dialog.dismiss();
            }
            dialog.dismiss();
        });
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "title", null);
            filepath = Uri.parse(path);
            Glide.with(this).load(filepath).into(userprofile);
//            userprofile.setImageBitmap(photo);
        }
        if (requestCode == MEDIA_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            filepath = selectedImage;
            Glide.with(this).load(filepath).into(userprofile);

//            userprofile.setImageURI(selectedImage);

        }
    }
}