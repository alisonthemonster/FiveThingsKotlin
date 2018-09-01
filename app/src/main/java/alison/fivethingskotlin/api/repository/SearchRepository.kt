package alison.fivethingskotlin.api.repository

import alison.fivethingskotlin.model.Listing
import alison.fivethingskotlin.model.SearchResult
import alison.fivethingskotlin.util.Resource
import android.arch.lifecycle.LiveData

interface SearchRepository {

    fun getSearchResults(token: String, keyword: String): LiveData<Resource<List<SearchResult>>>

    fun getPaginatedSearchResults(token: String, keyword: String, pageSize: Int, page: Int): Listing<SearchResult>

}