package alison.fivethingskotlin.api.repository

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.model.Listing
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.SearchResult
import alison.fivethingskotlin.model.Status
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor


class SearchRepositoryImpl(private val fiveThingsService: FiveThingsService,
                           private val networkExecutor: Executor): SearchRepository {

    //TODO delete, no longer in use
    override fun getSearchResults(token: String, keyword: String): LiveData<Resource<List<SearchResult>>> {
        val searchResults = MutableLiveData<Resource<List<SearchResult>>>()

        val call = fiveThingsService.searchAll(token, keyword)
        call.enqueue(object : Callback<List<SearchResult>> {
            override fun onResponse(call: Call<List<SearchResult>>?, response: Response<List<SearchResult>>) {
                if (response.isSuccessful) {
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