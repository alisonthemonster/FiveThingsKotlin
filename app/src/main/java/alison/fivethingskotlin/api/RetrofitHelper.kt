package alison.fivethingskotlin.api

import alison.fivethingskotlin.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {

    companion object {

        var baseUrl = BuildConfig.BASE_URL

        fun build(): Retrofit {

            val gson = GsonBuilder()
                    .create()

            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build()
        }

        private val httpClient by lazy {

            val httpClient = OkHttpClient.Builder()

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addNetworkInterceptor(httpLoggingInterceptor)

            httpClient.build()
        }
    }

}