package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.util.TimePreference
import android.content.Context
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import kotlinx.android.synthetic.main.pref_time_dialog.*
import android.widget.TimePicker




class TimePreferenceFragment: PreferenceDialogFragmentCompat() {

    var timePicker: TimePicker? = null

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

            val value = pref.timeToString(pref.hour, pref.minute)
            if (pref.callChangeListener(value)) pref.persistStringValue(value)
        }
    }

}