package com.example.carmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class view_expenses_activity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> datetimelist = new ArrayList<String>();
    List<String> onlydatelist = new ArrayList<String>();
    List<String> typelist = new ArrayList<>();
    List<String> odometerlist = new ArrayList<>();
    List<String> costlist = new ArrayList<>();
    List<String> urllist = new ArrayList<String>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;
    String userid, currentvehicle;
    TextView tverror;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);
        recyclerView = findViewById(R.id.recycler_viewexpenses);
        tverror = findViewById(R.id.tv_viewexpense_error);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        progressDialog.show();
        reference.child(userid).child("current_vehicle").get().addOnCompleteListener(task -> {
            try {

                currentvehicle = Objects.requireNonNull(task.getResult().getValue()).toString();
                reference.child(userid).child("expenses").child(currentvehicle).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                String key = ds.getKey();
                                reference.child(userid).child("expenses").child(currentvehicle).child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            try {
                                                String datetime = dataSnapshot.getKey();
                                                String cost = Objects.requireNonNull(dataSnapshot.child("cost").getValue()).toString();
                                                String odometer = Objects.requireNonNull(dataSnapshot.child("odometer").getValue()).toString();
                                                String type = Objects.requireNonNull(dataSnapshot.child("type").getValue()).toString();
                                                String url = Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString();
                                                onlydatelist.add(key);
                                                datetimelist.add(datetime);
                                                costlist.add(cost);
                                                odometerlist.add(odometer);
                                                typelist.add(type);
                                                urllist.add(url);
                                            } catch (Exception exception) {
                                                error(exception.toString());
                                            }
                                        }
                                        tverror.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adapter_viewexpense adapter = new adapter_viewexpense(view_expenses_activity.this, onlydatelist, datetimelist, costlist, odometerlist, typelist, urllist);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(view_expenses_activity.this));

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } catch (Exception exception) {
                                error(exception.toString());
                            }
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } catch (Exception exception) {
                error(exception.toString());
            }
        });

    }

    private void error(String message) {
        Toast.makeText(this, "Error occurred data cannot be found\n" + message, Toast.LENGTH_SHORT).show();
    progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(this,dashboard_activity.class));
    }
}