package com.example.carmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class  dashboard_activity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String firstName, userid, currentvehicle;
    Toolbar toolbar;
    ProgressDialog pd;
    TextView tvcurrentvehicle, tvfuelaverage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        pd = new ProgressDialog(this);
        pd.setMessage("Loading..");
        pd.show();

        Uri uri = Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl();
        String name = auth.getCurrentUser().getDisplayName();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView userprofile = findViewById(R.id.imagevw_userprofile);
        TextView username = findViewById(R.id.tvusername);
        TextView tvodometer = findViewById(R.id.tv_dashboard_odometer);
        tvfuelaverage = findViewById(R.id.tv_dashboard_fuelAverage);
        tvcurrentvehicle = findViewById(R.id.tvdashborad_currentvehicle);
        reference.child(userid).child("current_vehicle").get().addOnCompleteListener(task -> {
            try {
                String val = Objects.requireNonNull(task.getResult().getValue()).toString();
                tvcurrentvehicle.setText(val);
                currentvehicle = val;
                reference.child(userid).child(currentvehicle).child("fuel_average").get().addOnCompleteListener(task2 -> {
                    try {
                        String average = Objects.requireNonNull(task2.getResult().getValue()).toString();
                        tvfuelaverage.setText(average + " KM/L");
                    } catch (Exception exception) {
                        tvfuelaverage.setText("0.00KM/L");
                    }
                });
                reference.child(userid).child("odometer").child(val).get().addOnCompleteListener(task1 -> {
                    try {
                        String vv = Objects.requireNonNull(task1.getResult().getValue()).toString();
                        tvodometer.setText(vv + " KM");
                    } catch (Exception e) {
                        tvodometer.setText("0.00 KM");
                    }

                });
            } catch (Exception e) {
                tvcurrentvehicle.setText("Vehicle not found");
                tvodometer.setText("0.00 KM");
            }
        });
        Glide.with(this).load(uri).into(userprofile);

        if (name.split("\\w+").length > 1) {
            firstName = name.substring(0, name.lastIndexOf(' '));
        } else {
            firstName = name;
        }
        username.setText("Hi, " + firstName);
        reference.child(userid).child("vehicles").get().addOnCompleteListener(task -> {
            try {
                Objects.requireNonNull(task.getResult().getValue()).toString();
                pd.dismiss();
            } catch (NullPointerException exception) {
                error();
            } catch (Exception exception) {
                Toast.makeText(this, "Something went wrong\ncheck your internet connection", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

        reference.child(userid).child("fuel_price").get().addOnCompleteListener(task -> {
            try {
                Objects.requireNonNull(task.getResult().getValue()).toString();
            } catch (Exception exception) {
                updatefuelprice();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void error() {
        pd.dismiss();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.custom_dialog).create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        Button diagButton = dialog.findViewById(R.id.btn_custom_dialog);
        Button closebutton = dialog.findViewById(R.id.btn_close_customdialog);
        Objects.requireNonNull(closebutton).setOnClickListener(view1 -> dialog.cancel());
        Objects.requireNonNull(diagButton).setOnClickListener(view1 -> {
            // handle button click
            EditText brandname = dialog.findViewById(R.id.et_custom_dialog_brand_name);
            EditText modelno = dialog.findViewById(R.id.et_custom_dialog_model);
            EditText carno = dialog.findViewById(R.id.et_custom_dialog_vehicle_no);
            EditText odometer = dialog.findViewById(R.id.et_dashboardodometer);
            String brand = Objects.requireNonNull(brandname).getText().toString();
            String model = Objects.requireNonNull(modelno).getText().toString();
            String number = Objects.requireNonNull(carno).getText().toString();
            String odo = Objects.requireNonNull(odometer).getText().toString();
            if (brand.isEmpty()) {
                brandname.setError("Input required");
            }
            if (odo.isEmpty()) {
                odometer.setError("Input required");
            } else if (model.isEmpty()) {
                modelno.setError("Input required");
            } else if (number.isEmpty()) {
                carno.setError("Input required");
            } else {
                String message = brand + "-" + model + "-" + number;
                reference.child(userid).child("vehicles").child(number).setValue(message)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show();
                                tvcurrentvehicle.setText(message);
                                reference.child(userid).child("current_vehicle").setValue(message);
                                reference.child(userid).child("odometer").child(message).setValue(odo);
                                dialog.dismiss();
                                this.recreate();
                            }
                        });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_signout:
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.menu_updatefuelprice:
                updatefuelprice();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updatefuelprice() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.fuelprice).create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        MaterialButton btnupdatefuel = dialog.findViewById(R.id.btn_updatefuelprice);
        Objects.requireNonNull(btnupdatefuel).setOnClickListener(view -> {
            EditText etprice = dialog.findViewById(R.id.etfuelprice);
            if (TextUtils.isEmpty(Objects.requireNonNull(etprice).getText().toString())) {
                etprice.setError("Input required");
            } else {
                String price = etprice.getText().toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userid);
                ref.child("fuel_price").setValue(price);
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Exit")
                .setMessage("Are you sure to want exit?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            finishAffinity();
        }).setNegativeButton("No", (dialogInterface, i) -> {
        });
        builder.show();
    }

    public void button_statistics_clicked(View view) {
        this.finish();
        startActivity(new Intent(this, statistics_activity.class));
    }
    public void button_addexpenses_clicked(View view) {
        this.finish();
        startActivity(new Intent(this, add_expenses_activity.class));

    }

    public void button_viewexpenses_clicked(View view) {
        this.finish();
        startActivity(new Intent(this, view_expenses_activity.class));
    }

    public void button_searchexpenses_clicked(View view) {
        this.finish();
        startActivity(new Intent(this, search_expense_activity.class));
    }

    public void button_addreminder_clicked(View view) {
        this.finish();
        startActivity(new Intent(this, addreminder_activity.class));
    }

    public void button_vehiclessetting_clicked(View view) {
        this.finish();
        startActivity(new Intent(this, vehiclessetting_activity.class));
    }


}