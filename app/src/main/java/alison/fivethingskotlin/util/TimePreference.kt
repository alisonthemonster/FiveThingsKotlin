package alison.fivethingskotlin.util

import alison.fivethingskotlin.R
import android.content.Context
import android.preference.DialogPreference
import android.util.AttributeSet
import android.content.res.TypedArray

class TimePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    private var time: Int = 0

    init {
        isPersistent = false
        dialogLayoutResource = R.layout.pref_time_dialog
    }

    override fun onGetDefaultValue(array: TypedArray, index: Int): Any {
        // Default value from attribute. Fallback value is set to 0.
        return array.getInt(index, 0)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean,
                                   defaultValue: Any) {
        // Read the value. Use the default value if it is not possible.
        setTime(if (restorePersistedValue)
            getPersistedInt(time)
        else
            defaultValue as Int)
    }

    fun getTime(): Int {
        return time
    }

    fun setTime(time: Int) {
        this.time = time
        // Save to Shared Preferences
        persistInt(time)
    }

}