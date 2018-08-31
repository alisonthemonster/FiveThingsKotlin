package alison.fivethingskotlin.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*


class NotificationScheduler {

    companion object {
        const val ALARM_TYPE_RTC = 100
    }

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    fun setReminderNotification(context: Context, hour: Int, minute: Int) {

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        //if the current time is before the alarm time then we can set the alarm
        if (System.currentTimeMillis() < calendar.timeInMillis) {

            alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, ReminderReceiver::class.java)
            alarmIntent = intent.let {
                PendingIntent.getBroadcast(context, 0, it, 0)
            }

            alarmMgr?.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
            )
        }






    }
}