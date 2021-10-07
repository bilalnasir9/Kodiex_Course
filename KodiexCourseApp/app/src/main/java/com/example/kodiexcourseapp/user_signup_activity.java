package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.Objects;

public class user_signup_activity extends AppCompatActivity {
    EditText etname, etemail, etpassword;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String email = "", password = "", name = "";
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        etname = findViewById(R.id.edittext_signup_name);
        etemail = findViewById(R.id.edittext_signup_email);
        etpassword = findViewById(R.id.edittext_signup_password);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
    }

    public void button_signup_clicked(View view) {
        name = etname.getText().toString();
        email = etemail.getText().toString();
        password = etpassword.getText().toString();
        if (TextUtils.isEmpty(name)) {
            etname.setError("Name required");
        } else if (TextUtils.isEmpty(email)) {
            etemail.setError("Email required");
        } else if (TextUtils.isEmpty(password)) {
            etpassword.setError("Password required");
        } else if (password.length() <= 7) {
            etpassword.setError("Password must be at least 8 characters");
        } else {
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            reference.child(Objects.requireNonNull(auth.getUid())).child("name").setValue(name);
                            reference.child(Objects.requireNonNull(auth.getUid())).child("email").setValue(email);
                            reference.child(Objects.requireNonNull(auth.getUid())).child("profile_url").setValue("null");
                            reference.child(auth.getUid()).child("contact").setValue("null");
                            Toast.makeText(user_signup_activity.this, "Signup success.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(user_signup_activity.this, dashboard_student.class));
                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(user_signup_activity.this, "Something went wrong\nTry with other email",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}