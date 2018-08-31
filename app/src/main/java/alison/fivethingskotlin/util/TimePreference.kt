package alison.fivethingskotlin.util

import alison.fivethingskotlin.R
import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet


class TimePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    private var time: Int = 0

    init {
        isPersistent = true
        dialogLayoutResource = R.layout.pref_time_dialog
    }

    var hour = 0
    var minute = 0

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val value: String =
            if (restoreValue)
                getPersistedString(defaultValue.toString())
            else
                defaultValue.toString()

        hour = parseHour(value)
        minute = parseMinute(value)
    }

    fun persistStringValue(value: String) {
        persistString(value)
    }

}