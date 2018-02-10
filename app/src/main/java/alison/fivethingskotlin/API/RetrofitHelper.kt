package alison.fivethingskotlin.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    companion object {

        var baseUrl = "https://fivethings-dev.herokuapp.com/api/"

        fun build(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

        }
    }
}