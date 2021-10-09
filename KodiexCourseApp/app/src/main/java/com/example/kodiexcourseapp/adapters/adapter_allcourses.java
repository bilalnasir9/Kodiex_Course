package com.example.kodiexcourseapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kodiexcourseapp.R;
import com.example.kodiexcourseapp.allcourses_detail_explaination;
import com.example.kodiexcourseapp.allcourses_modelClass;

import java.util.ArrayList;
import java.util.List;

public class adapter_allcourses extends RecyclerView.Adapter<adapter_allcourses.viewholder> {
    Context context;
    List<String> list_courses_keys = new ArrayList<>();
    List<String> list_image_url = new ArrayList<>();
    List<String> list_curriculum_title = new ArrayList<>();
    List<String> list_curriculum_duration = new ArrayList<>();
    List<String> list_curriculum_link = new ArrayList<>();
    List<String> list_instructor = new ArrayList<>();
    List<String> list_lectures = new ArrayList<>();
    List<String> list_level = new ArrayList<>();
    List<String> list_price = new ArrayList<>();
    List<String> list_rating = new ArrayList<>();
    List<String> list_subject = new ArrayList<>();
    List<String> list_title = new ArrayList<>();

    public adapter_allcourses(Context context1, allcourses_modelClass modelClass) {
        this.context = context1;
        this.list_image_url = modelClass.getList_image_url();
        this.list_title = modelClass.getList_title();
        this.list_subject = modelClass.getList_subject();
        this.list_rating = modelClass.getList_rating();
        this.list_price = modelClass.getList_price();
        this.list_level = modelClass.getList_level();
        this.list_lectures = modelClass.getList_lectures();
        this.list_instructor = modelClass.getList_instructor();
        this.list_curriculum_link = modelClass.getList_curriculum_link();
        this.list_curriculum_duration = modelClass.getList_curriculum_duration();
        this.list_curriculum_title = modelClass.getList_curriculum_title();
        list_courses_keys = modelClass.getList_courses_keys();
    }

    @NonNull
    @Override
    public adapter_allcourses.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_allcourses, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_allcourses.viewholder holder, int position) {
        holder.tv_instructor.setText(list_instructor.get(position));
        holder.tv_title.setText(list_title.get(position));
        holder.tv_subject.setText(list_subject.get(position));
        holder.tv_price.setText(list_price.get(position));
        Glide.with(context).load(list_image_url.get(position)).into(holder.imageView);
        holder.ratingBar.setRating(Float.parseFloat(list_rating.get(position)));
        holder.layout.setOnClickListener(view -> {
            Intent intent=new Intent(context, allcourses_detail_explaination.class);
            intent.putExtra("title",list_title.get(position));
            intent.putExtra("instructor",list_instructor.get(position));
            intent.putExtra("subject",list_subject.get(position));
            intent.putExtra("price",list_price.get(position));
            intent.putExtra("rating",list_rating.get(position));
            intent.putExtra("level",list_level.get(position));
            intent.putExtra("lectures",list_lectures.get(position));
             intent.putExtra("course_key",list_courses_keys.get(position));
             intent.putExtra("url",list_image_url.get(position));

            intent.putStringArrayListExtra("durationlist", (ArrayList<String>) list_curriculum_duration);
            intent.putStringArrayListExtra("linklist", (ArrayList<String>) list_curriculum_link);
            intent.putStringArrayListExtra("titlecurriculumlist", (ArrayList<String>) list_curriculum_title);
            context.startActivity(intent);
//                Toast.makeText(context, link, Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, duration, Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public int getItemCount() {
        return list_title.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_instructor, tv_title, tv_subject, tv_price;
        RatingBar ratingBar;
        ConstraintLayout layout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_allcourses);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tv_instructor = itemView.findViewById(R.id.tv_allcourses_instructer);
            tv_title = itemView.findViewById(R.id.tv_allcourses_title);
            tv_subject = itemView.findViewById(R.id.tv_allcourses_subjectname);
            tv_price = itemView.findViewById(R.id.tv_allcourses_status);
            layout = itemView.findViewById(R.id.layout_recycler_allcourses);
        }
    }
}
