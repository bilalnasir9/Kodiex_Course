package com.example.kodiexcourseapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kodiexcourseapp.R;

import org.w3c.dom.Text;

import java.util.List;

public class adapter_curriculum extends RecyclerView.Adapter<adapter_curriculum.viewholder> {
    List<String> title, duration, links;
    Context context;

    public adapter_curriculum(Context cx, List<String> listduration, List<String> listlinks, List<String> listtitle) {
        this.title = listtitle;
        this.duration = listduration;
        this.links = listlinks;
        this.context = cx;
    }

    @NonNull
    @Override
    public adapter_curriculum.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_curriculum_detail, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_curriculum.viewholder holder, int position) {
        holder.tvtitle.setText(title.get(position));
        holder.tvduration.setText(duration.get(position));
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView tvtitle, tvduration;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            tvtitle = itemView.findViewById(R.id.textview_curriculum_title);
            tvduration = itemView.findViewById(R.id.textview_curriculum_duration);
        }
    }
}
