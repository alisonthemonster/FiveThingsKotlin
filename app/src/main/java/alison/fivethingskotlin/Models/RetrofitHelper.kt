package alison.fivethingskotlin.Models

import retrofit2.Retrofit

/**
 * Created by Alison on 1/30/18.
 */

class RetrofitHelper {
    companion object {

        var baseUrl = "https://fivethings-dev.herokuapp.com/api"

        fun build(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .build()

        }
    }
}