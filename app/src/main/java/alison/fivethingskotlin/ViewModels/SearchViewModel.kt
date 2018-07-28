package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.API.repository.SearchRepository
import alison.fivethingskotlin.Models.SearchResult
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

class SearchViewModel(val repository: SearchRepository): ViewModel() {

    fun getSearchResults(token: String, keyword: String): LiveData<Resource<List<SearchResult>>> {
        return repository.getSearchResults(token, keyword)
    }

//    fun getPaginatedSearchResults(keyword: String): LiveData<Resource<PaginatedSearchResults>> {
//        return repository.getPaginatedSearchResults(token, keyword)
//    }

}