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
                    "five")
            completeThings.isComplete shouldEqual true
        }
        "with some empty things" {
            val completeThings = FiveThings(
                    Date(),
                    "",
                    "two",
                    "three",
                    "four",
                    "five")
            completeThings.isComplete shouldEqual false
        }
        "with more empty things" {
            val completeThings = FiveThings(
                    Date(),
                    "",
                    "",
                    "",
                    "",
                    "")
            completeThings.isComplete shouldEqual false
        }
    }
})

