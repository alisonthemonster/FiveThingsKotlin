package alison.fivethingskotlin.ViewModels


import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class FiveThingsViewModelFactory(val token: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(String::class.java).newInstance(token)
    }

}