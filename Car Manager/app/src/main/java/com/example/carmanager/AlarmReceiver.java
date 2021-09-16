package com.example.carmanager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.dynamic.IFragmentWrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL_SAMPLE";
    List<Integer> dayslist = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        int dayofweek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int notificationId = intent.getIntExtra("notificationId", 0);
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");
        ArrayList selecteddays = intent.getStringArrayListExtra("selecteddays");
        for (int i = 0; i < selecteddays.size(); i++) {
            String checkday = String.valueOf(selecteddays.get(i));
            if (checkday.equals("Sunday")) {
                dayslist.add(i, 1);
            }
            if (checkday.equals("Monday")) {
                dayslist.add(i, 2);
            }
            if (checkday.equals("Tuesday")) {
                dayslist.add(i, 3);
            }
            if (checkday.equals("Wednesday")) {
                dayslist.add(i, 4);
            }
            if (checkday.equals("Thursday")) {
                dayslist.add(i, 5);
            }
            if (checkday.equals("Friday")) {
                dayslist.add(i, 6);
            }
            if (checkday.equals("Saturday")) {
                dayslist.add(i, 7);
            }
        }
        for (int k = 0; k < selecteddays.size(); k++) {
            try {
                if (dayofweek == dayslist.get(k)) {
                    // Call MainActivity when notification is tapped.
                    Intent mainIntent = new Intent(context, dashboard_activity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
                    // NotificationManager
                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // For API 26 and above
                        CharSequence channelName = "My Notification";
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
                        notificationManager.createNotificationChannel(channel);
                    }
//                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
//                            R.drawable.launcher);
                    // Prepare Notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
                    builder.setSmallIcon(R.drawable.launcher);
//                            builder.setLargeIcon(bitmap);
                    builder.setContentTitle(title)
                            .setContentText(message)
                            .setContentIntent(contentIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);
                    // Notify
                    notificationManager.notify(notificationId, builder.build());
                } else {
                    Toast.makeText(context, "You will be notify in future as per schedule", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
