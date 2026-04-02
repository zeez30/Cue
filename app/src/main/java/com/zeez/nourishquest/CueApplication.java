package com.zeez.nourishquest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class CueApplication extends Application {

    // Channel ID used when posting check-in reminder notifications
    public static final String REMINDER_CHANNEL_ID = "cue_mindful_reminders";
    public static final String REMINDER_CHANNEL_NAME = "Mindful Eating Reminders";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    // Notification channels must be registered before any notification is posted.
    // On API < 26 this is a no-op — channels don't exist below Android 8.
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    REMINDER_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Gentle reminders to check in with your hunger and fullness.");
            // Silent — no sound so it doesn't interrupt meals
            channel.setSound(null, null);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
