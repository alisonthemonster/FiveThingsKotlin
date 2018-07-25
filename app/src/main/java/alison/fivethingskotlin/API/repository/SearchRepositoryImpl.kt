package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.Models.PaginatedSearchResults
import alison.fivethingskotlin.Models.SearchResult
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepositoryImpl(private val fiveThingsService: FiveThingsService = FiveThingsService.create()): SearchRepository {

    override fun getSearchResults(token: String, keyword: String): LiveData<Resource<List<SearchResult>>> {
        val searchResults = MutableLiveData<Resource<List<SearchResult>>>()

        val call = fiveThingsService.searchAll(token, keyword)
        call.enqueue(object : Callback<List<SearchResult>> {
            override fun onResponse(call: Call<List<SearchResult>>?, response: Response<List<SearchResult>>) {
                if (response.isSuccessful) {
                    Log.d("blerg", "call is happy")
                    searchResults.value = Resource(Status.SUCCESS, "", response.body())
                } else {
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("detail")
                    searchResults.value = Resource(Status.ERROR, messageString, null)
                }
            }

            override fun onFailure(call: Call<List<SearchResult>>?, t: Throwable?) {
                searchResults.value = Resource(Status.ERROR, t?.message, null)
            }
        })

        return searchResults
    }

    override fun getPaginatedSearchResults(token: String, keyword: String): LiveData<Resource<PaginatedSearchResults>> {
        val searchResults = MutableLiveData<Resource<PaginatedSearchResults>>()

        val call = fiveThingsService.search(token, keyword)
        call.enqueue(object : Callback<PaginatedSearchResults> {
            override fun onResponse(call: Call<PaginatedSearchResults>?, response: Response<PaginatedSearchResults>) {
                if (response.isSuccessful) {
                    searchResults.value = Resource(Status.SUCCESS, "", response.body())
                } else {
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("detail")
                    searchResults.value = Resource(Status.ERROR, messageString, null)
                }
            }

            override fun onFailure(call: Call<PaginatedSearchResults>?, t: Throwable?) {
                searchResults.value = Resource(Status.ERROR, t?.message, null)
            }
        })

        return searchResults
    }

}