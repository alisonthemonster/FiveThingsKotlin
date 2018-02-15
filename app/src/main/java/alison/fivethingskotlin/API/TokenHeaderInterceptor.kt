package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.Token
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class TokenHeaderInterceptor(private val token: Token): Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        chain?.let {

            //if not a request that requires token
            if (chain.request().header("Authorization") == null) {
                Log.d("blerg", "not a request that requires a token!")
                return chain.proceed(chain.request())
            }
            Log.d("blerg", "adding the token to the request")
            val request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", token.tokenString)
                    //.addHeader("Accept","application/json")
                    .build()

            return chain.proceed(request)
        }
    }
}
