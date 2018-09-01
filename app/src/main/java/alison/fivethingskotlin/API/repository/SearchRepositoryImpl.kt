package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.Models.Listing
import alison.fivethingskotlin.Models.SearchResult
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.util.Log
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor


class SearchRepositoryImpl(private val fiveThingsService: FiveThingsService,
                           private val networkExecutor: Executor): SearchRepository {

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

    override fun getPaginatedSearchResults(token: String, keyword: String, pageSize: Int, page: Int): Listing<SearchResult> {
        val sourceFactory = SearchDataSourceFactory(fiveThingsService, keyword, networkExecutor, token)

        //initialize PagedLists on a background thread
        val livePagedList = LivePagedListBuilder(sourceFactory, pageSize)
                .setFetchExecutor(networkExecutor)
                .build()

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }

        return Listing(
                livePagedList,
                Transformations.switchMap(sourceFactory.sourceLiveData) {
                    it.networkState
                },
                retry = {
                    sourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )

    }

}