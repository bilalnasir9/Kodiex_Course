package com.example.carmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class vehiclessetting_activity extends AppCompatActivity {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userid, brand, model, number, odo;
    Spinner spiner_current_vehicle, spiner_delete_vehicle;
    List<String> list_currentvehicles = new ArrayList<>();
    List<String> list_deletevehicles = new ArrayList<>();
    List<String> list_allkeys = new ArrayList<>();
    String current_vehicle_value;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiclessetting);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        TextView tvcurrent = findViewById(R.id.tv_currentvehicle);
        spiner_current_vehicle = findViewById(R.id.spiner_currentvehicle);
        spiner_delete_vehicle = findViewById(R.id.spiner_deletevehicle);
        list_currentvehicles.add("Change current vehicle");
        list_deletevehicles.add("Select vehicle to delete");
        list_allkeys.add("All keys values");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userid).child("odometer");
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference(userid).child("current_vehicle");
        reference2.get().addOnCompleteListener(task -> {
            try {
                current_vehicle_value = Objects.requireNonNull(task.getResult().getValue()).toString();
                tvcurrent.setText(tvcurrent.getText() + current_vehicle_value + "\n");
            } catch (Exception exception) {
                tvcurrent.setText(tvcurrent.getText() + "Not found");
            }

        });
        DatabaseReference car_reference = FirebaseDatabase.getInstance().getReference(userid).child("vehicles");
        car_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String keys = Objects.requireNonNull(ds.getKey());
                        String val = Objects.requireNonNull(ds.getValue()).toString();
                        list_currentvehicles.add(val);
                        list_deletevehicles.add(val);
                        list_allkeys.add(keys);
                    } catch (NullPointerException exception) {
                        Toast.makeText(vehiclessetting_activity.this, exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list_currentvehicles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner_current_vehicle.setAdapter(adapter);
        spiner_current_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!list_currentvehicles.get(position).equals("Change current vehicle")) {
                    reference2.setValue(list_currentvehicles.get(position));
                    tvcurrent.setText("\nCurrent vehicle:\n" + list_currentvehicles.get(position) + "\n");
                    Toast.makeText(vehiclessetting_activity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter<String> adapter2;
        adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list_deletevehicles);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner_delete_vehicle.setAdapter(adapter2);
        spiner_delete_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!list_deletevehicles.get(position).equals("Select vehicle to delete")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(vehiclessetting_activity.this)
                            .setTitle("Delete Vehicle").setMessage("All data of selected vehicle will be deleted forever\n are you sure to proceed?");
                    builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        String val = list_allkeys.get(position);
                        car_reference.child(val).removeValue();
                        ref.child(list_deletevehicles.get(position)).removeValue();
                        reference.child(userid).child("expenses").child(list_deletevehicles.get(position)).removeValue();
                        Toast.makeText(vehiclessetting_activity.this, "All record deleted of vehicle no: " + val, Toast.LENGTH_LONG).show();
                        vehiclessetting_activity.this.recreate();
                        if (list_deletevehicles.get(position).equals(current_vehicle_value)) {
                            reference2.removeValue();
                            ref.child(current_vehicle_value).removeValue();
                            reference.child(userid).child("expenses").child(current_vehicle_value).removeValue();
                        }
                    }).setNegativeButton("No", (dialogInterface, i) -> {
                        vehiclessetting_activity.this.recreate();
                    }).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void float_btn_addvehicle(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.custom_dialog).create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        Button addButton = dialog.findViewById(R.id.btn_custom_dialog);
        Button closebutton = dialog.findViewById(R.id.btn_close_customdialog);
        Objects.requireNonNull(closebutton).setOnClickListener(view1 -> {
            dialog.cancel();
        });
        Objects.requireNonNull(addButton).setOnClickListener(view1 -> {
            EditText brandname = dialog.findViewById(R.id.et_custom_dialog_brand_name);
            EditText modelno = dialog.findViewById(R.id.et_custom_dialog_model);
            EditText carno = dialog.findViewById(R.id.et_custom_dialog_vehicle_no);
            EditText odometer = dialog.findViewById(R.id.et_dashboardodometer);
            // handle button click
            brand = Objects.requireNonNull(brandname).getText().toString();
            model = Objects.requireNonNull(modelno).getText().toString();
            number = Objects.requireNonNull(carno).getText().toString();
            odo = Objects.requireNonNull(odometer).getText().toString();
            if (brand.isEmpty()) {
                brandname.setError("Input required");
            } else if (odo.isEmpty()) {
                odometer.setError("Input required");
            } else if (model.isEmpty()) {
                modelno.setError("Input required");
            } else if (number.isEmpty()) {
                carno.setError("Input required");
            } else {
                reference.child(userid).child("vehicles").child(number).get().addOnCompleteListener(task -> {
                    try {
                        String value = Objects.requireNonNull(task.getResult().getValue()).toString();
                        if (!value.equals("")) {
                            Toast.makeText(this, "Vehicle with this number is already added", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException exception) {
                        reference.child(userid).child("vehicles").child(number).setValue(brand + "-" + model + "-" + number)
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Toast.makeText(vehiclessetting_activity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                                        reference.child(userid).child("odometer").child(brand + "-" + model + "-" + number).setValue(odo);
                                        dialog.dismiss();
                                        this.recreate();
                                        reference.child(userid).child("current_vehicle").get().addOnCompleteListener(task1 -> {
                                            try {
                                                String mm = Objects.requireNonNull(task1.getResult().getValue()).toString();
                                            } catch (Exception exception1) {
                                                reference.child(userid).child("current_vehicle").setValue(brand + "-" + model + "-" + number);
                                            }
                                        });
                                    }
                                });
                    }
                });
            }
        });
    }
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(this,dashboard_activity.class));
    }
}