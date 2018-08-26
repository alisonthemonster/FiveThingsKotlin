package alison.fivethingskotlin.ViewModels


import alison.fivethingskotlin.API.repository.SearchRepository
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService

class SearchViewModelFactory(private val repository: SearchRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(repository) as T
    }
}