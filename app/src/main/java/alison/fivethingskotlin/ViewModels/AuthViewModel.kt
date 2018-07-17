package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.API.repository.AuthRepositoryImpl
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Models.UserBody
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

class AuthViewModel: ViewModel() {

    private val repository = AuthRepositoryImpl()

    fun postUserBody(userBody: UserBody): LiveData<Resource<Token>> {
        return repository.postUserBody(userBody)
    }
}