package alison.fivethingskotlin.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.API.repository.FiveThingsRepository
import alison.fivethingskotlin.API.repository.FiveThingsRepositoryImpl
import alison.fivethingskotlin.Models.FiveThings
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import retrofit2.Call

@RunWith(JUnit4::class)
class FiveThingsRepositoryTest {

    private lateinit var service: FiveThingsService
    private lateinit var repository: FiveThingsRepository

    private val mockedFiveThings = mock<Call<FiveThings>>()

    @Before
    fun setup() {
        service = mock()
        repository = FiveThingsRepositoryImpl(service)

        `when`(service.getFiveThings(any(), any())).thenReturn(mockedFiveThings)
    }
}