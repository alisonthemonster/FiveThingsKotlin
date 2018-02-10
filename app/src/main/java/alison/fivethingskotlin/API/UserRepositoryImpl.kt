package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.CreateUserRequest
import alison.fivethingskotlin.Models.LogInUserRequest
import alison.fivethingskotlin.Models.Status.ERROR
import alison.fivethingskotlin.Models.Status.SUCCESS
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepositoryImpl: UserRepository {

    val retrofit = RetrofitHelper.build().create(AuthService::class.java)

    override fun createUser(userData: CreateUserRequest): LiveData<Resource<Token>> {

        val liveData = MutableLiveData<Resource<Token>>()
        Log.d("blerg", "about to make request")

        //TODO is email validation being done on server, or should client do it?

        val call = retrofit.createUser(userData)
        call.enqueue(object : Callback<Token> {
            override fun onFailure(call: Call<Token>?, t: Throwable?) {
                //TODO
                Log.d("blerg", "on failure babyyyy")
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<Token>?, response: Response<Token>) {
                Log.d("blerg", response.code().toString())
                Log.d("blerg", response.message())
                Log.d("blerg", response.isSuccessful.toString())
                if (response.isSuccessful) {
                    liveData.value = Resource(SUCCESS, "", response.body())
                } else {
                    liveData.value = Resource(ERROR, response.message(), response.body())
                }
            }
        })
        return liveData
    }

    override fun logIn(userData: LogInUserRequest): LiveData<Resource<Token>> {
        val liveData = MutableLiveData<Resource<Token>>()
        val call = retrofit.logInUser(userData)
        call.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                Log.d("blerg", response.code().toString())
                Log.d("blerg", response.message())
                Log.d("blerg", response.isSuccessful.toString())

                if (response.isSuccessful) {
                    liveData.value = Resource(SUCCESS, "", response.body())
                } else {
                    liveData.value = Resource(ERROR, response.message(), response.body())
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                //TODO
            }
        })
        return liveData
    }

}
