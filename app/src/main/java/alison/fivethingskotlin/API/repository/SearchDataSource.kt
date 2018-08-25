package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.Models.NetworkState
import alison.fivethingskotlin.Models.PaginatedSearchResults
import alison.fivethingskotlin.Models.SearchResult
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor


class SearchDataSource(private val service: FiveThingsService,
                       private val searchQuery: String,
                       private val retryExecutor: Executor,
                       private val token: String) : PageKeyedDataSource<String, SearchResult>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()


    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SearchResult>) {

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        //TODO get token

        val call = service.search(token, searchQuery, 50, 1)
        call.enqueue(object : Callback<PaginatedSearchResults> {
            override fun onResponse(call: Call<PaginatedSearchResults>?, response: Response<PaginatedSearchResults>) {
                if (response.isSuccessful) {
                    //TODO talk to nagkumar about changing the prev and next to just be indexes?
                    retry = null
                    networkState.postValue(NetworkState.LOADED)
                    initialLoad.postValue(NetworkState.LOADED)
                    callback.onResult(response.body()!!.results, null, "2")
                } else {
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("detail")
                    retry = {
                        loadInitial(params, callback)
                    }
                    networkState.postValue(NetworkState.error(messageString))
                }
            }

            override fun onFailure(call: Call<PaginatedSearchResults>?, t: Throwable) {
                retry = {
                    loadInitial(params, callback)
                }
                networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
            }
        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SearchResult>) {

        networkState.postValue(NetworkState.LOADING)

        val call = service.search(token, searchQuery, params.requestedLoadSize, params.key.toInt()) //TODO will this work with the way nagu has these?
        call.enqueue(object : Callback<PaginatedSearchResults> {
            override fun onResponse(call: Call<PaginatedSearchResults>?, response: Response<PaginatedSearchResults>) {
                if (response.isSuccessful) {
                    retry = null
                    callback.onResult(response.body()!!.results, response.body()!!.next) //TODO will this work with the way nagu has these?
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("detail")
                    //TODO retry
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.error(messageString))
                }
            }

            override fun onFailure(call: Call<PaginatedSearchResults>?, t: Throwable) {
                //TODO retry
                retry = {
                    loadAfter(params, callback)
                }
                networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
            }
        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SearchResult>) {
        // ignored, since we only ever append to our initial load
    }


}