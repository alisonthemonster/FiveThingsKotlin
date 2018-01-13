package alison.fivethingskotlin.Models

import io.kotlintest.specs.FreeSpec
import io.kotlintest.matchers.shouldEqual
import java.util.*

class DatesTest: FreeSpec( {
    "detects if Five things is complete" - {
        "with complete things" {
            val completeThings = FiveThings(
                    Date(),
                    "one",
                    "two",
                    "three",
                    "four",
                    "five",
                    true)
            completeThings.isComplete shouldEqual true
        }
        "with some empty things" {
            val completeThings = FiveThings(
                    Date(),
                    "",
                    "two",
                    "three",
                    "four",
                    "five",
                    true)
            completeThings.isComplete shouldEqual false
        }
        "with more empty things" {
            val completeThings = FiveThings(
                    Date(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    false)
            completeThings.isComplete shouldEqual false
        }
    }

    "returns correct string for boolean" - {
        "with saved == true" {
            val things = FiveThings(
                    Date(),
                    "one",
                    "two",
                    "three",
                    "four",
                    "five",
                    true)
            things.savedString shouldEqual "Saved"
        }
        "with saved == false" {
            val things = FiveThings(
                    Date(),
                    "one",
                    "two",
                    "three",
                    "four",
                    "five",
                    false)
            things.savedString shouldEqual "Save"
        }
    }
})

