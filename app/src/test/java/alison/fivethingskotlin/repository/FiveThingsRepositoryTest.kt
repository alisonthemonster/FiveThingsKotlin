package alison.fivethingskotlin.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.API.repository.FiveThingsRepository
import alison.fivethingskotlin.API.repository.FiveThingsRepositoryImpl
import alison.fivethingskotlin.LiveDataTestUtil
import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Models.Thing
import alison.fivethingskotlin.Util.Resource
import android.arch.core.executor.testing.InstantTaskExecutorRule
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

@RunWith(JUnit4::class)
class FiveThingsRepositoryTest {

    private lateinit var service: FiveThingsService
    private lateinit var repository: FiveThingsRepository

    private val fiveThingsCall = mock<Call<List<Thing>>>()
    private val datesCall = mock<Call<List<String>>>()

    private lateinit var date: Date
    private lateinit var fiveThings: FiveThings
    private lateinit var things: List<Thing>
    private lateinit var dates: List<Date>
    private lateinit var dateStrings: List<String>
    private val token = "token"

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        service = mock()
        repository = FiveThingsRepositoryImpl(service)

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 22)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        date = cal.time
        cal.set(Calendar.DAY_OF_MONTH, 23)
        val nextDate = cal.time
        cal.set(Calendar.DAY_OF_MONTH, 24)
        val nextNextDate = cal.time

        things = listOf(Thing("01-22-2017", "one", 1),
                Thing("01-22-2017", "two", 2),
                Thing("01-22-2017", "three", 3),
                Thing("01-22-2017", "four", 4),
                Thing("01-22-2017", "five", 5))

        fiveThings = FiveThings(date, things, false, true)
        dates = listOf(date, nextDate, nextNextDate)
        dateStrings = listOf("2017-01-22", "2017-01-23", "2017-01-24")

        `when`(service.getFiveThings(any(), any(), any(), any())).thenReturn(fiveThingsCall)
        `when`(service.getWrittenDates(any())).thenReturn(datesCall)
        `when`(service.updateFiveThings(any(), any())).thenReturn(datesCall)
        `when`(service.writeFiveThings(any(), any())).thenReturn(datesCall)
    }

    @Test
    fun getFiveThings_callsServiceOnce() {
        repository.getFiveThings(token, date, mock())

        verify(service, times(1)).getFiveThings(token, "2017", "01", "22")
    }

    @Test
    fun getFiveThings_serviceIsSuccessful_returnsFiveThings() {
        doAnswer {
            val callback: Callback<List<Thing>> = it.getArgument(0)
            callback.onResponse(fiveThingsCall, Response.success(things))
        }.`when`(fiveThingsCall).enqueue(any())

        LiveDataTestUtil.getValue(repository.getFiveThings(token, date, mock()))

        verify(service, times(1)).getFiveThings(token, "2017", "01", "22")
        verify(fiveThingsCall, times(1)).enqueue(any())
    }

    @Test
    fun getFiveThings_serviceSends404_returnsEmptyFiveThings() {
        doAnswer {
            val callback: Callback<List<Thing>> = it.getArgument(0)
            callback.onResponse(fiveThingsCall, Response.error(404, mock<ResponseBody>()))
        }.`when`(fiveThingsCall).enqueue(any())

        LiveDataTestUtil.getValue(repository.getFiveThings(token, date, mock()))

        verify(service, times(1)).getFiveThings(token, "2017", "01", "22")
        verify(fiveThingsCall, times(1)).enqueue(any())
    }

    @Test
    fun getFiveThings_serviceFails_returnsErrorResource() {
        val exception = IOException("Exception thrown")

        doAnswer {
            val callback: Callback<List<Thing>> = it.getArgument(0)
            callback.onFailure(fiveThingsCall, exception)
        }.`when`(fiveThingsCall).enqueue(any())

        LiveDataTestUtil.getValue(repository.getFiveThings(token, date, mock()))

        verify(service, times(1)).getFiveThings(token, "2017", "01", "22")
        verify(fiveThingsCall, times(1)).enqueue(any())
    }


    @Test
    fun getWrittenDates_callsServiceOnce() {
        repository.getWrittenDates(token)

        verify(service, times(1)).getWrittenDates(token)
    }

    @Test
    fun getWrittenDates_serviceIsSuccessful_returnsDates() {
        val expected = Resource(Status.SUCCESS, "", dates)

        doAnswer {
            val callback: Callback<List<String>> = it.getArgument(0)
            callback.onResponse(datesCall, Response.success(dateStrings))
        }.`when`(datesCall).enqueue(any())

        val actual = LiveDataTestUtil.getValue(repository.getWrittenDates(token))

        verify(service, times(1)).getWrittenDates(token)
        verify(datesCall, times(1)).enqueue(any())
        actual shouldEqual expected
    }

    @Test
    fun getWrittenDates_serviceFails_returnsErrorResource() {
        val exception = IOException("Exception thrown")
        val expected = Resource(Status.ERROR, exception.message, null)

        doAnswer {
            val callback: Callback<List<String>> = it.getArgument(0)
            callback.onFailure(datesCall, exception)
        }.`when`(datesCall).enqueue(any())

        val actual = LiveDataTestUtil.getValue(repository.getWrittenDates(token))

        verify(service, times(1)).getWrittenDates(token)
        verify(datesCall, times(1)).enqueue(any())
        actual shouldEqual expected
    }

    @Test
    fun saveFiveThings_existingEntry_callsUpdateService() {
        val expected = Resource(Status.SUCCESS, "Date updated", dates)

        doAnswer {
            val callback: Callback<List<String>> = it.getArgument(0)
            callback.onResponse(datesCall, Response.success(dateStrings))
        }.`when`(datesCall).enqueue(any())

        val actual = LiveDataTestUtil.getValue(repository.saveFiveThings(token, fiveThings, mock()))

        verify(service, times(1)).updateFiveThings(any(), any())
        verify(datesCall, times(1)).enqueue(any())
        actual shouldEqual expected
    }

    @Test
    fun saveFiveThings_existingEntry_callbackFails_createsErrorResource() {
        val expected = Resource(Status.ERROR, "", null)

        doAnswer {
            val callback: Callback<List<String>> = it.getArgument(0)
            callback.onResponse(datesCall, Response.error(401, mock<ResponseBody>()))
        }.`when`(datesCall).enqueue(any())

        val actual = LiveDataTestUtil.getValue(repository.saveFiveThings(token, fiveThings, mock()))

        verify(service, times(1)).updateFiveThings(any(), any())
        verify(datesCall, times(1)).enqueue(any())
        actual shouldEqual expected
    }

    @Test
    fun saveFiveThings_existingEntry_serviceFails_returnsErrorResource() {
        val exception = IOException("Exception thrown")
        val expected = Resource(Status.ERROR, exception.message, null)

        doAnswer {
            val callback: Callback<List<String>> = it.getArgument(0)
            callback.onFailure(datesCall, exception)
        }.`when`(datesCall).enqueue(any())

        val actual = LiveDataTestUtil.getValue(repository.saveFiveThings(token, fiveThings, mock()))

        verify(service, times(1)).updateFiveThings(any(), any())
        verify(datesCall, times(1)).enqueue(any())
        actual shouldEqual expected
    }

    @Test
    fun saveFiveThings_newEntry_callsWriteService() {
        val fiveThings = FiveThings(date, listOf(Thing("01-22-2017", "one", 1),
                Thing("01-22-2017", "two", 2),
                Thing("01-22-2017", "three", 3),
                Thing("01-22-2017", "four", 4),
                Thing("01-22-2017", "five", 5)), false, false)
        val expected = Resource(Status.SUCCESS, "Date in database", dates)

        doAnswer {
            val callback: Callback<List<String>> = it.getArgument(0)
            callback.onResponse(datesCall, Response.success(dateStrings))
        }.`when`(datesCall).enqueue(any())

        val actual = LiveDataTestUtil.getValue(repository.saveFiveThings(token, fiveThings, mock()))

        verify(service, times(1)).writeFiveThings(any(), any())
        verify(datesCall, times(1)).enqueue(any())
        actual shouldEqual expected
    }

    @Test
    fun saveFiveThings_newEntry_callbackFails_createsErrorResource() {
        val fiveThings = FiveThings(date, listOf(Thing("01-22-2017", "one", 1),
                Thing("01-22-2017", "two", 2),
                Thing("01-22-2017", "three", 3),
                Thing("01-22-2017", "four", 4),
                Thing("01-22-2017", "five", 5)), true, false)
        val expected = Resource(Status.ERROR, "", null)

        doAnswer {
            val callback: Callback<List<String>> = it.getArgument(0)
            callback.onResponse(datesCall, Response.error(401, mock<ResponseBody>()))
        }.`when`(datesCall).enqueue(any())

        val actual = LiveDataTestUtil.getValue(repository.saveFiveThings(token, fiveThings, mock()))

        verify(service, times(1)).writeFiveThings(any(), any())
        verify(datesCall, times(1)).enqueue(any())
        actual shouldEqual expected
    }

    @Test
    fun saveFiveThings_newEntry_serviceFails_returnsErrorResource() {
        val fiveThings = FiveThings(date, listOf(Thing("01-22-2017", "one", 1),
                Thing("01-22-2017", "two", 2),
                Thing("01-22-2017", "three", 3),
                Thing("01-22-2017", "four", 4),
                Thing("01-22-2017", "five", 5)), true, false)
        val exception = IOException("Exception thrown")

        doAnswer {
            val callback: Callback<List<String>> = it.getArgument(0)
            callback.onFailure(datesCall, exception)
        }.`when`(datesCall).enqueue(any())

        LiveDataTestUtil.getValue(repository.saveFiveThings(token, fiveThings, mock()))

        verify(service, times(1)).writeFiveThings(any(), any())
        verify(datesCall, times(1)).enqueue(any())
    }

    //mockito and kotlin fix
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T
}