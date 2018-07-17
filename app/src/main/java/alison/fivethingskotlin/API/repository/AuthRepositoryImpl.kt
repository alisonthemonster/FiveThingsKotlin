package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.API.AuthService
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Models.UserBody
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepositoryImpl(private val authService: AuthService = AuthService.create()): AuthRepository {

    override fun postUserBody(userBody: UserBody): LiveData<Resource<Token>> {
        val token = MutableLiveData<Resource<Token>>()

        val call = authService.postUserBody(userBody)
        call.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>?, response: Response<Token>) {
                if (response.isSuccessful) {
                    val tokenResponse = response.body()?.token
                    token.value = Resource(Status.SUCCESS, "", Token(tokenResponse!!))
                } else {
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("message")
                    token.value = Resource(Status.ERROR, messageString, null)
                }
            }

            override fun onFailure(call: Call<Token>?, t: Throwable?) {
                token.value = Resource(Status.ERROR, t?.message, null)
            }
        })

        return token
    }

}