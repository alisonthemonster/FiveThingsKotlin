package alison.fivethingskotlin.API

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(): Authenticator {

    //when a response is 401 Not Authorised this is called
    override fun authenticate(route: Route?, response: Response?): Request? {
        return null

        //TODO come back to this one day?
//            val account = accountManager.getAccountsByType("FIVE_THINGS")[0]
//            val oldToken = accountManager.peekAuthToken(account, "full_access")
//            oldToken?.let {
//                //token is invalid
//                accountManager.invalidateAuthToken("FIVE_THINGS", oldToken)
//            }
//
//            // Refresh your access_token using a synchronous api request
//            val newToken = accountManager.blockingGetAuthToken(account, "full_access", false)
//
//            // Add new header to rejected request and retry it
//            return response.request()
//                    .newBuilder()
//                    .header("Authorization", newToken)
//                    .build()

    }
}

