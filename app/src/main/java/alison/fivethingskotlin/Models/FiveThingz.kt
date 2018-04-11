package alison.fivethingskotlin.Models

import alison.fivethingskotlin.Util.getFullDateFormat
import java.util.*

data class FiveThingz(
        val _id: FiveThingsId,
        val user_id: UserId,
        val naguDate: NaguDate,
        val things: List<Thing>,
        var saved: Boolean = false) {

    val isComplete: Boolean
        get() {
            return !things[0].isEmpty &&
                    !things[1].isEmpty &&
                    !things[2].isEmpty &&
                    !things[3].isEmpty &&
                    !things[4].isEmpty
        }

    val isEmpty: Boolean
        get() {
            return things[0].isEmpty &&
                    things[1].isEmpty &&
                    things[2].isEmpty &&
                    things[3].isEmpty &&
                    things[4].isEmpty
        }

    val savedString: String
        get() {
            return if (saved) "Saved" else "Save"
        }

    val fullDateString: String
        get() {
            return getFullDateFormat(naguDate.date)
        }
}

data class NaguDate(
    val date: Date)

data class FiveThingsId(
    val oid: String)

data class Thing(val content: String) {

    val isEmpty: Boolean
        get() {
            return content.isEmpty()
        }
}

data class UserId(
    val oid: String)

