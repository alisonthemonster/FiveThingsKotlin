package alison.fivethingskotlin.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.API.repository.SearchRepository
import alison.fivethingskotlin.API.repository.SearchRepositoryImpl
import alison.fivethingskotlin.Models.Listing
import alison.fivethingskotlin.Models.PaginatedSearchResults
import alison.fivethingskotlin.Models.SearchResult
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import com.nhaarman.mockito_kotlin.any
import io.kotlintest.mock.mock
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

@RunWith(JUnit4::class)
class SearchRepositoryTest {

    private lateinit var service: FiveThingsService
    private lateinit var repository: SearchRepository

    private val searchCall = mock<Call<PaginatedSearchResults>>()
    private val networkExecutor = Executor { command -> command.run() }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        service = mock()
        repository = SearchRepositoryImpl(service, networkExecutor)

        Mockito.`when`(service.search(any(), any(), any(), any())).thenReturn(searchCall)
    }

    @Test
    fun getPaginatedSearchResults_withNoResults() {
        val searchResults = emptyList<SearchResult>()
        val paginatedSearchResults = PaginatedSearchResults(1, null, null, searchResults)

        Mockito.doAnswer {
            val callback: Callback<PaginatedSearchResults> = it.getArgument(0)
            callback.onResponse(searchCall, Response.success(paginatedSearchResults))
        }.`when`(searchCall).enqueue(any())

        val listing = repository.getPaginatedSearchResults("token", "search query", 50, 1)
        assertThat(getPagedList(listing), `is`(searchResults))
    }

    @Test
    fun getPaginatedSearchResults_withOneResult() {
        val searchResults = listOf(
                SearchResult(2, "content", "2012-12-09", 1))
        val paginatedSearchResults = PaginatedSearchResults(1, null, null, searchResults)

        Mockito.doAnswer {
            val callback: Callback<PaginatedSearchResults> = it.getArgument(0)
            callback.onResponse(searchCall, Response.success(paginatedSearchResults))
        }.`when`(searchCall).enqueue(any())

        val listing = repository.getPaginatedSearchResults("token", "search query", 50, 1)
        assertThat(getPagedList(listing), `is`(searchResults))
    }

    private fun getPagedList(listing: Listing<SearchResult>): PagedList<SearchResult> {
        val observer = LoggingObserver<PagedList<SearchResult>>()
        listing.pagedList.observeForever(observer)
        assertThat(observer.value, `is`(notNullValue()))
        return observer.value!!
    }
}

/**
 * simple observer that logs the latest value it receives
 */
private class LoggingObserver<T> : Observer<T> {
    var value : T? = null
    override fun onChanged(t: T?) {
        this.value = t
    }
}