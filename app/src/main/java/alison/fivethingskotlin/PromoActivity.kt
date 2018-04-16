package alison.fivethingskotlin

import alison.fivethingskotlin.Util.Constants.ACCOUNT_TYPE
import alison.fivethingskotlin.Util.Constants.AUTH_TOKEN_TYPE
import alison.fivethingskotlin.databinding.ActivityPromoBinding
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_promo.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class PromoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPromoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_promo)
        binding.loading = true

        val accountManager = AccountManager.get(this)
        val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE)
        if (!accounts.isEmpty()) {
            //an account was found on device, try to find a token
            val account = accounts[0]
            accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, Bundle(), this, OnTokenAcquired(), null) //TODO add onError handler instead of null
        } else {
            binding.loading = false
        }

        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private inner class OnTokenAcquired: AccountManagerCallback<Bundle> {

        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            if (bundle.getString(AccountManager.KEY_INTENT) != null) {
                //no token found on device, show log in screen to force manual entry
                val intent = Intent(applicationContext, LogInActivity::class.java)
                startActivity(intent)
                return
            }
            //A token was found and we can open the app
            val intent = Intent(applicationContext, ContainerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

}
