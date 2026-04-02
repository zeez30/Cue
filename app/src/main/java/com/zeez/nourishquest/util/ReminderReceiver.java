package com.zeez.nourishquest.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Receives BOOT_COMPLETED so WorkManager can reschedule reminders after a device restart.
// WorkManager handles the actual rescheduling automatically — this receiver just needs
// to exist in the manifest for compatibility on API < 33.
public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // WorkManager re-enqueues its own periodic work after reboot automatically
    }
}
