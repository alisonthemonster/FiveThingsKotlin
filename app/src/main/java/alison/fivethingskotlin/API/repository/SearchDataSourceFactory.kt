package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.Models.SearchResult
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import java.util.concurrent.Executor

class SearchDataSourceFactory(private val service: FiveThingsService,
                              private val query: String,
                              private val retryExecutor: Executor,
                              private val token: String) : DataSource.Factory<String, SearchResult>() {

    val sourceLiveData = MutableLiveData<SearchDataSource>()

    override fun create(): DataSource<String, SearchResult> {
        val source = SearchDataSource(service, query, retryExecutor, token)
        sourceLiveData.postValue(source)
        return source
    }

}