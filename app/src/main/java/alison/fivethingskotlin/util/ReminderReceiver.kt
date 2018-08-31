package alison.fivethingskotlin.util

import alison.fivethingskotlin.PromoActivity
import alison.fivethingskotlin.ContainerActivity.Companion.CHANNEL_ID
import alison.fivethingskotlin.R
import alison.fivethingskotlin.util.NotificationScheduler.Companion.ALARM_TYPE_RTC
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import java.util.*

class ReminderReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("blerg", "Alarm received by BR")


        val intentToRepeat = Intent(context, PromoActivity::class.java)
        intentToRepeat.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //set flag to restart/relaunch the app
        val pendingIntent = PendingIntent.getActivity(context, ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT)


        //Build notification
        createLocalNotification(context, pendingIntent)

        //TODO check intent action

    }

    private fun createLocalNotification(context: Context, pendingIntent: PendingIntent) {

        Log.d("blerg", "Creating notif")

        val notificationBuilder =  NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.five_things_logo)
                .setContentTitle("Five Things Reminder")
                .setContentText("It's time to write down your Five Things!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) //removes notif when tapped

        //TODO add snoozing capability
            //https://developer.android.com/training/notify-user/build-notification#Actions

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(ALARM_TYPE_RTC, notificationBuilder.build())
    }

}