package alison.fivethingskotlin.model

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

    //TODO instead of showing the words saved what about a check next to the date
    val savedString: String
        get() {
            return if (edited) "Saving" else "Saved"
        }

    val fullDateString: String
        get() {
            return getFullDateFormat(date)
        }

    val thingsCount: Int
        get() {
            var count = 0
            for (thing in things) {
                if (!thing.isEmpty) count++
            }
            return count
        }
}


