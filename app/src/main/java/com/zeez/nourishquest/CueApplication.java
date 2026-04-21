package com.zeez.nourishquest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class CueApplication extends Application {

    // Unique identifier for the mindful check-in notification channel
    public static final String REMINDER_CHANNEL_ID = "cue_mindful_reminders";
    public static final String REMINDER_CHANNEL_NAME = "Mindful Eating Reminders";

    @Override
    public void onCreate() {
        super.onCreate();
        initializeNotificationChannels();
    }

    /**
     * Registers notification channels for Android 8.0 (API 26) and above.
     * This setup is required before any notifications can be dispatched to the user.
     */
    private void initializeNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    REMINDER_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("Gentle reminders to check in with hunger and fullness signals.");

            // Audio is disabled to minimize interruption during meal times
            channel.setSound(null, null);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}