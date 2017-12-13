package alison.fivethingskotlin.Models

import alison.fivethingskotlin.Util.getFullDateFormat
import java.util.*

/**
 * Created by Alison on 12/11/17.
 */
data class FiveThings(
        val date: Date,
        val one: String,
        val two: String,
        val three: String,
        val four: String,
        val five: String
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
}