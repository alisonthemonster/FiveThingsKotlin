package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.CreateUserRequest
import alison.fivethingskotlin.Models.LogInUserRequest
import alison.fivethingskotlin.Models.Token
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET

interface AuthService {

    companion object {
        fun create(): AuthService = RetrofitHelper.build().create(AuthService::class.java)
    }

    @POST("signup")
    fun createUser(@Body createUserRequest: CreateUserRequest): Call<Token>

    @POST("login")
    fun logInUser(@Body logInUserRequest: LogInUserRequest): Call<Token>

}