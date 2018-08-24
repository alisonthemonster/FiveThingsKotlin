package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.Models.PaginatedSearchResults
import alison.fivethingskotlin.Models.SearchResult
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData

interface SearchRepository {

    fun getSearchResults(token: String, keyword: String): LiveData<Resource<List<SearchResult>>>

    fun getPaginatedSearchResults(token: String, keyword: String, pageSize: Int, page: Int): LiveData<Resource<PaginatedSearchResults>>

}