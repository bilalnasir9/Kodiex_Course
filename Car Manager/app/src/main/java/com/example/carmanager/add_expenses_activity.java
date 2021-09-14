 package com.example.carmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

 public class add_expenses_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

    }
     public void button_addfuel_onclicke(View view) {
    Intent intent=new Intent(this,save_expenses_activity.class).putExtra("EXP_TYPE","Fuel");
        startActivity(intent);
     }

     public void button_enginetunning_onclike(View view) {
         Intent intent=new Intent(this,save_expenses_activity.class).putExtra("EXP_TYPE","Engine tunning");
         startActivity(intent);
     }

     public void button_service_onclik(View view) {
         Intent intent=new Intent(this,save_expenses_activity.class).putExtra("EXP_TYPE","Service");
         startActivity(intent);
     }

     public void button_maintenance_onclik(View view) {
         Intent intent=new Intent(this,save_expenses_activity.class).putExtra("EXP_TYPE","Maintenance");
         startActivity(intent);
     }

     public void button_spare_parts_clicked(View view) {
         Intent intent=new Intent(this,save_expenses_activity.class).putExtra("EXP_TYPE","Spare parts");
         startActivity(intent);
     }

     public void button_others_clicked(View view) {
         Intent intent=new Intent(this,save_expenses_activity.class).putExtra("EXP_TYPE","others");
         startActivity(intent);

     }
     public void onBackPressed() {
         this.finish();
         startActivity(new Intent(this,dashboard_activity.class));
     }

 }