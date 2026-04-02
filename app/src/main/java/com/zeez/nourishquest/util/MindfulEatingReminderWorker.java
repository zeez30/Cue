package com.zeez.nourishquest.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zeez.nourishquest.CueApplication;
import com.zeez.nourishquest.R;
import com.zeez.nourishquest.ui.MainActivity;

import java.util.concurrent.TimeUnit;

// WorkManager worker that posts a daily check-in reminder notification.
// WorkManager handles battery optimisation, Doze mode, and reboot rescheduling automatically.
// Notification is silent (no sound) to avoid disrupting meals.
public class MindfulEatingReminderWorker extends Worker {

    private static final String WORK_TAG = "cue_daily_reminder";
    private static final int NOTIFICATION_ID = 1001;

    // Reminder messages — rotate by day so it doesn't feel repetitive
    private static final String[] REMINDER_MESSAGES = {
        "How is your body feeling right now?",
        "Pause for a moment — where are you on the hunger scale?",
        "Check in with yourself. What do you notice?",
        "Your body is communicating. Are you listening?",
        "A moment of body awareness, whenever you're ready.",
        "What do you need right now — food, rest, or something else?",
        "How hungry or full are you feeling at this moment?"
    };

    public MindfulEatingReminderWorker(@NonNull Context context,
                                       @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        postReminderNotification();
        return Result.success();
    }

    private void postReminderNotification() {
        Context context = getApplicationContext();

        // Tapping the notification opens the app directly
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Pick message based on day to avoid showing the same one every day
        int dayIndex = (int) (System.currentTimeMillis() / (24 * 60 * 60 * 1000L))
                % REMINDER_MESSAGES.length;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, CueApplication.REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_hunger)
                .setContentTitle("Cue")
                .setContentText(REMINDER_MESSAGES[dayIndex])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(null); // Silent

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    // Schedules a daily reminder. Safe to call multiple times — WorkManager deduplicates by tag.
    public static void scheduleDaily(Context context) {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                MindfulEatingReminderWorker.class,
                24, TimeUnit.HOURS,
                30, TimeUnit.MINUTES // flex window — can fire anytime in the last 30 mins
        ).addTag(WORK_TAG).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }

    public static void cancel(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG);
    }
}
