package alison.fivethingskotlin

import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Models.UserBody
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.Util.restoreAuthState
import alison.fivethingskotlin.ViewModels.AuthViewModel
import alison.fivethingskotlin.databinding.ActivityPromoBinding
import android.app.PendingIntent
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_promo.*
import net.openid.appauth.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*
import android.support.design.widget.Snackbar
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.text.TextUtils
import org.json.JSONObject
import okhttp3.OkHttpClient
import android.os.AsyncTask
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthState
import okhttp3.Request
import org.jetbrains.anko.doAsync


class PromoActivity : AppCompatActivity() {

    private val SHARED_PREFERENCES_NAME = "AuthStatePreference"
    private val AUTH_STATE = "AUTH_STATE"
    private val USED_INTENT = "USED_INTENT"

    private lateinit var binding: ActivityPromoBinding
    private lateinit var viewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_promo)

        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        enablePostAuthorizationFlows()

        google_auth_button.setOnClickListener {
            val serviceConfiguration = AuthorizationServiceConfiguration(
                    Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                    Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
            )

            val clientId = "623073071257-p5f1lgvj78kp9qdeafcm85767f4q37qa.apps.googleusercontent.com"
            val redirectUri = Uri.parse("alison.fivethingskotlin:/oauth2redirect")
            //val redirectUri = Uri.parse("com.googleusercontent.apps.623073071257-p5f1lgvj78kp9qdeafcm85767f4q37qa:/oauth2redirect")
            val builder = AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    AuthorizationRequest.RESPONSE_TYPE_CODE,
                    redirectUri
            )
            builder.setScopes("profile email")
            val request = builder.build()

            val authorizationService = AuthorizationService(it.context)
            val action = "HANDLE_AUTHORIZATION_RESPONSE"
            val postAuthorizationIntent = Intent(action)
            val pendingIntent = PendingIntent.getActivity(it.context, request.hashCode(), postAuthorizationIntent, 0)
            authorizationService.performAuthorizationRequest(request, pendingIntent)

        }

    }

    override fun onStart() {
        super.onStart()
        checkIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        intent?.let {
            val action = intent.action
            when (action) {
                "HANDLE_AUTHORIZATION_RESPONSE" ->  {
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent)
                        intent.putExtra(USED_INTENT, true)
                    }
                }
            }
        }
    }



    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun handleAuthorizationResponse(intent: Intent) {

        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)
        val authState = AuthState(response, error)
        /**
         * The AuthState object created here is a convenient way to store details from the
         * authorization session. You can update it with the results of new OAuth responses,
         * and persist it to store the authorization session between app starts.
         */

        //exchange that authorization code for the refresh and access tokens
        //update the AuthState instance with that response
        response?.let {
            Log.i("blerg", String.format("Handled Authorization Response %s ", authState.toJsonString()))
            val service = AuthorizationService(this)
            service.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, exception ->
                if (exception != null) {
                    Log.w("blerg", "Token Exchange failed", exception)
                } else {
                    if (tokenResponse != null) {
                        authState.update(tokenResponse, exception)
                        persistAuthState(authState)
                        Log.i("blerg", String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken))
                    }
                }
            }
        }
    }

    private fun persistAuthState(authState: AuthState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.toJsonString())
                .apply()

        getUserData(authState)
        enablePostAuthorizationFlows()
    }

    private fun getUserData(authState: AuthState) {
        //get user details for nagu
        val authorizationService = AuthorizationService(this)
        authState.performActionWithFreshTokens(authorizationService, { accessToken, idToken, ex ->

        })

        authState.performActionWithFreshTokens(authorizationService, AuthState.AuthStateAction { accessToken, idToken, exception ->


            doAsync {
                val client = OkHttpClient()
                val request = Request.Builder()
                        .url("https://www.googleapis.com/oauth2/v3/userinfo")
                        .addHeader("Authorization", String.format("Bearer %s", accessToken))
                        .build()

                try {
                    val response = client.newCall(request).execute()
                    val userInfo = JSONObject(response.body()?.string())
                    val email = userInfo.optString("email", null)
                    val fullName = userInfo.optString("name", null)
                    val givenName = userInfo.optString("given_name", null)
                    val familyName = userInfo.optString("family_name", null)
                    val userBody = UserBody(email, fullName, givenName, familyName)

                    postUserDataToDatabase(userBody)
                } catch (exception: Exception) {
                    Log.w("blerg", exception)
                }
            }
        })
    }

    private fun postUserDataToDatabase(userBody: UserBody) {
        //give user details to nagu

        Log.d("blerg", "in post user data to database")

//        viewModel.postUserBody(userBody).observe(this, Observer<Resource<Token>> { token ->
//            token?.let{
//                if (token.)
//            }
//        })
    }

    private fun enablePostAuthorizationFlows() {
        val mAuthState = restoreAuthState(this)

        mAuthState?.let {
            if (mAuthState.isAuthorized) {
                //we are logged in!!
                Log.d("blerg", "yo yo yo we in bitches")
                val intent = Intent(applicationContext, ContainerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                //we need to log in!
                Log.d("blerg", "bitches gotta log in")
            }
        }
    }

}
