package alison.fivethingskotlin.util

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FreeSpec

class TimesTest : FreeSpec( {

    "gets the minutes from a HH:MM string" {
        val timeString = "04:44"
        val actual = parseMinute(timeString)
        actual shouldEqual 44
    }

    "gets the hours from a HH:MM string" {
        val timeString = "04:44"
        val actual = parseHour(timeString)
        actual shouldEqual 4
    }

    "creates a HH:MM string from hour and minute" {
        val hour = 4
        val minute = 44
        val actual = timeToString(hour, minute)
        actual shouldEqual "04:44"
    }

})