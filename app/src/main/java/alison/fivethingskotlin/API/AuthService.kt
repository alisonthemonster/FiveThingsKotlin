package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.Token
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

interface AuthService {

    companion object {
        fun create(): AuthService = RetrofitHelper.build().create(AuthService::class.java)
    }

    @POST("login")
    fun createUser(@Body email: String, @Body password: String): Call<Token>
    //TODO are we using usernames?

    @GET("login")
    fun logInUser(@Body email: String, @Body password: String): Call<Token>


}