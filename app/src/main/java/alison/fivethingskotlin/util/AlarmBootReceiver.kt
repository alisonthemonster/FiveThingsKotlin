package alison.fivethingskotlin.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmBootReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("blerg", "in onReceive for AlarmBootReceiver")

            val notificationScheduler = NotificationScheduler()
            notificationScheduler.setReminderNotification(context, 18, 30) //TODO get time from shared prefs settings
        }
    }
}