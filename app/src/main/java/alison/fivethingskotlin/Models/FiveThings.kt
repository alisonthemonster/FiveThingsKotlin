package alison.fivethingskotlin.Models

import alison.fivethingskotlin.Util.getFullDateFormat
import java.util.*


//TODO update this class when nagkumar updates the response
data class FiveThings(
        var naguDate: Date,
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
            return getFullDateFormat(naguDate)
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