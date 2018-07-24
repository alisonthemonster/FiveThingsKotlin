package alison.fivethingskotlin.Models

import io.kotlintest.specs.FreeSpec
import io.kotlintest.matchers.shouldEqual
import java.util.*

class FiveThingsTest: FreeSpec( {
    "detects if Five things is complete" - {
        "with complete things" {
            val completeThings = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "one", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    false,
                    false)
            completeThings.isComplete shouldEqual true
        }
        "with some empty things" {
            val completeThings = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    false,
                    false)
            completeThings.isComplete shouldEqual false
        }
        "with more empty things" {
            val completeThings = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "", 1),
                            Thing("MM-DD-YYYY", "", 2),
                            Thing("MM-DD-YYYY", "", 3),
                            Thing("MM-DD-YYYY", "", 4),
                            Thing("MM-DD-YYYY", "", 5)),
                    false,
                    false)
            completeThings.isComplete shouldEqual false
        }
    }

    "detects if Five things is empty" - {
        "with complete things" {
            val completeThings = FiveThings(
                Date(),
                    listOf(Thing("MM-DD-YYYY", "one", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    false,
                false)
            completeThings.isEmpty shouldEqual false
        }
        "with some empty things" {
            val completeThings = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    false,
                    false)
            completeThings.isEmpty shouldEqual false
        }
        "with more empty things" {
            val completeThings = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "", 1),
                            Thing("MM-DD-YYYY", "", 2),
                            Thing("MM-DD-YYYY", "", 3),
                            Thing("MM-DD-YYYY", "", 4),
                            Thing("MM-DD-YYYY", "", 5)),
                    false,
                    false)
            completeThings.isEmpty shouldEqual true
        }
    }

    "returns correct string for boolean" - {
        "with inDatabase and edited" {
            val things = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "one", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    true,
                    true)
            things.savedString shouldEqual "Save"
        }
        "In the database and not edited" {
            val things = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "one", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    false,
                    true)
            things.savedString shouldEqual "Saved"
        }
        "Not in the database and not edited" {
            val things = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "one", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    false,
                    false)
            things.savedString shouldEqual "Save"
        }
        "Not in the database but edited" {
            val things = FiveThings(
                    Date(),
                    listOf(Thing("MM-DD-YYYY", "one", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    true,
                    false)
            things.savedString shouldEqual "Save"
        }
    }

    "returns correct date string for five things" - {
        "For this date" {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2017)
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 22)
            val date = cal.time

            val things = FiveThings(
                    date,
                    listOf(Thing("MM-DD-YYYY", "one", 1),
                            Thing("MM-DD-YYYY", "two", 2),
                            Thing("MM-DD-YYYY", "three", 3),
                            Thing("MM-DD-YYYY", "four", 4),
                            Thing("MM-DD-YYYY", "five", 5)),
                    false,
                    false)
            things.fullDateString shouldEqual "Sunday January 22nd, 2017"
        }
    }

    //TODO write tests for the date string getter
})

