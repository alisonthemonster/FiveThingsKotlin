package alison.fivethingskotlin.model

import alison.fivethingskotlin.util.getFullDateFormat
import java.util.*

data class FiveThings(
        val date: Date,
        val things: List<Thing>,
        var edited: Boolean = false,
        var inDatabase: Boolean = true) {

    var one = ""
    var two = ""
    var three = ""
    var four = ""
    var five = ""

    init {
        for (thing in things) {
            when (thing.order) {
                1 -> one = thing.content
                2 -> two = thing.content
                3 -> three = thing.content
                4 -> four = thing.content
                5 -> five = thing.content
            }
        }
    }


    val isEmpty: Boolean
        get() {

            return one.isEmpty() &&
                    two.isEmpty() &&
                    three.isEmpty() &&
                    four.isEmpty() &&
                    five.isEmpty()
        }

    val isComplete: Boolean
        get() {
            return !one.isEmpty() &&
                    !two.isEmpty() &&
                    !three.isEmpty() &&
                    !four.isEmpty() &&
                    !five.isEmpty()
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


