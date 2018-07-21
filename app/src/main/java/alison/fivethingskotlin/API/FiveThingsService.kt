package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.FiveThingsRequest
import alison.fivethingskotlin.Models.Thing
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
                                   @Path("day") day: String): Call<List<Thing>>

    @PUT("things_for_day")
    fun updateFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: FiveThingsRequest): Call<List<String>>

    @POST("things_for_day")
    fun writeFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: FiveThingsRequest): Call<List<String>>

    @GET("get_days_written")
    fun getWrittenDates(@Header("Authorization") token: String): Call<List<String>>

}
