package alison.fivethingskotlin.Models

import alison.fivethingskotlin.Util.getFullDateFormat
import java.util.*

data class FiveThingz(
        val date: Date,
        val things: List<String>,
        var saved: Boolean = true) {

    //todo: should date be saved by default? useful for retrofit gson stuffz

    val isComplete: Boolean
        get() {
            return !things[0].isEmpty() &&
                    !things[1].isEmpty() &&
                    !things[2].isEmpty() &&
                    !things[3].isEmpty() &&
                    !things[4].isEmpty()
        }

    val isEmpty: Boolean
        get() {
            return things[0].isEmpty() &&
                    things[1].isEmpty() &&
                    things[2].isEmpty() &&
                    things[3].isEmpty() &&
                    things[4].isEmpty()
        }

    val savedString: String
        get() {
            return if (saved) "Saved" else "Save"
        }

    val fullDateString: String
        get() {
            return getFullDateFormat(date)
        }
}


