package alison.fivethingskotlin.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.preference.PreferenceManager
import java.util.*


class NotificationScheduler {

    companion object {
        const val ALARM_TYPE_RTC = 100
        const val PENDING_INTENT = 101
    }

    private var alarmManager: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    fun setReminderNotification(context: Context) {

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

        val alarmIsOn = sharedPref.getBoolean("notif_parent", true)

        if (alarmIsOn) {

            val prefTime = sharedPref.getString("pref_time", "22:00")

            val hour = parseHour(prefTime)
            val minute = parseMinute(prefTime)

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }

            //if the current time is before the alarm time then we can set the alarm
            if (System.currentTimeMillis() < calendar.timeInMillis) {

                alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val intent = Intent(context, ReminderReceiver::class.java)
                alarmIntent = intent.let {
                    PendingIntent.getBroadcast(context, PENDING_INTENT, it, 0)
                }

                alarmManager?.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        alarmIntent)
            }
        } else {
            cancelNotifications(context)
        }
    }

    fun cancelNotifications(context: Context) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = intent.let {
            PendingIntent.getBroadcast(context, PENDING_INTENT, it, 0)
        }

        pendingIntent.cancel()

        alarmManager?.cancel(pendingIntent)

    }
}