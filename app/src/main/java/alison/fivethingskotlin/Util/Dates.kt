package alison.fivethingskotlin.Util

import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*
import com.github.sundeepk.compactcalendarview.domain.Event


//TODO convert all Date objects to be something else like JodaTime or DateTime
fun getDatabaseStyleDate(date: Date): String {
    return SimpleDateFormat("yy-MM-dd").format(date).toString()
}

fun getDateFromDatabaseStyle(dateString: String): Date {
    return SimpleDateFormat("yy-MM-dd").parse(dateString)
}

fun getDayOfWeek(date: Date): String {
    val newDateFormat = SimpleDateFormat("dd/MM/yyyy")
    newDateFormat.applyPattern("EEEE")
    return newDateFormat.format(date)
}

fun getOrdinalDate(day: Int): String{
        val j = day % 10
        val k = day % 100
        if (j == 1 && k != 11) {
            return day.toString() + "st"
        }
        if (j == 2 && k != 12) {
            return day.toString() + "nd"
        }
        if (j == 3 && k != 13) {
            return day.toString() + "rd"
        }
        return day.toString() + "th"
}

fun getPreviousDate(date: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DATE, -1)
    return cal.time
}

fun getNextDate(date: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DATE, 1)
    return cal.time
}

fun getFullDateFormat(date: Date): String {
    val dayOfWeek = getDayOfWeek(date)
    val day = getOrdinalDate(getDay(date))
    val month = getMonth(date)
    val year = getYear(date)
    return "$dayOfWeek $month $day, $year"
}

private fun getDay(date: Date): Int {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal.get(Calendar.DATE)
}

fun getMonth(date: Date): String {
    val cal = Calendar.getInstance()
    cal.time = date
    val monthNumber = cal.get(Calendar.MONTH)
    val monthNames = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    return monthNames[monthNumber]
}

fun getYear(date: Date): Int {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal.get(Calendar.YEAR)
}

fun convertDateToEvent(date: Date): Event  {
    return Event(Color.WHITE, date.time)
}

