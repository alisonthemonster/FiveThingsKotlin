package alison.fivethingskotlin.API


import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Models.UserBody
import retrofit2.Call
import retrofit2.http.*

interface AuthService {
    companion object {
        fun create(): AuthService = RetrofitHelper.build().create(AuthService::class.java)
    }

    @POST("nagkumar_will_tell_me")
    fun postUserBody(@Body body: UserBody): Call<Token>

}
