package com.example.kodiexcourseapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kodiexcourseapp.R;

import java.util.List;

public class adapter_announcement extends RecyclerView.Adapter<adapter_announcement.viewholder> {
    List<String> days, title, detail;
    Context context;

    public adapter_announcement(Context announcement_activity, List<String> listtile, List<String> listdetail, List<String> listdays) {
        this.context = announcement_activity;
        this.days = listdays;
        this.detail = listdetail;
        this.title = listtile;
    }

    @NonNull
    @Override
    public adapter_announcement.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.announce_layout, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_announcement.viewholder holder, int position) {

            if (days.get(position).equals("Today")) {
                holder.textview_title.setTextColor(Color.BLACK);
                holder.textview_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                holder.imageView.setVisibility(View.VISIBLE);

            }
                holder.textview_days.setText(days.get(position));
                holder.textview_detail.setText(detail.get(position));
                holder.textview_title.setText(title.get(position));
                holder.layout.setOnClickListener(view -> {
                    if (holder.textview_detail.getMaxLines()==20){
                        holder.textview_detail.setMaxLines(1);
                    }
                    else {
                        holder.textview_detail.setMaxLines(20);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView textview_title, textview_days, textview_detail;
               ImageView imageView;
ConstraintLayout layout;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            textview_title = itemView.findViewById(R.id.textview_announce_title);
            imageView = itemView.findViewById(R.id.imageView_new);
            textview_days = itemView.findViewById(R.id.textview_announce_days);
            textview_detail = itemView.findViewById(R.id.textview_announce_detail);
            layout=itemView.findViewById(R.id.announce_layout);
        }
    }
}
