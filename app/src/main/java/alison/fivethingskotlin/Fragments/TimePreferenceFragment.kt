package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.util.NotificationScheduler
import alison.fivethingskotlin.util.TimePreference
import alison.fivethingskotlin.util.timeToString
import android.content.Context
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.TimePicker

class TimePreferenceFragment: PreferenceDialogFragmentCompat() {

    private var timePicker: TimePicker? = null

    override fun onCreateDialogView(context: Context): View {
        timePicker = TimePicker(context)
        return timePicker as TimePicker
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        timePicker?.setIs24HourView(false)
        val pref = preference as TimePreference
        timePicker?.hour = pref.hour
        timePicker?.minute = pref.minute
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val pref = preference as TimePreference
            pref.hour = timePicker?.hour!!
            pref.minute = timePicker?.minute!!

            val value = timeToString(pref.hour, pref.minute)
            if (pref.callChangeListener(value)) {
                pref.persistStringValue(value)
                NotificationScheduler().setReminderNotification(context!!)
            }
        }
    }

}