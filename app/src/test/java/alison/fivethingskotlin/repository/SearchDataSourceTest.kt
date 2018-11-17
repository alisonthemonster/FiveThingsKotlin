package alison.fivethingskotlin.repository

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.search.SearchDataSource
import alison.fivethingskotlin.LiveDataTestUtil
import alison.fivethingskotlin.model.NetworkState
import alison.fivethingskotlin.model.PaginatedSearchResults
import alison.fivethingskotlin.model.SearchResult
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(JUnit4::class)
class SearchDataSourceTest {

    private lateinit var service: FiveThingsService
    private lateinit var dataSource: SearchDataSource

    private val searchCall = mock<Call<PaginatedSearchResults>>()

    private lateinit var results: PaginatedSearchResults

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()


    @Before
    fun setup() {
        service = mock()
        dataSource = SearchDataSource(service, "search query", mock(), "token")
        results = PaginatedSearchResults(10, "1", null, listOf(SearchResult(1, "content", "2012-12-10", 1),
                SearchResult(2, "content", "2012-12-09", 1),
                SearchResult(3, "content", "2012-12-08", 1),
                SearchResult(4, "content", "2012-12-07", 1),
                SearchResult(5, "content", "2012-12-06", 1),
                SearchResult(6, "content", "2012-12-05", 1),
                SearchResult(7, "content", "2012-12-04", 1),
                SearchResult(8, "content", "2012-12-03", 1),
                SearchResult(9, "content", "2012-12-02", 1),
                SearchResult(10, "content", "2012-12-01", 1)))

        `when`(service.search(any(), any(), any(), any())).thenReturn(searchCall)
    }

    @Test
    fun loadInitial_callsServiceOnce() {
        dataSource.loadInitial(mock(), mock())

        Mockito.verify(service, Mockito.times(1)).search("token", "search query", 50, 1)
    }

    @Test
    fun loadInitial_serviceIsSuccessful_updatesNetworkState() {
        Mockito.doAnswer {
            val callback: Callback<PaginatedSearchResults> = it.getArgument(0)
            callback.onResponse(searchCall, Response.success(results))
        }.`when`(searchCall).enqueue(any())

        dataSource.loadInitial(mock(), mock())

        val networkState = LiveDataTestUtil.getValue(dataSource.networkState)

        networkState shouldEqual NetworkState.LOADED
        Mockito.verify(service, Mockito.times(1)).search("token", "search query", 50, 1)
        Mockito.verify(searchCall, Mockito.times(1)).enqueue(any())
    }

    @Test
    fun loadInitial_serviceIsSuccessful_updatesInitialLoad() {
        Mockito.doAnswer {
            val callback: Callback<PaginatedSearchResults> = it.getArgument(0)
            callback.onResponse(searchCall, Response.success(results))
        }.`when`(searchCall).enqueue(any())

        dataSource.loadInitial(mock(), mock())

        val initialLoad = LiveDataTestUtil.getValue(dataSource.initialLoad)

        initialLoad shouldEqual NetworkState.LOADED
        Mockito.verify(service, Mockito.times(1)).search("token", "search query", 50, 1)
        Mockito.verify(searchCall, Mockito.times(1)).enqueue(any())
    }

}