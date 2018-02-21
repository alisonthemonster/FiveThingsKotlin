package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.Models.CreateUserRequest
import alison.fivethingskotlin.Models.LogInUserRequest
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData

interface UserRepository {

    fun createUser(userData: CreateUserRequest): LiveData<Resource<Token>>

    fun logIn(userData: LogInUserRequest): LiveData<Resource<Token>>
}
