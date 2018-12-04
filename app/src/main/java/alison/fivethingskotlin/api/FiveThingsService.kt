package alison.fivethingskotlin.api

import alison.fivethingskotlin.model.EmotionCount
import alison.fivethingskotlin.model.PaginatedSearchResults
import alison.fivethingskotlin.model.SearchResult
import alison.fivethingskotlin.model.Thing
import io.reactivex.Observable
import lecho.lib.hellocharts.model.PointValue
import retrofit2.Call
import retrofit2.http.*

interface FiveThingsService {
    companion object {
        fun create(): FiveThingsService = RetrofitHelper.build().create(FiveThingsService::class.java)
    }

    @GET("get_things_for_day/{year}/{month}/{day}")
    fun getFiveThings(@Header("Authorization") token: String,
                      @Path("year") year: String,
                      @Path("month") month: String,
                      @Path("day") day: String): Observable<List<Thing>>

    @PUT("things_for_day/")
    fun updateFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: Array<Thing>): Observable<List<String>>

    @POST("things_for_day/")
    fun writeFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: Array<Thing>): Observable<List<String>>

    @GET("get_days_written")
    fun getWrittenDates(@Header("Authorization") token: String): Observable<List<String>>

    @GET("search_all/{keyword}")
    fun searchAll(@Header("Authorization") token: String, @Path("keyword") keyword: String): Call<List<SearchResult>>

    @GET("search/{keyword}")
    fun search(@Header("Authorization") token: String,
               @Path("keyword") keyword: String,
               @Query("page_size") pageSize: Int,
               @Query("page") page: Int): Call<PaginatedSearchResults>

    @GET("sentiment_over_time")
    fun getSentimentOverTime(@Header("Authorization") token: String,
                             @Header("start_date") startDate: String,
                             @Header("end_date") endDate: String): Observable<List<PointValue>>

    @GET("count_emotion")
    fun getEmotionCounts(@Header("Authorization") token: String,
                         @Header("start_date") startDate: String,
                         @Header("end_date") endDate: String): Observable<List<EmotionCount>>

}
