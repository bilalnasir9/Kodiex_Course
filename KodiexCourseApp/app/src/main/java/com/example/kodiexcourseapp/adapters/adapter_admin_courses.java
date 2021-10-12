package com.example.kodiexcourseapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kodiexcourseapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class adapter_admin_courses extends RecyclerView.Adapter<adapter_admin_courses.vieholder> {
    Context context;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    List<String> title, keys, url;
    int curriculum_key;
    char charAt;

    public adapter_admin_courses(Context context, List<String> list_coursekey, List<String> list_title, List<String> list_url) {
        this.context = context;
        title = list_title;
        keys = list_coursekey;
        url = list_url;
    }

    @NonNull
    @Override
    public adapter_admin_courses.vieholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_admin_courses, parent, false);
        return new vieholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_admin_courses.vieholder holder, int position) {
        holder.tv_title.setText(title.get(position).toString());
        Glide.with(context).load(url.get(position).toString()).into(holder.imageView);
        holder.button_addlecture.setOnClickListener(view -> {
            reference.child("admin").child("counter_curriculum").child("course_" + keys.get(position)).get()
                    .addOnCompleteListener(task -> {
                        try {
                            curriculum_key = Integer.parseInt(Objects.requireNonNull(task.getResult().getValue()).toString());
                            curriculum_key = curriculum_key + 1;
                            final Dialog dialog = new Dialog(context);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.layout_admin_add_newcurriculum);
                            EditText ettitle = dialog.findViewById(R.id.edittext_admin_courses_newcurriculum_title);
                            EditText etduration = dialog.findViewById(R.id.edittext_admin_courses_newcurriculum_duration);
                            EditText eturl = dialog.findViewById(R.id.edittext_admin_courses_newcurriculum_url);
                            AppCompatButton saveButton = dialog.findViewById(R.id.btn_admin_courses_addnewcurriculum_save);
                            AppCompatButton cancelButton = dialog.findViewById(R.id.btn_admin_courses_addnewcurriculum_cancel);
                            dialog.show();
                            saveButton.setOnClickListener(v -> {
                                String gettitle = ettitle.getText().toString();
                                String getduration = etduration.getText().toString();
                                String geturl = eturl.getText().toString();
                                try {
                                    charAt = getduration.charAt(2);
                                } catch (Exception ignored) {
                                }
                                if (TextUtils.isEmpty(gettitle)) {
                                    ettitle.setError("Input required");
                                } else if (TextUtils.isEmpty(getduration)) {
                                    etduration.setError("Input required");
                                } else if (etduration.length()!=5 || !String.valueOf(charAt).equals(":")) {
                                    etduration.setError("Invalid format");
                                } else if (TextUtils.isEmpty(geturl)) {
                                    eturl.setError("Input required");
                                } else {
                                    Pattern pattern = Pattern.compile(
                                            "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                                            Pattern.CASE_INSENSITIVE);
                                    Matcher matcher = pattern.matcher(geturl);
                                    if (matcher.matches()) {
                                        String url = matcher.group(1);
                                        reference.child("admin").child("courses").child(keys.get(position)).child("Curriculum")
                                                .child(String.valueOf(curriculum_key)).child("duration").setValue(getduration);

                                        reference.child("admin").child("courses").child(keys.get(position)).child("Curriculum")
                                                .child(String.valueOf(curriculum_key)).child("link").setValue(url);

                                        reference.child("admin").child("courses").child(keys.get(position)).child("Curriculum")
                                                .child(String.valueOf(curriculum_key)).child("title").setValue(gettitle);
                                        reference.child("admin").child("counter_curriculum").child("course_" + keys.get(position)).setValue(String.valueOf(curriculum_key));
                                        Toast.makeText(context, "Task Successful!", Toast.LENGTH_SHORT).show();
                                        ((Activity) context).recreate();
                                        dialog.dismiss();
                                    }
                                    else {
                                        Toast.makeText(context, "URL is invalid!", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            });
                            cancelButton.setOnClickListener(v -> {
                                dialog.dismiss();
                            });

                            Window window = dialog.getWindow();
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        } catch (Exception exception) {
//                            Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public static class vieholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_title;
        MaterialButton button_addlecture;

        public vieholder(@NonNull View itemView) {
            super(itemView);
            button_addlecture = itemView.findViewById(R.id.btn_admin_courses_addnewlecture);
            tv_title = itemView.findViewById(R.id.textview_admin_courses_title);
            imageView = itemView.findViewById(R.id.imageView_admin_courses);

        }
    }
}
