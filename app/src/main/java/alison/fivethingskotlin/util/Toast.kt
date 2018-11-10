package alison.fivethingskotlin.util

import alison.fivethingskotlin.R
import android.content.Context
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import android.widget.TextView
import android.widget.Toast

//mmmm toast
fun makeToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
    val toast = Toast.makeText(context, message, duration)
    val view = toast.view

    //Gets the actual oval background of the Toast then sets the colour filter
    view.background.setColorFilter(ContextCompat.getColor(context, R.color.bluegreen), PorterDuff.Mode.SRC_IN)

    //Gets the TextView from the Toast so it can be editted
    val text = view.findViewById(android.R.id.message) as TextView
    text.setTextColor(ContextCompat.getColor(context, R.color.white))

    toast.show()
}