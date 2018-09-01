package alison.fivethingskotlin.Models

import alison.fivethingskotlin.util.getFullDateFormat
import java.util.*

data class FiveThings(
        val date: Date,
        val things: List<Thing>,
        var edited: Boolean = false,
        var inDatabase: Boolean = true) {

    val isEmpty: Boolean
        get() {

            return things[0].isEmpty &&
                    things[1].isEmpty &&
                    things[2].isEmpty &&
                    things[3].isEmpty &&
                    things[4].isEmpty
        }

    val isComplete: Boolean
        get() {
            return !things[0].isEmpty &&
                !things[1].isEmpty &&
                !things[2].isEmpty &&
                !things[3].isEmpty &&
                !things[4].isEmpty
        }

    val savedString: String
        get() {
            if (!inDatabase) return "Save"

            return if (edited) "Save" else "Saved"
        }

    val fullDateString: String
        get() {
            return getFullDateFormat(date)
        }
}


