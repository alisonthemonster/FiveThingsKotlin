package alison.fivethingskotlin.Util

import java.text.SimpleDateFormat
import java.util.*

fun getDatabaseStyleDate(date: Date): String {
    return SimpleDateFormat("dd-MM-yy").format(date).toString()
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


