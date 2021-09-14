package com.example.carmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.internal.DiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static androidx.core.graphics.TypefaceCompatUtil.getTempFile;

public class save_expenses_activity extends AppCompatActivity {
    DatabaseReference referene = FirebaseDatabase.getInstance().getReference();
    DatabaseReference statereferece;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    String userid, current_vehicle, fuelprice, lastfillup, prev_odometer, date_time, expense_type, imgeurl = "abcdef";
    int getstatecost=0;
    EditText etodometer, ettotalcost;
    TextView tv_previousodometer, tvdate;
    final Calendar myCalendar = Calendar.getInstance();
    ImageView imageView_receipt;
    expense_class objexpense;
    Uri filepath;
    ProgressDialog pd;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MEDIA_REQUEST = 1520;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    int curr, prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_expenses);
        Intent intent = getIntent();
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait..");
        expense_type = intent.getStringExtra("EXP_TYPE");
        tvdate = findViewById(R.id.tv_expense_date);
        etodometer = findViewById(R.id.et_odo_meter);
        ettotalcost = findViewById(R.id.et_total_cost);
        imageView_receipt = findViewById(R.id.imgeview_receipt);
        tv_previousodometer = findViewById(R.id.tv_previoudoddmeter);
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        statereferece = FirebaseDatabase.getInstance().getReference(userid).child("statistics");
        statereferece.child(expense_type).get().addOnCompleteListener(task -> {
            try {
                String res= Objects.requireNonNull(task.getResult().getValue()).toString();
                getstatecost=Integer.parseInt(res);
            }catch (Exception ignored){
            }
        });
        referene.child(userid).child("fuel_price").get().addOnCompleteListener(task -> {
            try {
                fuelprice = Objects.requireNonNull(task.getResult().getValue()).toString();
            } catch (Exception exception) {
                Toast.makeText(this, "Something went wrong\nFuel average cannot be calculated", Toast.LENGTH_SHORT).show();
            }
        });
        referene.child(userid).child("current_vehicle").get().addOnCompleteListener(task -> {
            try {
                current_vehicle = Objects.requireNonNull(task.getResult().getValue()).toString();
                referene.child(userid).child("odometer").child(current_vehicle).get().addOnCompleteListener(task2 -> {
                    try {
                        prev_odometer = Objects.requireNonNull(task2.getResult().getValue()).toString();
                        tv_previousodometer.setText("Previous value=" + prev_odometer + " km");
                        referene.child(userid).child(current_vehicle).child("lastfillup").get().addOnCompleteListener(task3 -> {
                            try {
                                lastfillup = Objects.requireNonNull(task3.getResult().getValue()).toString();
                            } catch (Exception exception) {
                                Toast.makeText(this, "Something went wrong\nFuel average cannot be calculated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception exception) {
                        Toast.makeText(this, "Expense can't be added first you need to add current vehicle", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception exception) {
                Toast.makeText(this, "Expense can't be added first you need to add current vehicle", Toast.LENGTH_LONG).show();
            }
        });
        if (!expense_type.equals("Fuel")) {
            etodometer.setVisibility(View.INVISIBLE);
            tv_previousodometer.setVisibility(View.INVISIBLE);
        } else {
            etodometer.setVisibility(View.VISIBLE);
            tv_previousodometer.setVisibility(View.VISIBLE);
        }

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        tvdate.setOnClickListener(view -> {
            new DatePickerDialog(save_expenses_activity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String myFormat2 = "dd-MM-yyyy hh:mm:ss"; //In which you need put here
        SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
        tvdate.setText(sdf.format(myCalendar.getTime()));
        date_time = sdf2.format(myCalendar.getTime());
    }

    public void button_addreceipt_onclike(View view) {
        Dialog dialog = new AlertDialog.Builder(this).setView(R.layout.choose_image_layout).create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        MaterialButton btnmedia = dialog.findViewById(R.id.dialog_mediabutton);
        MaterialButton btncamera = dialog.findViewById(R.id.dialog_camerabutton);
        btnmedia.setOnClickListener(view1 -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, MEDIA_REQUEST);
            dialog.dismiss();
        });
        btncamera.setOnClickListener(view1 -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                dialog.dismiss();
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                dialog.dismiss();
            }

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
            imageView_receipt.setImageBitmap(photo);
        }
        if (requestCode == MEDIA_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            filepath = selectedImage;
            imageView_receipt.setImageURI(selectedImage);

        }
    }

    public void button_save_allexpense_onclike(View view) {
        String currentodo = etodometer.getText().toString();
        String date = tvdate.getText().toString();
        String cost = ettotalcost.getText().toString();
        if (!expense_type.equals("Fuel")) {
            if (TextUtils.isEmpty(date)) {
                tvdate.setError("Input required");
            } else if (TextUtils.isEmpty(cost)) {
                ettotalcost.setError("Input required");
            } else {
                if (filepath != null && prev_odometer != null && current_vehicle != null) {
                    pd.show();
                    StorageReference childRef = storageReference.child(date_time + ".jpg");
                    childRef.putFile(filepath)
                            .addOnSuccessListener(taskSnapshot -> {
                                // Download file From Firebase Storage
                                childRef.getDownloadUrl().addOnSuccessListener(downloadPhotoUrl -> {
                                    imgeurl = downloadPhotoUrl.toString();
                                    objexpense = new expense_class(expense_type, cost, imgeurl, prev_odometer);
                                    referene.child(userid).child("expenses").child(current_vehicle).child(date).child(date_time).setValue(objexpense);
                                 int getcost=Integer.parseInt(cost);
                                  statereferece.child(expense_type).setValue(getstatecost+getcost);
                                    Toast.makeText(save_expenses_activity.this, "Successfully added", Toast.LENGTH_SHORT).show();

                                    pd.dismiss();
                                    ettotalcost.setText(null);
                                    this.recreate();
                                });
                            });

                }
                if (filepath == null && prev_odometer != null && current_vehicle != null) {
                    pd.show();
                    objexpense = new expense_class(expense_type, cost, imgeurl, prev_odometer);
                    referene.child(userid).child("expenses").child(current_vehicle).child(date).child(date_time).setValue(objexpense);
                    int getcost=Integer.parseInt(cost);
                    statereferece.child(expense_type).setValue(getstatecost+getcost);
                    Toast.makeText(save_expenses_activity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    ettotalcost.setText(null);
                    this.recreate();
                }

            }
        } else {
            try {
                curr = Integer.parseInt(currentodo);
                prev = Integer.parseInt(prev_odometer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (TextUtils.isEmpty(currentodo)) {
                etodometer.setError("Input required");
            } else if (curr < prev) {
                etodometer.setError("Current value cannot be less than previous value");
            } else if (TextUtils.isEmpty(date)) {
                tvdate.setError("Input required");
            } else if (TextUtils.isEmpty(cost)) {
                ettotalcost.setError("Input required");
            } else {
                if (filepath != null && prev_odometer != null && current_vehicle != null) {
                    pd.show();
                    try {
                        double millage = curr - prev;
                        double totalcost = Double.parseDouble(lastfillup);
                        double priceperliter = Double.parseDouble(fuelprice);
                        double totalliter = totalcost / priceperliter;
                        double average = millage / totalliter;
                        String result = String.valueOf(average);
                        referene.child(userid).child(current_vehicle).child("fuel_average").setValue(result);
                    } catch (Exception e) {
                        Toast.makeText(this, "Fuel average cannot be calculated", Toast.LENGTH_SHORT).show();
                    }
                    StorageReference childRef = storageReference.child(date_time + ".jpg");
                    childRef.putFile(filepath)
                            .addOnSuccessListener(taskSnapshot -> {
                                // Download file From Firebase Storage
                                childRef.getDownloadUrl().addOnSuccessListener(downloadPhotoUrl -> {
                                    imgeurl = downloadPhotoUrl.toString();
                                    objexpense = new expense_class(expense_type, cost, imgeurl, currentodo);
                                    referene.child(userid).child("expenses").child(current_vehicle).child(date).child(date_time).setValue(objexpense);
                                    referene.child(userid).child("odometer").child(current_vehicle).setValue(currentodo);
                                    referene.child(userid).child(current_vehicle).child("lastfillup").setValue(cost);
                                    int getcost=Integer.parseInt(cost);
                                    statereferece.child(expense_type).setValue(getstatecost+getcost);
                                    Toast.makeText(save_expenses_activity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    ettotalcost.setText(null);
                                    etodometer.setText(null);
                                    this.recreate();
                                });
                            });
                }
                if (filepath == null && prev_odometer != null && current_vehicle != null) {
                    pd.show();
                    try {
                        double millage = curr - prev;
                        double totalcost = Double.parseDouble(lastfillup);
                        double priceperliter = Double.parseDouble(fuelprice);
                        double totalliter = totalcost / priceperliter;
                        double average = millage / totalliter;
                        DecimalFormat formater = new DecimalFormat("0.00");
                        String result = formater.format(average);
                        referene.child(userid).child(current_vehicle).child("fuel_average").setValue(result);
                    } catch (Exception e) {
                        Toast.makeText(this, "Fuel average cannot be calculated", Toast.LENGTH_SHORT).show();
                    }
                    objexpense = new expense_class(expense_type, cost, imgeurl, currentodo);
                    referene.child(userid).child("expenses").child(current_vehicle).child(date).child(date_time).setValue(objexpense);
                    referene.child(userid).child("odometer").child(current_vehicle).setValue(currentodo);
                    referene.child(userid).child(current_vehicle).child("lastfillup").setValue(cost);
                    int getcost=Integer.parseInt(cost);
                    statereferece.child(expense_type).setValue(getstatecost+getcost);
                    Toast.makeText(save_expenses_activity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    ettotalcost.setText(null);
                    etodometer.setText(null);
                    this.recreate();
                }
            }


        }
    }


}
