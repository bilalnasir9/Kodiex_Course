package com.example.carmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class statistics_activity extends AppCompatActivity {
    List<String> listnames = new ArrayList<>();
    List<Integer> listcost = new ArrayList<>();
    ArrayList<PieEntry> values = new ArrayList<>();
    PieChart pieChart;
    String userid;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        pieChart = findViewById(R.id.piechart);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        reference.child(userid).child("statistics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        listnames.add(ds.getKey());
                        String cost= Objects.requireNonNull(ds.getValue()).toString();
                        listcost.add(Integer.parseInt(cost));
                    } catch (Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(statistics_activity.this, "Data not found", Toast.LENGTH_SHORT).show();
                    }
                }
                setupPieChart();
                loadPieChartData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < listnames.size(); i++) {
            entries.add(new PieEntry(listcost.get(i), listnames.get(i)));
        }
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.JOYFUL_COLORS) {
            colors.add(color);
        }
        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(13);
        data.setValueTextColor(Color.BLUE);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        progressDialog.dismiss();
    }
    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextSize(10);
        pieChart.getDescription().setEnabled(false);
        Legend l = pieChart.getLegend();
        pieChart.setHoleRadius(15);
        pieChart.setTransparentCircleColor(Color.BLACK);
        pieChart.setTransparentCircleRadius(17);
    l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}