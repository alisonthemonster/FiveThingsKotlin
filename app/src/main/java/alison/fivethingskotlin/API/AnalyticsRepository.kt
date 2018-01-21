package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class AnalyticsRepository {
    private val analyticsAPI: AnalyticsSource

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://localhost:5000/api")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        analyticsAPI = retrofit.create(AnalyticsSource::class.java)
    }

    fun getTotalDaysWritten(username: String): Call<Int> {
        return analyticsAPI.getTotalDaysWritten(username)
    }

    fun getStreak(username: String): Call<StreakResponse> {
        return analyticsAPI.getStreak(username)
    }

    fun getPositveAndNegativeDays(username: String): Call<SentimentTotalsResponse> {
        return analyticsAPI.getPositveAndNegativeDays(username)
    }

    fun getDateJoined(username: String): Call<Date> {
        return analyticsAPI.getDateJoined(username)
    }

    fun getSentimentOverTime(username: String): Call<SentimentOverTimeResponse> {
        return analyticsAPI.getSentimentOverTime(username)
    }

    fun getStrongestDatesForEmotion(username: String, emotion: Emotion, count: Int): Call<StrongestDatesResponse> {
        return analyticsAPI.getStrongestDatesForEmotion(username, emotion, count)
    }

    fun getMostMentionedEntities(username: String, entity: Entity, count: Int): Call<MostMentionedEntityResponse> {
        return analyticsAPI.getMostMentionedEntities(username, entity, count)
    }
}