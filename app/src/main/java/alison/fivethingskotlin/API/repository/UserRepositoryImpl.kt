package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.API.AuthService
import alison.fivethingskotlin.Models.CreateUserRequest
import alison.fivethingskotlin.Models.LogInUserRequest
import alison.fivethingskotlin.Models.Status.ERROR
import alison.fivethingskotlin.Models.Status.SUCCESS
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.Util.buildErrorResource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserRepositoryImpl(private val authService: AuthService = AuthService.create()): UserRepository {

    override fun createUser(userData: CreateUserRequest): LiveData<Resource<Token>> {

        val liveData = MutableLiveData<Resource<Token>>()

        val call = authService.createUser(userData)
        call.enqueue(object : Callback<Token> {
            override fun onFailure(call: Call<Token>?, t: Throwable) {
                liveData.value = Resource(ERROR, t.message, null)
            }

            override fun onResponse(call: Call<Token>?, response: Response<Token>) {
                if (response.isSuccessful) {
                    liveData.value = Resource(SUCCESS, "", response.body())
                } else {
                    liveData.value = buildErrorResource(response)
                }
            }
        })
        return liveData
    }

    override fun logIn(userData: LogInUserRequest): LiveData<Resource<Token>> {
        val liveData = MutableLiveData<Resource<Token>>()
        val call = authService.logInUser(userData)
        call.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful) {
                    liveData.value = Resource(SUCCESS, "", response.body())
                } else {
                    liveData.value = buildErrorResource(response)
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                liveData.value = Resource(ERROR, t.message, null)
            }
        })
        return liveData
    }

}
