package com.example.kodiexcourseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText etemail, etpassword;
    String email, password;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 160;
    private CallbackManager callbackManager;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        etemail = findViewById(R.id.edittext_login_email);
        etpassword = findViewById(R.id.edittext_login_password);
        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
//        button_facebook_signup = findViewById(R.id.facebook_signup_button);
        progressDialog.setMessage("Please wait");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void textview_signup_clicked(View view) {
        startActivity(new Intent(this, user_signup_activity.class));
    }

    public void button_login_clicked(View view) {
        email = etemail.getText().toString();
        password = etpassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etemail.setError("Email required");
        } else if (TextUtils.isEmpty(password)) {
            etpassword.setError("Password required");
        } else if (password.length() <= 7) {
            etpassword.setError("Password must be at least 8 characters");
        } else {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Login success.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, dashboard_student.class));
                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Email or password is incorrect\nif you are a new user please create account",
                                    Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    public void button_Google_signin_clicked(View view) {
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void button_Facebook_signin_clicked(View view) {
//        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        progressDialog.show();
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "signIn failed\n" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(MainActivity.this, "SignIn successful\t", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        updateUI(user);
                        progressDialog.dismiss();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "SignIn failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                        progressDialog.dismiss();
                    }
                });

    }

    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task3 -> {
                    if (task3.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(MainActivity.this, "SignIn successful" , Toast.LENGTH_SHORT).show();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        updateUI(currentUser);
                        progressDialog.dismiss();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "SignIn failed facebook", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user!=null) {
            String email=""+ user.getEmail();
            String contact = "" + user.getPhoneNumber();
            String name=""+ user.getDisplayName();
            String url = "" + Objects.requireNonNull(user.getPhotoUrl()).toString();
            try {
                reference.child(user.getUid()).child("email").setValue(email);
                reference.child(user.getUid()).child("name").setValue(name);
                 reference.child(user.getUid()).child("profile_url").setValue(url);
                reference.child(user.getUid()).child("contact").setValue(contact);
                startActivity(new Intent(MainActivity.this, dashboard_student.class));
            } catch (Exception exception) {
                Toast.makeText(MainActivity.this, "Something went wrong\n"+exception.toString(), Toast.LENGTH_SHORT).show();

            }
        }
        else {
            Toast.makeText(MainActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void button_passwordreset(View view) {
        if (TextUtils.isEmpty(etemail.getText().toString())) {
            etemail.setError("Email required");
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = etemail.getText().toString();

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "A link to reset password is successfully sent to your email\n"+emailAddress, Toast.LENGTH_LONG).show();                            }
                    });
        }
    }

    public void onStart() {
        super.onStart();
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(MainActivity.this, dashboard_student.class));
            }
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}