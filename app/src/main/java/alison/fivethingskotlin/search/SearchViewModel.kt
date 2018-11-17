package alison.fivethingskotlin.search

import alison.fivethingskotlin.search.SearchRepository
import alison.fivethingskotlin.model.Listing
import alison.fivethingskotlin.model.SearchResult
import alison.fivethingskotlin.model.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel

class SearchViewModel(val repository: SearchRepository): ViewModel() {


    private val repoResult = MutableLiveData<Listing<SearchResult>>()

    //these are observed by the fragment
    val searchResults = switchMap(repoResult) { it.pagedList }!!
    val networkState = switchMap(repoResult) { it.networkState }!!
    val refreshState = switchMap(repoResult) { it.refreshState }!!

    fun getSearchResults(token: String, keyword: String): LiveData<Resource<List<SearchResult>>> {

        return repository.getSearchResults(token, keyword)
    }

    //kicks of the initial search
    fun getPaginatedSearchResults(token: String, keyword: String, pageSize: Int, page: Int) {
        repoResult.value = repository.getPaginatedSearchResults(token, keyword, pageSize, page)
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

}