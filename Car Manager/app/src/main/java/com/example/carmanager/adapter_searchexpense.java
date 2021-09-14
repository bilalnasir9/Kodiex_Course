package com.example.carmanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class adapter_searchexpense extends RecyclerView.Adapter<adapter_searchexpense.ViewHolder> {
    Context context;
    List datetime;
    List cost;
    List odometer;
    List type;
    List url;
    String geturl, userid, currentvehicle, onlydate, secondte;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public adapter_searchexpense(Context cx, String getdate, List<String> datetimelist, List<String> costlist, List<String> odolist, List<String> typelist, List<String> urllist) {
        this.onlydate = getdate;
        this.context = cx;
        this.datetime = datetimelist;
        this.cost = costlist;
        this.odometer = odolist;
        this.type = typelist;
        this.url = urllist;
    }

    @NonNull

    @Override
    public adapter_searchexpense.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_searchexpense.ViewHolder holder, int position) {
        holder.tvdate.setText(datetime.get(position).toString());
        holder.tvcost.setText(cost.get(position).toString());
        holder.tvodometer.setText(odometer.get(position).toString() + "\tKM");
        holder.tvtype.setText(type.get(position).toString());
        geturl = url.get(position).toString();
        if (geturl.equals("abcdef")) {
            holder.imereceipt.setImageResource(R.drawable.noreciept);
        } else {
            Glide.with(context).load(geturl).into(holder.imereceipt);
        }
        holder.layout.setOnClickListener(view -> {
            geturl = url.get(position).toString();
            Intent intent = new Intent(context, fullscreenImage_activity.class);
            intent.putExtra("fullscreenimageurl", geturl);
            intent.putExtra("getfirstdate", onlydate);
            intent.putExtra("getdate", datetime.get(position).toString());
            intent.putExtra("getcost", cost.get(position).toString());
            intent.putExtra("getodo", odometer.get(position).toString());
            intent.putExtra("gettype", type.get(position).toString());
            intent.putExtra("getactivity","search");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return datetime.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvdate, tvcost, tvtype, tvodometer;
        ImageView imereceipt;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvdate = itemView.findViewById(R.id.tvexpense_tvdateTime);
            tvcost = itemView.findViewById(R.id.tviewexpense_cost);
            tvodometer = itemView.findViewById(R.id.tviewexpense_odomete);
            tvtype = itemView.findViewById(R.id.tviewexpense_type);
            imereceipt = itemView.findViewById(R.id.tviewexpense_receipt);
            layout = itemView.findViewById(R.id.single_view_expenses);
        }
    }
}
