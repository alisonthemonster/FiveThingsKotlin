package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Models.UserBody
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData

interface AuthRepository {

    fun postUserBody(userBody: UserBody): LiveData<Resource<Token>>
}