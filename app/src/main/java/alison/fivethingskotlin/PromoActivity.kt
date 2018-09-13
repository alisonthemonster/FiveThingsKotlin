package alison.fivethingskotlin

import alison.fivethingskotlin.databinding.ActivityPromoBinding
import alison.fivethingskotlin.util.AUTH_STATE
import alison.fivethingskotlin.util.SHARED_PREFERENCES_NAME
import alison.fivethingskotlin.util.restoreAuthState
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_promo.*
import net.hockeyapp.android.UpdateManager
import net.openid.appauth.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import net.hockeyapp.android.CrashManager


class PromoActivity : AppCompatActivity() {

    private val USED_INTENT = "USED_INTENT"
    private lateinit var binding: ActivityPromoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enablePostAuthorizationFlows()

        setContentView(R.layout.activity_promo)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_promo)
        binding.loading = true

        google_auth_button.setOnClickListener { startAuthorizationRequest(it) }

        checkForUpdates()
    }

    override fun onResume() {
        super.onResume()
        checkForCrashes()
    }

    override fun onPause() {
        super.onPause()
        unregisterManagers()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterManagers()
    }

    override fun onStart() {
        super.onStart()
        checkIntent(intent)
    }

    private fun startAuthorizationRequest(view: View) {
        binding.loading = true
        val serviceConfiguration = AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        )

        val clientId = "142866886118-1hna99dvja9ssjl5mdbms1bj6ctmo55j.apps.googleusercontent.com"
        val redirectUri = Uri.parse("alison.fivethingskotlin:/oauth2redirect")
        val builder = AuthorizationRequest.Builder(
                serviceConfiguration,
                clientId,
                ResponseTypeValues.CODE,
                redirectUri
        )
        builder.setScopes("profile email")
        val request = builder.build()

        val authorizationService = AuthorizationService(view.context)
        val action = "HANDLE_AUTHORIZATION_RESPONSE"
        val postAuthorizationIntent = Intent(view.context, PromoActivity::class.java)
        postAuthorizationIntent.action = action
        val pendingIntent = PendingIntent.getActivity(view.context, request.hashCode(), postAuthorizationIntent, 0)
        authorizationService.performAuthorizationRequest(request, pendingIntent)
    }

    override fun onNewIntent(intent: Intent) {
        checkIntent(intent)
        //blerg
    }

    private fun checkIntent(intent: Intent?) {
        intent?.let {
            val action = intent.action
            when (action) {
                "HANDLE_AUTHORIZATION_RESPONSE" -> {
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent)
                        intent.putExtra(USED_INTENT, true)
                    }
                }
                else -> {
                    binding.loading = false
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun handleAuthorizationResponse(intent: Intent) {
        binding.loading = true

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
            val service = AuthorizationService(this)
            service.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, exception ->
                if (exception != null) {
                    binding.loading = false
                    //Token Exchange failed
                } else {
                    if (tokenResponse != null) {
                        authState.update(tokenResponse, exception)
                        persistAuthState(authState)
                    }
                }
            }
        }
    }

    private fun persistAuthState(authState: AuthState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.jsonSerializeString())
                .apply()

        enablePostAuthorizationFlows()
    }

    private fun enablePostAuthorizationFlows() {
        val mAuthState = restoreAuthState(this)


        mAuthState?.let {
            if (mAuthState.isAuthorized) {
                //we are logged in!!
                val intent = Intent(applicationContext, ContainerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                //TODO handle this animation more smoothly
            } else {
                binding.loading = false
                //we need to log in!
            }
        }
    }

    private fun checkForCrashes() {
        CrashManager.register(this)
    }

    private fun checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this)
    }

    private fun unregisterManagers() {
        UpdateManager.unregister()
    }

}
