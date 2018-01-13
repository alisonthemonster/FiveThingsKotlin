package alison.fivethingskotlin.Models

import alison.fivethingskotlin.Util.getFullDateFormat
import java.util.*

data class FiveThings(
        var date: Date,
        var one: String,
        var two: String,
        var three: String,
        var four: String,
        var five: String
) {
    val isComplete: Boolean
        get() {
            return !one.isNullOrEmpty() &&
                    !two.isNullOrEmpty() &&
                    !three.isNullOrEmpty() &&
                    !four.isNullOrEmpty() &&
                    !five.isNullOrEmpty()
        }
    val fullDateString: String
        get() {
            return getFullDateFormat(date)
        }
    val isEmpty: Boolean
        get() {
            return one.isNullOrEmpty() &&
                    two.isNullOrEmpty() &&
                    three.isNullOrEmpty() &&
                    four.isNullOrEmpty() &&
                    five.isNullOrEmpty()
        }
}