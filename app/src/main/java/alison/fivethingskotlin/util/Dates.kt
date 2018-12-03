package alison.fivethingskotlin.util

import android.graphics.Color
import com.github.sundeepk.compactcalendarview.domain.Event
import org.joda.time.DateTime
import org.joda.time.Days
import java.text.SimpleDateFormat
import java.util.*


private val monthNames = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
private val monthNamesShort = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")


//TODO convert all NaguDate objects to be something else like JodaTime or DateTime
fun getDatabaseStyleDate(date: Date): String {
    return SimpleDateFormat("yyyy-MM-dd").format(date).toString()
}

fun getDateFromDatabaseStyle(dateString: String): Date {
    return SimpleDateFormat("yyyy-MM-dd").parse(dateString)
}

fun getDayOfWeek(date: Date): String {
    val newDateFormat = SimpleDateFormat("dd/MM/yyyy")
    newDateFormat.applyPattern("EEEE")
    return newDateFormat.format(date)
}

fun getDayOfWeekShort(date: Date): String {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US)
}

fun getOrdinalDate(day: Int): String {
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
    return subtractXDaysFromDate(date, 1)
}

fun getNextDate(date: Date): Date {
    return addXDaysToDate(date, 1)
}

fun getNextMonth(date: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.MONTH, 1)
    return cal.time
}

fun getFirstOfMonth(monthNumber: Int, year: Int): Date {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, monthNumber)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    return cal.time
}

fun addXDaysToDate(date: Date, x: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DATE, x)
    return cal.time
}

fun subtractXDaysFromDate(date: Date, x: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DATE, -x)
    return cal.time
}

fun getFullDateFormat(date: Date): String {
    val dayOfWeek = getDayOfWeek(date)
    val day = getOrdinalDate(getDay(date))
    val month = getMonth(date)
    val year = getYear(date)
    return "$dayOfWeek $month $day, $year"
}

fun getDateFromFullDateFormat(dateString: String): Date {
    val words = dateString.split(" ")
    val month = words[1]
    val day = words[2]
    val year = words[3]

    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year.toInt())
    cal.set(Calendar.MONTH, getMonthNumber(month))
    cal.set(Calendar.DAY_OF_MONTH, day.dropLast(3).toInt()) //remove "th,"

    return cal.time
}

fun getDay(date: Date): Int {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal.get(Calendar.DATE)
}

fun getMonth(date: Date): String {
    val cal = Calendar.getInstance()
    cal.time = date
    val monthNumber = cal.get(Calendar.MONTH)
    return monthNames[monthNumber]
}

fun getShortMonth(date: Date): String {
    val cal = Calendar.getInstance()
    cal.time = date
    val monthNumber = cal.get(Calendar.MONTH)
    return monthNamesShort[monthNumber]
}

fun getMonthNumber(date: Date): Int {
    val cal = Calendar.getInstance()
    cal.time = date
    val monthNumber = cal.get(Calendar.MONTH)
    val monthNames = arrayOf(Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER)
    return monthNames[monthNumber] + 1
}

fun getMonthNumber(monthString: String): Int {
    var monthNumber = -1
    for (month in monthNames) {
        monthNumber++
        if (month == monthString) {
            return monthNumber
        }
    }
    return monthNumber
}

fun getYear(date: Date): Int {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal.get(Calendar.YEAR)
}

fun getDateInAYear(currentYear: Date, year: Int): Date {
    val yearDifference = getYear(currentYear) - year
    return DateTime(currentYear).minusYears(yearDifference).toDate()
}

fun convertDateToEvent(date: Date): Event {
    return Event(Color.WHITE, date.time)
}

fun getDaysBetween(one: Date, two: Date): Int {
    return Days.daysBetween(DateTime(one), DateTime(two)).days
}

