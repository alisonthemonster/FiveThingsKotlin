package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.FiveThingsRequest
import alison.fivethingskotlin.Models.Message
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface FiveThingsService {
    companion object {
        fun create(): FiveThingsService = RetrofitHelper.build().create(FiveThingsService::class.java)
    }

    @GET("get_info_for_day/{dayString}")
    fun getFiveThings(@Header("Authorization") token: String, @Path("dayString") day: String): Call<FiveThings> //TODO change

    @PUT("log_day")
    fun updateFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: FiveThingsRequest): Call<Message> //TODO what kind of response is this?

    @POST("log_day")
    fun writeFiveThings(@Header("Authorization") token: String, @Body fiveThingsRequest: FiveThingsRequest): Call<Message>

    @HTTP(method = "DELETE", path = "log_day", hasBody = true)
    fun deleteFiveThings(@Header("Authorization") token: String, @Body dateString: String): Call<Response<Void>>

    @GET("get_days_written")
    fun getWrittenDates(@Header("Authorization") token: String): Call<List<String>>

}
