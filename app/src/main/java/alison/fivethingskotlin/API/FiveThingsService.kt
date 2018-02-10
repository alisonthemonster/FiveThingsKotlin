package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import retrofit2.Call
import retrofit2.http.GET

interface FiveThingsService {
    companion object {
        fun create(): FiveThingsService = RetrofitHelper.build().create(FiveThingsService::class.java)
    }

    @GET("") //TODO
    fun getFiveThings(): Call<FiveThings>

}
