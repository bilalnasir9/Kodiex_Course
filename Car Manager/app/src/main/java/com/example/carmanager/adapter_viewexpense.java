package com.example.carmanager;

import android.app.AlertDialog;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class adapter_viewexpense extends RecyclerView.Adapter<adapter_viewexpense.ViewHolder> {
    Context context;
    List datetime,onlydate;
    List cost;
    List odometer;
    List type;
    List url;
    String geturl,userid,currentvehicle,only,secondte;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth =FirebaseAuth.getInstance();

    public adapter_viewexpense(Context cx,List<String> onlydatelist, List<String> datetimelist, List<String> costlist, List<String> odometerlist, List<String> typelist, List<String> urllist) {
        this.context = cx;
        this.datetime = datetimelist;
        this.cost = costlist;
        this.onlydate=onlydatelist;
        this.odometer = odometerlist;
        this.type = typelist;
        this.url = urllist;
    }

    @NonNull

    @Override
    public adapter_viewexpense.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_viewexpense.ViewHolder holder, int position) {
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
            intent.putExtra("getfirstdate", onlydate.get(position).toString());
            intent.putExtra("getdate", datetime.get(position).toString());
            intent.putExtra("getcost", cost.get(position).toString());
            intent.putExtra("getodo", odometer.get(position).toString());
            intent.putExtra("gettype", type.get(position).toString());
            intent.putExtra("getactivity","view");
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
