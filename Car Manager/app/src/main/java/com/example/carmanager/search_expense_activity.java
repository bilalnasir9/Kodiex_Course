package com.example.carmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class search_expense_activity extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    TextView tvdate,tverror;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String getdate, userid, currentvehicle;
    List<String> datetimelist = new ArrayList<String>();
    List<String> costlist = new ArrayList<String>();
    List<String> odolist = new ArrayList<>();
    List<String> typelist = new ArrayList<String>();
    List<String> urllist = new ArrayList<>();
    ProgressDialog progressDialog;
RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_expense);
        tvdate = findViewById(R.id.tvdate_searchexpense);
        tverror = findViewById(R.id.tv_searchexpense_error);
        recyclerView=findViewById(R.id.recycler_searchexpense);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        reference.child(userid).child("current_vehicle").get().addOnCompleteListener(task -> {
            try {
                currentvehicle = Objects.requireNonNull(task.getResult().getValue()).toString();
            } catch (Exception exception) {
                error();
            }
        });

    }

    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(this, dashboard_activity.class));
    }

    public void error() {
        Toast.makeText(this, "Error found", Toast.LENGTH_SHORT).show();
    }

    public void button_searchexpense(View view) {
        progressDialog.show();
        tverror.setVisibility(View.VISIBLE);
        datetimelist.clear();
        costlist.clear();
        odolist.clear();
        typelist.clear();
        urllist.clear();

        reference.child(userid).child("expenses").child(currentvehicle).child(getdate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dttime = ds.getKey();
                    String cost = Objects.requireNonNull(ds.child("cost").getValue()).toString();
                    String odo = Objects.requireNonNull(ds.child("odometer").getValue()).toString();
                    String type = Objects.requireNonNull(ds.child("type").getValue()).toString();
                    String url = Objects.requireNonNull(ds.child("url").getValue()).toString();
                    datetimelist.add(dttime);
                    costlist.add(cost);
                    odolist.add(odo);
                    typelist.add(type);
                    urllist.add(url);
                    tverror.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter_searchexpense adapter=new adapter_searchexpense(search_expense_activity.this,getdate,datetimelist,costlist,odolist,typelist,urllist);
           recyclerView.setAdapter(adapter);
           recyclerView.setLayoutManager(new LinearLayoutManager(search_expense_activity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        progressDialog.dismiss();
    }

    public void choosedate_searchexpense(View view) {
        DatePickerDialog.OnDateSetListener date = (vw, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        new DatePickerDialog(search_expense_activity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvdate.setText(sdf.format(myCalendar.getTime()));
        getdate = tvdate.getText().toString();
    }
}