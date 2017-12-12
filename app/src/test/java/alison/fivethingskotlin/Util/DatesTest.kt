package alison.fivethingskotlin.Util

import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.FreeSpec
import io.kotlintest.matchers.shouldEqual
import java.util.*

class DatesTest: FreeSpec( {
    "converts from Date object" - {
        "to DD-MM-YY" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 22)
            val date = cal.time
            getDatabaseStyleDate(date) shouldEqual "22-01-17"
        }
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

})
