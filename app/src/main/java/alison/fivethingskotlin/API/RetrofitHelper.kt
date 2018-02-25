package alison.fivethingskotlin.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.util.*


class RetrofitHelper {

    companion object {

        var baseUrl = "https://fivethings-dev.herokuapp.com/api/"

        fun build(): Retrofit {

            val gson = GsonBuilder()
                    .registerTypeAdapter(Date::class.java, JsonDeserializer<Date> { json, typeOfT, context ->
                        Date(json.asJsonPrimitive.asLong) })
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