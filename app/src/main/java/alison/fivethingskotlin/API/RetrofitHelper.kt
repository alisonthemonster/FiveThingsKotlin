package alison.fivethingskotlin.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    companion object {

        var baseUrl = "https://fivethings-dev.herokuapp.com/api/"

        fun build(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()
        }

        private val httpClient by lazy {

            val httpClient = OkHttpClient.Builder()

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addNetworkInterceptor(httpLoggingInterceptor)

            val tokenHeaderInterceptor = TokenHeaderInterceptor()
            val tokenAuthenticator = TokenAuthenticator()
            httpClient.addNetworkInterceptor(tokenHeaderInterceptor)
            httpClient.authenticator(tokenAuthenticator)

            httpClient.build()
        }
    }
}