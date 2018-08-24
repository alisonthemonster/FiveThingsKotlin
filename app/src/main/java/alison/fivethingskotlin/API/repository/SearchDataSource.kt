package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.Models.PaginatedSearchResults
import alison.fivethingskotlin.Models.SearchResult
import android.arch.paging.PageKeyedDataSource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchDataSource(private val service: FiveThingsService,
                       private val searchQuery: String) : PageKeyedDataSource<String, SearchResult>() {


    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SearchResult>) {

        //TODO get token

        val call = service.search(token, searchQuery, 50, 1)
        call.enqueue(object : Callback<PaginatedSearchResults> {
            override fun onResponse(call: Call<PaginatedSearchResults>?, response: Response<PaginatedSearchResults>) {
                if (response.isSuccessful) {
                    //TODO talk to nagkumar about changing the prev and next to just be indexes?
                    callback.onResult(response.body()!!.results, null, "2")
                } else {
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("detail")
                    //TODO retry
                }
            }

            override fun onFailure(call: Call<PaginatedSearchResults>?, t: Throwable?) {
                //TODO retry
            }
        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SearchResult>) {

        val call = service.search(token, searchQuery, params.requestedLoadSize, params.key)
        call.enqueue(object : Callback<PaginatedSearchResults> {
            override fun onResponse(call: Call<PaginatedSearchResults>?, response: Response<PaginatedSearchResults>) {
                if (response.isSuccessful) {
                    callback.onResult(response.body()!!.results, response.body()!!.next) //TODO will this work with the way nagu has these?
                } else {
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("detail")
                    //TODO retry
                }
            }

            override fun onFailure(call: Call<PaginatedSearchResults>?, t: Throwable?) {
                //TODO retry
            }
        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SearchResult>) {
        // ignored, since we only ever append to our initial load
    }


}