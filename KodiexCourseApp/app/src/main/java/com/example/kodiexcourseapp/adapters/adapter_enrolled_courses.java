package com.example.kodiexcourseapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kodiexcourseapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class adapter_enrolled_courses extends RecyclerView.Adapter<adapter_enrolled_courses.viewholder> {
    List title, subject, instructor, price, rating,url,keys,lecturesprogress,lectures;
    Context context;
DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
FirebaseAuth auth=FirebaseAuth.getInstance();
    public adapter_enrolled_courses(Context cx,List<String> list_title, List<String> list_subject, List<String> list_instructor, List<String> list_price, List<String> list_rating,List<String> list_url,List<String> list_keys,List<String> list_progress,List<String> list_lectures) {
        this.context = cx;
        this.title = list_title;
        this.subject = list_subject;
        this.instructor = list_instructor;
        this.price = list_price;
        this.rating = list_rating;
        this.url=list_url;
        this.keys=list_keys;
        this.lecturesprogress=list_progress;
        this.lectures=list_lectures;
    }

    @NonNull
    @Override
    public adapter_enrolled_courses.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_enrolled_courses, parent, false);
        return new viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull adapter_enrolled_courses.viewholder holder, int position) {
        holder.tvtitle.setText(title.get(position).toString());
        holder.tvsubject.setText(subject.get(position).toString());
        holder.tvprice.setText(price.get(position).toString());
        holder.tvinstructor.setText(instructor.get(position).toString());
        holder.tv_coursenumber.setText("Course ID=\t"+keys.get(position).toString());
        holder.ratingBar.setRating(Float.parseFloat(rating.get(position).toString()));
        Glide.with(context).load(url.get(position)).into(holder.imageView);
        int getprogress= Integer.parseInt(lecturesprogress.get(position).toString());
        holder.progressBar.setProgress(getprogress);
        holder.progressBar.setMax(Integer.parseInt(lectures.get(position).toString()));
        int percentage=(getprogress*100)/48;
        holder.tvpercentage.setText(String.valueOf(percentage) +"%\n"+getprogress+"/"+lectures.get(position).toString());
        holder.button_ratingSubmit.setOnClickListener(view -> {
            String userid= Objects.requireNonNull(auth.getCurrentUser()).getUid();
            String getkey=keys.get(position).toString();
            Float prev_rating= Float.valueOf(rating.get(position).toString());
          Float curr_rating=  holder.ratingBar_userfeedback.getRating();
          float result=(prev_rating+curr_rating)/2;
          holder.ratingBar.setRating(result);
          reference.child("admin").child("courses").child(getkey).child("rating").setValue(Float.toString(result));
            reference.child(userid).child("enrolled_courses").child(getkey).child("rating").setValue(Float.toString(result));
            Toast.makeText(context, "Thank your for your feedback !", Toast.LENGTH_SHORT).show();
        });
        holder.button_complete.setOnClickListener(view -> {
          int lec=  Integer.parseInt(lectures.get(position).toString());
            if (getprogress==lec){
                Toast.makeText(context, "yoy can complete", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Can't complete, you have to watch all lectures first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView tvtitle, tvsubject, tvprice, tvinstructor,tv_coursenumber,tvpercentage;
        RatingBar ratingBar,ratingBar_userfeedback;
        ProgressBar progressBar;
        ImageView imageView;
        MaterialButton button_ratingSubmit;
        AppCompatButton button_complete;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            tvtitle=itemView.findViewById(R.id.tv_enrolledcourses_title);
            tv_coursenumber=itemView.findViewById(R.id.textview_coursenumber_enrolled);
             tvsubject=itemView.findViewById(R.id.tv_enrolledcourses_subjectname);
             tvprice=itemView.findViewById(R.id.tv_enrolledcourses_status);
             tvinstructor=itemView.findViewById(R.id.tv_enrolledcourses_instructer);
            ratingBar=itemView.findViewById(R.id.ratingBar_enrolledcourses);
             ratingBar_userfeedback=itemView.findViewById(R.id.ratingbar_userview);
             progressBar=itemView.findViewById(R.id.simpleProgressBar);
             tvpercentage=itemView.findViewById(R.id.textView_progress1_percentage);
        imageView=itemView.findViewById(R.id.imageView_enrolledcourses);
        button_ratingSubmit=itemView.findViewById(R.id.btn_rating_submit);
        button_complete=itemView.findViewById(R.id.btn_enrolled_courses_complete);
        }
    }
}
