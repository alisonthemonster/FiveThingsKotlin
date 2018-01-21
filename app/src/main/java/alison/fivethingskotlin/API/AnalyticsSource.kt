package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

interface AnalyticsSource {

    @GET("total_days_written/{username}")
    fun getTotalDaysWritten(@Path("username") username: String): Call<Int>

    @GET("streak/{username}")
    fun getStreak(@Path("username") username: String): Call<StreakResponse>

    @GET("sentiment_breakdown/{username}")
    fun getPositveAndNegativeDays(@Path("username") username: String): Call<SentimentTotalsResponse>

    @GET("date_joined/{username}")
    fun getDateJoined(@Path("username") username: String): Call<Date>

    @GET("sentiment_over_time/{username}")
    fun getSentimentOverTime(@Path("username") username: String): Call<SentimentOverTimeResponse>
        //user's sentiment over time. Optional headers = (start_date, end_date yyyy-mm-dd)

    @GET("dates_for_highest_emotion/{username}/{emotion}/{count}")
    fun getStrongestDatesForEmotion(@Path("username") username: String,
                                    @Path("emotion") emotion: Emotion,
                                    @Path("count") count: Int): Call<StrongestDatesResponse>

    @GET("most_mentioned_entity/{username}/{entityType}/{int:count}")
    fun getMostMentionedEntities(@Path("username") username: String,
                                 @Path("entityType") entityType: Entity,
                                 @Path("count") count: Int): Call<MostMentionedEntityResponse>
 }
