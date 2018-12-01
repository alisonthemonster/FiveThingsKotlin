package alison.fivethingskotlin.util

import android.graphics.Color
import com.github.sundeepk.compactcalendarview.domain.Event
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.FreeSpec
import io.kotlintest.matchers.shouldEqual
import java.util.*

class DatesTest: FreeSpec( {
    "converts from Date object" - {
        "to YY-MM-DD" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 22)
            val date = cal.time
            getDatabaseStyleDate(date) shouldEqual "2017-01-22"
        }
    }

    "converts from yy-MM-dd" - {
        "to Date object" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 22)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val date = cal.time
            getDateFromDatabaseStyle("2017-01-22") shouldEqual date
        }
    }

    "converts from Wednesday January 3rd, 2018 to a date object" {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 22)

        val date = cal.time
        getDateFromFullDateFormat("Wednesday January 22nd, 2017").toString() shouldEqual date.toString()
    }

    "gets the month from a date object" {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 22)
        val date = cal.time
        getMonth(date) shouldEqual "January"
    }

    "gets the year from a date object" {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 22)
        val date = cal.time
        getYear(date) shouldEqual 2017
    }

    "gets the month number from a date object" {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 22)
        val date = cal.time
        getMonthNumber(date) shouldEqual 1
    }

    "gets the month number from a month string" {
        getMonthNumber("January") shouldEqual 0
        getMonthNumber("February") shouldEqual 1
        getMonthNumber("March") shouldEqual 2
        getMonthNumber("April") shouldEqual 3
        getMonthNumber("May") shouldEqual 4
        getMonthNumber("June") shouldEqual 5
        getMonthNumber("July") shouldEqual 6
        getMonthNumber("August") shouldEqual 7
        getMonthNumber("September") shouldEqual 8
        getMonthNumber("October") shouldEqual 9
        getMonthNumber("November") shouldEqual 10
        getMonthNumber("December") shouldEqual 11
    }

    "gets day of week" - {
        "from a date object" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 22)
            val date = cal.time
            getDayOfWeek(date) shouldEqual "Sunday"
        }
    }

    "gets short day of week" - {
        "from a date object" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 22)
            val date = cal.time
            getDayOfWeek(date) shouldEqual "Sun"
        }
    }

    "ordinals" {
        forAll(table(
                headers("raw", "formatted"),
                row(1, "1st"),
                row(2, "2nd"),
                row(3, "3rd"),
                row(4, "4th"),
                row(5, "5th"),
                row(6, "6th"),
                row(7, "7th"),
                row(8, "8th"),
                row(9, "9th"),
                row(0, "0th")
        )) { raw, formatted ->
            getOrdinalDate(raw) shouldEqual formatted
        }
    }

    "gets previous date object given a date" {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 22)
        val date = cal.time
        cal.set(Calendar.DAY_OF_MONTH, 21)
        val newDate = cal.time
        getPreviousDate(date) shouldEqual newDate
    }

    "gets previous date object given a date" {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 22)
        val date = cal.time
        cal.set(Calendar.DAY_OF_MONTH, 23)
        val newDate = cal.time
        getNextDate(date) shouldEqual newDate
    }

    "builds a pretty date" - {
        "for 12/12/17" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.DECEMBER)
            cal.set(Calendar.DAY_OF_MONTH, 12)
            val date = cal.time
            getFullDateFormat(date) shouldEqual "Tuesday December 12th, 2017"
        }
        "for 01/31/16" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2016)
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 31)
            val date = cal.time
            getFullDateFormat(date) shouldEqual "Sunday January 31st, 2016"
        }
    }

    "builds an event object from a Date" {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.MONTH, Calendar.DECEMBER)
        cal.set(Calendar.DAY_OF_MONTH, 12)
        val date = cal.time
        val event = Event(Color.WHITE, date.time)
        convertDateToEvent(date) shouldEqual event
    }

    "Calculates the days between two dates" - {
        "for different days" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.DECEMBER)
            cal.set(Calendar.DAY_OF_MONTH, 12)
            val date1 = cal.time
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.DECEMBER)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val date2 = cal.time
            getDaysBetween(date2, date1) shouldEqual 11
        }
        "for the same day" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.DECEMBER)
            cal.set(Calendar.DAY_OF_MONTH, 12)
            val date1 = cal.time
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.DECEMBER)
            cal.set(Calendar.DAY_OF_MONTH, 12)
            val date2 = cal.time
            getDaysBetween(date2, date1) shouldEqual 0
        }
        "for days in the past" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.DECEMBER)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val date1 = cal.time
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.DECEMBER)
            cal.set(Calendar.DAY_OF_MONTH, 12)
            val date2 = cal.time
            getDaysBetween(date2, date1) shouldEqual -11
        }
    }
})
