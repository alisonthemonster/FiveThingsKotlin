package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThingsRequest
import alison.fivethingskotlin.Models.FiveThings
import retrofit2.Call
import retrofit2.http.*

interface FiveThingsService {
    companion object {
        fun create(): FiveThingsService = RetrofitHelper.build().create(FiveThingsService::class.java)
    }

    @GET("info_for_day/{dayString}")
    fun getFiveThings(@Header("Authorization") token: String, @Path("dayString") day: String): Call<FiveThings>

    @GET("get_info_for_day/{dayString}")
    fun getFiveThingsWithAnalytics(@Header("Authorization") token: String, @Path("dayString") day: String): Call<FiveThings>

    @PUT("log_day")
    fun updateFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: FiveThingsRequest): Call<List<String>>

    @POST("log_day")
    fun writeFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: FiveThingsRequest): Call<List<String>>

    @HTTP(method = "DELETE", path = "log_day", hasBody = true)
    fun deleteFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: FiveThingsRequest): Call<List<String>>

    @GET("get_days_written")
    fun getWrittenDates(@Header("Authorization") token: String): Call<List<String>>

}
