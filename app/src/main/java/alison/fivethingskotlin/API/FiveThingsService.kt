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

    //TODO find way good way to get a token added to these
        //interceptor
        //inline

    @GET("get_info_for_day/{dayString}")
    fun getFiveThings(@Path("dayString") day: String): Call<FiveThings> //TODO change

    //TODO what are the differences between put, post, and delete
        //PUT: update a day
        //POST: post a day first time
        //DELETE: remove a day
    @PUT("log_day")
    fun updateFiveThings(fiveThingsRequest: FiveThingsRequest): Call<Message> //TODO what kind of response is this?

    @POST("log_day")
    fun writeFiveThings(fiveThingsRequest: FiveThingsRequest): Call<Message>

    @DELETE("log_day")
    fun deleteFiveThings(dateString: String): Call<Response<Void>>

    @GET("get_written_days")
    fun getWrittenDates(): Call<List<String>>

}
