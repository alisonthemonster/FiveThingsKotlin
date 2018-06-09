package alison.fivethingskotlin.repository

import alison.fivethingskotlin.API.AuthService
import alison.fivethingskotlin.API.repository.UserRepository
import alison.fivethingskotlin.API.repository.UserRepositoryImpl
import alison.fivethingskotlin.LiveDataTestUtil
import alison.fivethingskotlin.Models.CreateUserRequest
import alison.fivethingskotlin.Models.LogInUserRequest
import alison.fivethingskotlin.Models.Token
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.mock.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


@RunWith(JUnit4::class)
class UserRepositoryTest {

    private lateinit var service: AuthService
    private lateinit var repository: UserRepository
    private val createUserRequest = CreateUserRequest("Alison", "alison", "alison@alison.com")
    private val logInUserRequest = LogInUserRequest("alison@alison.com", "alison")
    private val token = Token("token")

    private val tokenCall = mock<Call<Token>>()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        service = mock()
        repository = UserRepositoryImpl(service)

        whenever(service.logInUser(any())).thenReturn(tokenCall)
        whenever(service.createUser(any())).thenReturn(tokenCall)
    }

    @Test
    fun createUser_callsUserServiceOnce() {
        repository.createUser(createUserRequest)

        verify(service, times(1)).createUser(any())
    }

    @Test
    fun createUser_serviceIsSuccessful_returnsToken() {

        Mockito.doAnswer {
            val callback: Callback<Token> = it.getArgument(0)
            callback.onResponse(tokenCall, Response.success(token))
        }.`when`(tokenCall).enqueue(any())

        LiveDataTestUtil.getValue(repository.createUser(createUserRequest))

        Mockito.verify(service, Mockito.times(1)).createUser(createUserRequest)
        Mockito.verify(tokenCall, Mockito.times(1)).enqueue(any())
    }

    @Test
    fun createUser_serviceFails_returnsErrorResource() {
        val exception = IOException("Exception thrown")

        Mockito.doAnswer {
            val callback: Callback<Token> = it.getArgument(0)
            callback.onFailure(tokenCall, exception)
        }.`when`(tokenCall).enqueue(any())

        LiveDataTestUtil.getValue(repository.createUser(createUserRequest))

        Mockito.verify(service, Mockito.times(1)).createUser(createUserRequest)
        Mockito.verify(tokenCall, Mockito.times(1)).enqueue(any())
    }

    @Test
    fun logIn_callsUserServiceOnce() {
        repository.logIn(logInUserRequest)

        verify(service, times(1)).logInUser(any())
    }

    @Test
    fun logIn_serviceIsSuccessful_returnsToken() {

        Mockito.doAnswer {
            val callback: Callback<Token> = it.getArgument(0)
            callback.onResponse(tokenCall, Response.success(token))
        }.`when`(tokenCall).enqueue(any())

        LiveDataTestUtil.getValue(repository.logIn(logInUserRequest))

        Mockito.verify(service, Mockito.times(1)).logInUser(logInUserRequest)
        Mockito.verify(tokenCall, Mockito.times(1)).enqueue(any())
    }

    @Test
    fun logIn_serviceFails_returnsErrorResource() {
        val exception = IOException("Exception thrown")

        Mockito.doAnswer {
            val callback: Callback<Token> = it.getArgument(0)
            callback.onFailure(tokenCall, exception)
        }.`when`(tokenCall).enqueue(any())

        LiveDataTestUtil.getValue(repository.logIn(logInUserRequest))

        Mockito.verify(service, Mockito.times(1)).logInUser(logInUserRequest)
        Mockito.verify(tokenCall, Mockito.times(1)).enqueue(any())
    }
}