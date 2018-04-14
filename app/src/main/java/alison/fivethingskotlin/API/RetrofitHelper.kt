package alison.fivethingskotlin.API

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {

    companion object {

        var baseUrl = "https://fivethings-dev.herokuapp.com/api/"

        fun build(): Retrofit {

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()

            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build()
        }

        private val httpClient by lazy {

            val httpClient = OkHttpClient.Builder()

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addNetworkInterceptor(httpLoggingInterceptor)

//            val tokenAuthenticator = TokenAuthenticator()
//            httpClient.authenticator(tokenAuthenticator)

            httpClient.build()
        }
    }
}