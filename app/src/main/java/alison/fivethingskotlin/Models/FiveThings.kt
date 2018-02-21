package alison.fivethingskotlin.Models

import alison.fivethingskotlin.Util.getFullDateFormat
import java.util.*


//TODO udpate this class when nagkumar updates the response
data class FiveThings(
        var date: Date,
        var one: String,
        var two: String,
        var three: String,
        var four: String,
        var five: String,
        var saved: Boolean
) {
    val isComplete: Boolean
        get() {
            return !one.isEmpty() &&
                    !two.isEmpty() &&
                    !three.isEmpty() &&
                    !four.isEmpty() &&
                    !five.isEmpty()
        }
    val fullDateString: String
        get() {
            return getFullDateFormat(date)
        }
    val isEmpty: Boolean
        get() {
            return one.isEmpty() &&
                    two.isEmpty() &&
                    three.isEmpty() &&
                    four.isEmpty() &&
                    five.isEmpty()
        }
    val savedString: String
        get() {
            return if (saved) "Saved" else "Save"
        }
}