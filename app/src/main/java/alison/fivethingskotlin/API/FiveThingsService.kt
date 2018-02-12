package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.FiveThingsRequest
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface FiveThingsService {
    companion object {
        fun create(): FiveThingsService = RetrofitHelper.build().create(FiveThingsService::class.java)
    }

    //TODO find way good way to get a token added to these
        //interceptor
        //inline

    @GET("get_info_for_day/{dayString}")
    fun getFiveThings(@Path("dayString") day: String): Call<FiveThings>

    //TODO what are the differences between put, post, and delete
    @PUT("log_day")
    fun writeFiveThings(fiveThingsRequest: FiveThingsRequest) //TODO what kind of response is this?

    @GET("get_written_days")
    fun getWrittenDates(): Call<List<String>>

}
