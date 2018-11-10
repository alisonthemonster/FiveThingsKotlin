package alison.fivethingskotlin

import alison.fivethingskotlin.adapter.IntroAdapter
import alison.fivethingskotlin.databinding.ActivityPromoBinding
import alison.fivethingskotlin.util.AUTH_STATE
import alison.fivethingskotlin.util.SHARED_PREFERENCES_NAME
import alison.fivethingskotlin.util.restoreAuthState
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_promo.*
import net.openid.appauth.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class PromoActivity : AppCompatActivity() {

    companion object {
        const val USED_INTENT = "USED_INTENT"
        const val HANDLE_AUTHORIZATION_RESPONSE = "HANDLE_AUTHORIZATION_RESPONSE"
    }

    private lateinit var binding: ActivityPromoBinding
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setContentView(R.layout.activity_promo)

        binding = ActivityPromoBinding.inflate(layoutInflater)

        binding.loading = true

        enablePostAuthorizationFlows()

        google_auth_button.setOnClickListener { startAuthorizationRequest(it) }

        readFromIntent()

        promo_view_pager.adapter = IntroAdapter(supportFragmentManager)
    }

    override fun onStart() {
        super.onStart()
        checkIntent(intent)
    }

    private fun readFromIntent() {
        if (intent.extras != null) {
            val havingAuthTrouble = intent.getBooleanExtra("AUTH_TROUBLE", false)
            auth_problems.visibility = if (havingAuthTrouble) View.VISIBLE else View.GONE
            val havingNetworkTrouble = intent.getBooleanExtra("NETWORK_TROUBLE", false)
            network_problems.visibility = if (havingNetworkTrouble) View.VISIBLE else View.GONE

            val cameFromNotification = intent.getBooleanExtra("fromNotification", false)
            if (cameFromNotification) mFirebaseAnalytics.logEvent("cameFromNotification", null)
        }
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
        val action = HANDLE_AUTHORIZATION_RESPONSE
        val postAuthorizationIntent = Intent(view.context, PromoActivity::class.java)
        postAuthorizationIntent.action = action
        val pendingIntent = PendingIntent.getActivity(view.context, request.hashCode(), postAuthorizationIntent, 0)
        authorizationService.performAuthorizationRequest(request, pendingIntent)
    }

    override fun onNewIntent(intent: Intent) {
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        intent?.let {
            val action = intent.action
            when (action) {
                HANDLE_AUTHORIZATION_RESPONSE -> {
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
                    Crashlytics.logException(exception)
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

        if (mAuthState == null) {
            binding.loading = false
        } else {
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

}
