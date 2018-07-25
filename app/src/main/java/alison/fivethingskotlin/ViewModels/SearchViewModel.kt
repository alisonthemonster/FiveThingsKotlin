package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.API.repository.SearchRepository
import alison.fivethingskotlin.Models.PaginatedSearchResults
import alison.fivethingskotlin.Models.SearchResult
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService

class SearchViewModel(val authorizationService: AuthorizationService, val authState: AuthState, val repository: SearchRepository): ViewModel() {

    fun getSearchResults(keyword: String): LiveData<Resource<List<SearchResult>>> {
        var result: LiveData<Resource<List<SearchResult>>>
        val errorResult = MutableLiveData<Resource<List<SearchResult>>>()
        result = errorResult

        authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                Log.e("blerg", "Negotiation for fresh tokens failed: $ex")
                errorResult.value = Resource(Status.ERROR, "Negotiation for fresh tokens failed: $ex", null)
            } else {
                idToken?.let {
                    result = repository.getSearchResults("Bearer $it", keyword)
                }
            }
        }
        return result
    }

//    fun getPaginatedSearchResults(keyword: String): LiveData<Resource<PaginatedSearchResults>> {
//        return repository.getPaginatedSearchResults(token, keyword)
//    }

}