package alison.fivethingskotlin.settings

import alison.fivethingskotlin.R
import alison.fivethingskotlin.util.parseHour
import alison.fivethingskotlin.util.parseMinute
import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.support.v7.preference.DialogPreference
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet


class TimePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    init {
        isPersistent = true
        dialogLayoutResource = R.layout.pref_time_dialog
        val title = SpannableString(dialogTitle)
        title.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        dialogTitle = title

        val button = SpannableString(positiveButtonText)
        button.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, button.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        positiveButtonText = button

        val negButton = SpannableString(negativeButtonText)
        negButton.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, negButton.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        negativeButtonText = negButton
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