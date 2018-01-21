package alison.fivethingskotlin.Models

import java.util.*

class StreakResponse(
    val longestStreak: Int,
    val currentStreak: Int)

class SentimentTotalsResponse(
    val negativeDays: Int,
    val positiveDays: Int
)

class SentimentOverTimeResponse() //TODO

class StrongestDatesResponse(
    val strongestDates: List<Date>
)

class MostMentionedEntityResponse(
    val mostMentionedEntities: List<Entity>
)
