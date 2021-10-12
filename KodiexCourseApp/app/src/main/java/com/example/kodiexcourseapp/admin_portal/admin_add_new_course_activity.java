package com.example.kodiexcourseapp.admin_portal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kodiexcourseapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class admin_add_new_course_activity extends AppCompatActivity {
    String course_key;
    EditText et_title, et_subject, et_rating, et_price, et_level, et_lectures, et_instructor,
            et_thumbnail_url, et_curr_title, et_curr_duration, et_curr_videourl;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_course);
        Intent intent = getIntent();
        course_key = intent.getStringExtra("course_key");

        et_title = findViewById(R.id.edittext_admin_anc_title);
        et_subject = findViewById(R.id.edittext_admin_anc_subject);
        et_rating = findViewById(R.id.edittext_admin_anc_rating);
        et_price = findViewById(R.id.edittext_admin_anc_price);
        et_level = findViewById(R.id.edittext_admin_anc_level);
        et_lectures = findViewById(R.id.edittext_admin_anc_lectures);
        et_instructor = findViewById(R.id.edittext_admin_anc_instructor);
        et_thumbnail_url = findViewById(R.id.edittext_admin_anc_imageURL);
        et_curr_videourl = findViewById(R.id.edittext_admin_anc_curriculum_link);
        et_curr_title = findViewById(R.id.edittext_admin_anc_curriculum_title);
        et_curr_duration = findViewById(R.id.edittext_admin_anc_curriculum_duration);

    }

    public void button_admin_anc_save(View view) {
        String curr_url = et_curr_videourl.getText().toString();
        String title = et_title.getText().toString();
        String subject = et_subject.getText().toString();
        String rating = et_rating.getText().toString();
        String price = et_price.getText().toString();
        String level = et_level.getText().toString();
        String lectures = et_lectures.getText().toString();
        String instructor = et_instructor.getText().toString();
        String thumbnail = et_thumbnail_url.getText().toString();
        String curr_title = et_curr_title.getText().toString();
        String curr_duration = et_curr_duration.getText().toString();
        if (TextUtils.isEmpty(title)) {
            et_title.setError("Input required");
        } else if (TextUtils.isEmpty(subject)) {
            et_subject.setError("Input required");
        } else if (TextUtils.isEmpty(rating)) {
            et_rating.setError("Input required");
        } else if (TextUtils.isEmpty(price)) {
            et_price.setError("Input required");
        } else if (TextUtils.isEmpty(level)) {
            et_level.setError("Input required");
        } else if (TextUtils.isEmpty(lectures)) {
            et_lectures.setError("Input required");
        } else if (TextUtils.isEmpty(instructor)) {
            et_instructor.setError("Input required");
        } else if (TextUtils.isEmpty(thumbnail)) {
            et_thumbnail_url.setError("Input required");
        } else if (TextUtils.isEmpty(curr_title)) {
            et_curr_title.setError("Input required");
        } else if (TextUtils.isEmpty(curr_url)) {
            et_curr_videourl.setError("Input required");
        } else if (TextUtils.isEmpty(curr_duration)) {
            et_curr_duration.setError("Input required");
        } else {
            Pattern pattern = Pattern.compile(
                    "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(curr_url);
            if (matcher.matches()) {
                String vId = matcher.group(1);
                reference.child("courses").child(course_key).child("title").setValue(title);
                reference.child("courses").child(course_key).child("subject").setValue(subject);
                reference.child("courses").child(course_key).child("rating").setValue(rating);
                reference.child("courses").child(course_key).child("price").setValue(price);
                reference.child("courses").child(course_key).child("level").setValue(level);
                reference.child("courses").child(course_key).child("lectures").setValue(lectures);
                reference.child("courses").child(course_key).child("instructor").setValue(instructor);
                reference.child("courses").child(course_key).child("image_url").setValue(thumbnail);
                reference.child("courses").child(course_key).child("Curriculum").child("1").child("title").setValue(curr_title);
                reference.child("courses").child(course_key).child("Curriculum").child("1").child("link").setValue(vId);
                reference.child("courses").child(course_key).child("Curriculum").child("1").child("duration").setValue(curr_duration);
                reference.child("counter_curriculum").child("course_" + course_key).setValue("1");
               reference.child("counter_allcourses").setValue(course_key);
                Toast.makeText(this, "Course successfully added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, admin_portal.class));
            } else {
                et_curr_videourl.setError("Invalid URL");
            }
        }
    }

    public void button_admin_anc_cancel(View view) {
        startActivity(new Intent(this, admin_courses_activity.class));

    }
}