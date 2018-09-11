package alison.fivethingskotlin.viewmodel


import alison.fivethingskotlin.api.repository.FiveThingsRepository
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.view.ViewPager
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService

class FiveThingsViewModelFactory(private val repository: FiveThingsRepository, private val authState: AuthState?, private val authorizationService: AuthorizationService) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FiveThingsViewModel(repository, authState, authorizationService) as T
    }
}