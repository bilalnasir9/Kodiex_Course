package com.example.kodiexcourseapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kodiexcourseapp.R;
import com.example.kodiexcourseapp.play_video_lecture_activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class adapter_videolectures extends RecyclerView.Adapter<adapter_videolectures.viewholder> {
    List<String> keys;
    List<String> title;
    List<String> duration;
    List<String> links;
    List<String> counter;
    List<String> list_completed_lectures;
    String course_key;
    Context context;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public adapter_videolectures(Context cx, List<String> list_curriculum_keys, List<String> list_title, List<String> list_duration, List<String> list_links, List<String> list_counter, String coursekey) {
        context = cx;
        keys = list_curriculum_keys;
        title = list_title;
        duration = list_duration;
        links = list_links;
        counter = list_counter;
        course_key = coursekey;
    }

    @NonNull
    @Override
    public adapter_videolectures.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_curriculum_videolectures, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_videolectures.viewholder holder, @SuppressLint("RecyclerView") int position) {

        String userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        reference.child(userid).child("completed_lectures").child(course_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String key = ds.getKey();
                        if (keys.get(position).equals(key)) {
                            holder.imageView_completcheck.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ignored) {
//                        Toast.makeText(context, "exception found ", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.tv_title.setText(title.get(position).toString());
        holder.tv_duration.setText(duration.get(position).toString());
        holder.tvcounter.setText(counter.get(position).toString() + ".");
        holder.layout.setOnClickListener(view -> {
            Intent intent = new Intent(context, play_video_lecture_activity.class);
            intent.putExtra("course_key", course_key);
            intent.putExtra("curriculum_url", links.get(position).toString());
            intent.putExtra("curriculum_key", keys.get(position).toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_duration, tvcounter;
        ConstraintLayout layout;
        ImageView imageView_completcheck;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.textview_curriculum_videolecture_title);
            tv_duration = itemView.findViewById(R.id.textview_curriculum_videolecture_duration);
            tvcounter = itemView.findViewById(R.id.textView_playvideo_counter);
            layout = itemView.findViewById(R.id.recycler_curriculum_playvideo_layout);
            imageView_completcheck = itemView.findViewById(R.id.imageView_playvideo_completecheck);
        }
    }
}
