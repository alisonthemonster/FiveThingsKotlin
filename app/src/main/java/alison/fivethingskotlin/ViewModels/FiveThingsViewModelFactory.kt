package alison.fivethingskotlin.ViewModels


import alison.fivethingskotlin.API.repository.FiveThingsRepository
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class FiveThingsViewModelFactory(private val token: String, private val repository: FiveThingsRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FiveThingsViewModel(token, repository) as T
    }
}